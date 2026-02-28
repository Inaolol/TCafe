import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Customer.java
 * Represents a customer placing orders at the café.
 *
 * ── THREAD CREATION METHOD 1: implements Runnable ────────────────────────────
 * Customer implements Runnable — meaning it defines a task (run()) that can be
 * given to any Thread object to execute.
 *
 * In Main:  Thread t = new Thread(new Customer(...));
 *           t.start();
 *
 * WHY Runnable is PREFERRED over extending Thread:
 *   • Java has single inheritance — a class extending Thread cannot extend anything else
 *   • Runnable separates the TASK (what to do) from the THREAD (how to run it)
 *   • Better design: Customer knows how to place orders; it doesn't need to know
 *     about thread management
 *
 * ── THREAD LIFECYCLE DEMONSTRATED ────────────────────────────────────────────
 *   NEW          → when 'new Thread(new Customer(...))' is called in Main
 *   RUNNABLE     → when 't.start()' is called — thread is ready, waiting for CPU
 *   RUNNING      → while executing the for-loop in run()
 *   TIMED_WAITING → during Thread.sleep(...) between orders
 *   WAITING      → when orderQueue.put() blocks because the queue is full
 *   TERMINATED   → when run() returns after all orders are placed
 *
 * ── SHARED MEMORY ─────────────────────────────────────────────────────────────
 * orderQueue is a reference to the SAME object in heap memory shared with all
 * Worker threads. Customers PRODUCE orders; Workers CONSUME them.
 * This is the classic Producer-Consumer thread pattern.
 */
public class Customer implements Runnable {

    private final int customerId;
    private final BlockingQueue<Order> orderQueue; // SHARED with all Workers
    private final String[] ingredients;
    private final int ordersToPlace;
    private final Random rnd;

    public Customer(int customerId,
                    BlockingQueue<Order> orderQueue,
                    String[] ingredients,
                    int ordersToPlace,
                    long seed) {
        this.customerId   = customerId;
        this.orderQueue   = orderQueue;
        this.ingredients  = ingredients;
        this.ordersToPlace = ordersToPlace;
        this.rnd          = new Random(seed + customerId); // unique sequence per customer
    }

    /**
     * run() is the thread's task.
     * IMPORTANT: never call run() directly — that executes it on the current
     * thread, no new thread is created. Always call start() on the Thread wrapper.
     */
    @Override
    public void run() {
        // Give the thread a meaningful name for easier debugging
        Thread.currentThread().setName("Customer-" + customerId);

        // STATE: RUNNING — actively placing orders
        System.out.println(Thread.currentThread().getName()
                + " has arrived. Will place " + ordersToPlace + " order(s).");

        for (int i = 1; i <= ordersToPlace; i++) {
            String ingredient = ingredients[rnd.nextInt(ingredients.length)];
            int amount = 1 + rnd.nextInt(2); // randomly order 1 or 2 units
            Order order = new Order(customerId, ingredient, amount);

            try {
                // STATE: TIMED_WAITING — sleeping between orders (simulates arrival gap)
                Thread.sleep(100 + rnd.nextInt(300));

                // STATE: WAITING — put() BLOCKS if queue is full
                // Thread is automatically suspended until a Worker frees space
                orderQueue.put(order);

                // STATE: RUNNING — resumed after put() succeeded
                System.out.println(Thread.currentThread().getName()
                        + " placed " + order);

            } catch (InterruptedException e) {
                // Thread was interrupted during sleep or put() — clean shutdown
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName()
                        + " was interrupted. Leaving café early.");
                return;
            }
        }

        System.out.println(Thread.currentThread().getName()
                + " finished ordering. Leaving café.");
        // STATE: TERMINATED — run() returns, thread ends permanently
    }
}
