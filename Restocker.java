import java.util.Map;

/**
 * Restocker.java
 * A background thread that periodically monitors and restocks inventory.
 *
 * ── THREAD LIFECYCLE DEMONSTRATED ────────────────────────────────────────────
 *   NEW           → when 'new Restocker(...)' is called in Main
 *   RUNNABLE      → when 'restocker.start()' is called
 *   RUNNING       → while checking each ingredient in the for-loop
 *   TIMED_WAITING → during Thread.sleep(500) between checks
 *   TERMINATED    → when shutdown() is called and run() returns
 *
 * ── VOLATILE KEYWORD ─────────────────────────────────────────────────────────
 * The 'running' flag is declared volatile.
 *
 * WHY this is necessary:
 * Each CPU core has its own cache. Without volatile, when Main calls shutdown()
 * and sets running = false, the Restocker thread might keep reading its cached
 * copy of 'running' which still shows true — the thread never stops.
 *
 * 'volatile' forces all reads and writes to go directly to MAIN MEMORY,
 * ensuring every thread always sees the most up-to-date value.
 *
 * Note: volatile is NOT a replacement for synchronized. It only guarantees
 * visibility (reading the latest value), not atomicity (indivisible operations).
 * For compound operations like check-then-act, synchronized is still required.
 *
 * ── SHARED RESOURCE ───────────────────────────────────────────────────────────
 * Restocker and Workers both access the same Inventory object.
 * Restocker calls restock() and isLow() — both synchronized inside Inventory,
 * so there is no risk of corrupting stock data even if a Worker is mid-deduction.
 */
public class Restocker extends Thread {

    private final Inventory inventory;
    private final String[]  ingredients;
    private final int       restockAmount;

    // 'volatile' ensures Main's shutdown() call is immediately visible here
    private volatile boolean running = true;

    public Restocker(Inventory inventory, String[] ingredients, int restockAmount) {
        super("Restocker");
        this.inventory     = inventory;
        this.ingredients   = ingredients;
        this.restockAmount = restockAmount;
    }

    /**
     * Called by Main to stop this thread gracefully.
     * Sets running = false (visible immediately due to volatile),
     * then interrupts the thread to wake it from sleep right away.
     */
    public void shutdown() {
        running = false;  // signal the while loop to exit
        interrupt();      // wake from Thread.sleep() immediately
    }

    @Override
    public void run() {
        System.out.println(getName() + " started. Monitoring inventory every 500ms.");

        // STATE: RUNNING — monitoring loop
        while (running) {
            try {
                // STATE: TIMED_WAITING — sleeping between checks
                Thread.sleep(500);

                // STATE: RUNNING — woke up, checking each ingredient
                for (String ingredient : ingredients) {
                    if (inventory.isLow(ingredient)) {
                        // Restock — synchronized inside Inventory, safe with Workers
                        inventory.restock(ingredient, restockAmount);
                    }
                }

                // Print a snapshot so we can watch inventory change over time
                Map<String, Integer> snap = inventory.snapshot();
                System.out.println(getName() + " snapshot: " + snap);

            } catch (InterruptedException e) {
                // Woken by shutdown() — check flag and exit if stopping
                if (!running) {
                    System.out.println(getName() + " received shutdown signal.");
                    return;
                }
                // Otherwise spurious interrupt — continue monitoring
            }
        }

        System.out.println(getName() + " monitoring stopped.");
        // STATE: TERMINATED
    }
}
