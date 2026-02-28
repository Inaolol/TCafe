import java.util.concurrent.BlockingQueue;

/**
 * Worker.java
 * Represents a barista processing customer orders.
 *
 * ── THREAD CREATION METHOD 2: extends Thread ─────────────────────────────────
 * Worker extends Thread directly. This is the simpler approach when the class
 * doesn't need to extend anything else.
 *
 * In Main:  Worker w = new Worker(1, orderQueue, inventory);
 *           w.start();
 *
 * ── THREAD LIFECYCLE DEMONSTRATED ────────────────────────────────────────────
 *   NEW          → when 'new Worker(...)' is called in Main
 *   RUNNABLE     → when 'w.start()' is called
 *   RUNNING      → while processing an order (tryConsume, print statements)
 *   WAITING      → when orderQueue.take() blocks — no orders available
 *                  (thread is suspended by JVM, CPU is freed for other threads)
 *   TIMED_WAITING → during Thread.sleep(150) when re-queuing an out-of-stock order
 *   TERMINATED   → when POISON_PILL is received and run() returns
 *
 * ── BLOCKING QUEUE AS SYNCHRONIZATION ────────────────────────────────────────
 * orderQueue.take() does two things automatically:
 *   1. BLOCKS (WAITING state) when queue is empty — no busy-waiting
 *   2. WAKES UP when a Customer adds a new order
 * No manual wait()/notify() required — BlockingQueue handles it internally.
 *
 * ── THREAD COORDINATION: PRODUCER-CONSUMER ───────────────────────────────────
 * Customers PRODUCE orders → queue → Workers CONSUME orders
 * The BlockingQueue is the coordination mechanism between the two thread types.
 * This pattern safely decouples production speed from consumption speed.
 */
public class Worker extends Thread {

    private final BlockingQueue<Order> orderQueue; // SHARED with all Customers
    private final Inventory inventory;              // SHARED with all Workers and Restocker

    public Worker(int workerId, BlockingQueue<Order> orderQueue, Inventory inventory) {
        super("Worker-" + workerId); // sets thread name — visible in getState() output
        this.orderQueue = orderQueue;
        this.inventory  = inventory;
    }

    @Override
    public void run() {
        // STATE: RUNNING — worker is active and ready
        System.out.println(getName() + " started. Ready to serve customers.");

        while (true) {
            try {
                System.out.println(getName() + " waiting for next order...");

                // STATE: WAITING — take() blocks until an order is available
                // JVM suspends this thread; CPU is used by other threads
                Order order = orderQueue.take();

                // STATE: RUNNING — order received, now processing
                if (order.isPoisonPill()) {
                    // POISON PILL received — time to terminate
                    System.out.println(getName() + " received STOP signal. Ending shift.");
                    break; // exits the while loop → run() returns → TERMINATED
                }

                System.out.println(getName() + " processing " + order);

                // Access SHARED RESOURCE — synchronized inside Inventory
                boolean ok = inventory.tryConsume(order.getIngredient(), order.getAmount());

                if (ok) {
                    System.out.println(getName() + " ✓ served Customer-"
                            + order.getCustomerId()
                            + " (" + order.getIngredient()
                            + " x" + order.getAmount() + ")");
                } else {
                    // Out of stock — re-queue the order to be retried after restocking
                    System.out.println(getName() + " ✗ cannot serve Customer-"
                            + order.getCustomerId()
                            + " — " + order.getIngredient() + " out of stock. Re-queuing.");
                    orderQueue.put(order);

                    // STATE: TIMED_WAITING — short pause to avoid tight re-queue loop
                    Thread.sleep(150);
                }

            } catch (InterruptedException e) {
                // Thread interrupted externally — set flag and exit cleanly
                interrupt();
                System.out.println(getName() + " interrupted. Terminating.");
                return;
            }
        }

        System.out.println(getName() + " shift ended.");
        // STATE: TERMINATED — run() returns
    }
}
