
/**
 * Order.java
 * Represents a single customer order placed at the café.
 *
 * ── THREAD CONCEPT: IMMUTABLE SHARED OBJECT ──────────────────────────────────
 * Order objects are created by Customer threads and consumed by Worker threads.
 * All fields are declared 'final' — meaning they cannot be changed after the
 * object is created. This makes Order objects IMMUTABLE.
 *
 * Immutability is one of the simplest thread-safety strategies:
 * if an object can never change, multiple threads can read it simultaneously
 * with zero risk of a race condition — no synchronization needed.
 *
 * ── POISON PILL PATTERN ───────────────────────────────────────────────────────
 * POISON_PILL is a special Order used as a shutdown signal.
 * When Main wants Workers to stop, it puts one POISON_PILL per Worker into
 * the queue. When a Worker picks it up, it knows to terminate cleanly.
 * This is the standard Java pattern for stopping consumer threads gracefully.
 */
public class Order {

    private final int    customerId;   // which customer placed this order
    private final String ingredient;   // what ingredient is requested
    private final int    amount;       // how many units

    /** Special signal order: tells Workers to stop. customerId = -1 is the sentinel. */
    public static final Order POISON_PILL = new Order(-1, "STOP", 0);

    public Order(int customerId, String ingredient, int amount) {
        this.customerId = customerId;
        this.ingredient = ingredient;
        this.amount     = amount;
    }

    public int    getCustomerId() { return customerId; }
    public String getIngredient() { return ingredient; }
    public int    getAmount()     { return amount;     }

    /** Returns true if this order is the shutdown signal. */
    public boolean isPoisonPill() { return customerId == -1; }

    @Override
    public String toString() {
        return "Order{customer=" + customerId
             + ", ingredient='" + ingredient + "'"
             + ", amount=" + amount + "}";
    }
}
