import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Main.java
 * Entry point — creates all threads and coordinates the simulation.
 *
 * ── WHAT THIS FILE DEMONSTRATES ──────────────────────────────────────────────
 * This is the "hub" of the simulation. Reading this file gives a full picture
 * of how threads are created, started, synchronized, and terminated.
 *
 * 1. SHARED MEMORY
 *    orderQueue and inventory are created ONCE here and passed to every thread.
 *    All threads hold a reference to the same heap objects.
 *    This is Java's shared-memory concurrency model:
 *    threads share the process HEAP but each has its own STACK.
 *
 * 2. TWO THREAD CREATION STYLES (side by side)
 *    Workers use  : extends Thread  → Worker w = new Worker(...); w.start();
 *    Customers use: implements Runnable → new Thread(new Customer(...)).start();
 *
 * 3. THREAD LIFECYCLE PRINTED LIVE
 *    w.getState() shows the actual Java thread state at runtime:
 *    NEW → RUNNABLE → ... → TERMINATED
 *    This is the lifecycle diagram made visible in the terminal.
 *
 * 4. start() vs run()
 *    start() is always used — never run().
 *    start() tells the JVM to create a new thread of execution.
 *    run() called directly would just execute on the current (main) thread.
 *
 * 5. join()
 *    join() makes the calling thread WAIT until the target thread TERMINATES.
 *    Without join(), Main would reach shutdown code before all orders are served.
 *
 * 6. GRACEFUL SHUTDOWN
 *    Step 1: Wait for all Customers to finish (join on customer threads)
 *    Step 2: Send one POISON_PILL per Worker into the queue
 *    Step 3: join() on Workers — wait for them to process final orders and exit
 *    Step 4: Call restocker.shutdown() — sets volatile flag + interrupts sleep
 *    Step 5: join() on Restocker
 *    Result: every thread terminates cleanly, no data is lost
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("TCafe — Multithreaded Simulation\n");

        // ── CONFIGURATION ────────────────────────────────────────────────────
        final String[] INGREDIENTS     = {"Milk", "Chocolate", "Vanilla", "Cups"};
        final int      CUSTOMER_COUNT  = 6;
        final int      ORDERS_EACH     = 3;
        final int      WORKER_COUNT    = 2;
        final int      LOW_THRESHOLD   = 2;   // restock when stock ≤ this
        final int      RESTOCK_AMOUNT  = 5;   // units added per restock

        // ── SHARED MEMORY (heap objects shared by all threads) ────────────────
        BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(50);

        Inventory inventory = new Inventory(LOW_THRESHOLD);
        inventory.addIngredient("Milk",      6);
        inventory.addIngredient("Chocolate", 4);
        inventory.addIngredient("Vanilla",   4);
        inventory.addIngredient("Cups",      6);

        System.out.println("── Initial inventory: " + inventory.snapshot());
        System.out.println("── Starting " + WORKER_COUNT + " workers, "
                + CUSTOMER_COUNT + " customers, 1 restocker\n");

        // ── CREATE & START WORKERS (extends Thread) ───────────────────────────
        List<Worker> workers = new ArrayList<>();
        for (int i = 1; i <= WORKER_COUNT; i++) {
            Worker w = new Worker(i, orderQueue, inventory);
            workers.add(w);

            // Print state BEFORE start — shows NEW state
            System.out.println("Main: " + w.getName() + " state before start → " + w.getState());
            w.start(); // STATE: NEW → RUNNABLE
            System.out.println("Main: " + w.getName() + " state after  start → " + w.getState());
        }

        // ── CREATE & START RESTOCKER ───────────────────────────────────────────
        Restocker restocker = new Restocker(inventory, INGREDIENTS, RESTOCK_AMOUNT);
        System.out.println("Main: Restocker state before start → " + restocker.getState());
        restocker.start();
        System.out.println("Main: Restocker state after  start → " + restocker.getState() + "\n");

        // ── CREATE & START CUSTOMERS (implements Runnable) ────────────────────
        List<Thread> customerThreads = new ArrayList<>();
        for (int i = 1; i <= CUSTOMER_COUNT; i++) {
            Thread t = new Thread(new Customer(i, orderQueue, INGREDIENTS, ORDERS_EACH, 123));
            customerThreads.add(t);

            // Print state BEFORE start — shows NEW state
            System.out.println("Main: Customer-" + i + " state before start → " + t.getState());
            t.start(); // STATE: NEW → RUNNABLE
        }
        System.out.println();

        // ── WAIT FOR ALL CUSTOMERS TO FINISH (join) ───────────────────────────
        // Main thread BLOCKS here until every customer thread TERMINATES
        for (Thread t : customerThreads) {
            t.join();
        }
        System.out.println("\nMain: All customers have finished placing orders.");

        // ── GRACEFUL WORKER SHUTDOWN ───────────────────────────────────────────
        // One POISON_PILL per Worker — each Worker consumes one and terminates
        System.out.println("Main: Sending " + WORKER_COUNT + " STOP signal(s) to workers...");
        for (int i = 0; i < WORKER_COUNT; i++) {
            orderQueue.put(Order.POISON_PILL);
        }

        // Wait for Workers to finish their last orders and terminate
        for (Worker w : workers) {
            w.join();
            // Print state AFTER join — shows TERMINATED state
            System.out.println("Main: " + w.getName() + " final state → " + w.getState());
        }

        // ── GRACEFUL RESTOCKER SHUTDOWN ────────────────────────────────────────
        restocker.shutdown(); // sets volatile running=false + interrupts sleep
        restocker.join();
        System.out.println("Main: Restocker final state → " + restocker.getState());

        // ── FINAL SUMMARY ──────────────────────────────────────────────────────
        System.out.println("\nTCafe closed. All threads terminated.");
        System.out.println("Final inventory: " + inventory.snapshot());
    }
}
