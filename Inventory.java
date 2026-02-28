import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Inventory.java
 * Represents the café's shared ingredient stock.
 *
 * ── THREAD CONCEPT: CRITICAL SECTION & SYNCHRONIZATION ───────────────────────
 * This object is a SHARED RESOURCE — multiple threads access it at the same time:
 *   • Worker threads call tryConsume() when processing an order
 *   • Restocker thread calls restock() when stock is low
 *   • Main thread calls addIngredient() during setup
 *
 * Without 'synchronized', a RACE CONDITION occurs:
 *   Thread A (Worker-1) reads Milk stock = 2
 *   Thread B (Worker-2) reads Milk stock = 2   ← reads the same value
 *   Thread A deducts 2  → writes 0
 *   Thread B deducts 2  → writes -2            ← WRONG: stock goes negative
 *
 * The 'synchronized' keyword creates a CRITICAL SECTION:
 * only ONE thread can execute any synchronized method on this object at a time.
 * Thread B must WAIT until Thread A fully completes its deduction.
 *
 * ── SHARED MEMORY ─────────────────────────────────────────────────────────────
 * The 'stock' map lives on the JVM HEAP — shared by all threads in the process.
 * Each thread has its own STACK (local variables, method calls) but they all
 * access this same heap object. This is Java's shared-memory concurrency model.
 */
public class Inventory {

    // THE SHARED RESOURCE — all threads read and write this map
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private final int lowStockThreshold;

    public Inventory(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    /**
     * Add an ingredient during setup (called from Main before threads start).
     * synchronized: safe even if called concurrently.
     */
    public synchronized void addIngredient(String name, int initialAmount) {
        stock.put(name, initialAmount);
    }

    /**
     * Attempt to consume 'amount' units of 'name'.
     * Returns true if successful, false if insufficient stock.
     *
     * CRITICAL SECTION: the check-then-act (read stock → deduct) must be atomic.
     * Without synchronized, two Workers could both pass the check and both deduct.
     */
    public synchronized boolean tryConsume(String name, int amount) {
        int current = stock.getOrDefault(name, 0);
        if (current >= amount) {
            stock.put(name, current - amount);
            System.out.printf("    [Inventory] %-12s consumed %d unit(s). Remaining: %d%n",
                              name, amount, current - amount);
            return true;
        }
        System.out.printf("    [Inventory] %-12s OUT OF STOCK (requested %d, have %d)%n",
                          name, amount, current);
        return false;
    }

    /**
     * Restock an ingredient.
     * synchronized: prevents race with tryConsume().
     */
    public synchronized void restock(String name, int amount) {
        int current = stock.getOrDefault(name, 0);
        stock.put(name, current + amount);
        System.out.printf("    [Inventory] %-12s restocked +%d. Now: %d%n",
                          name, amount, current + amount);
    }

    /**
     * Check if an ingredient is running low.
     * synchronized: reads the shared map safely.
     */
    public synchronized boolean isLow(String name) {
        return stock.getOrDefault(name, 0) <= lowStockThreshold;
    }

    /**
     * Returns a snapshot (copy) of the current stock.
     * Returns a COPY so the caller sees a consistent state
     * even if other threads modify stock after this call.
     */
    public synchronized Map<String, Integer> snapshot() {
        return new LinkedHashMap<>(stock);
    }
}
