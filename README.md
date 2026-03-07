> 
> 
> 
> ## What This Project Demonstrates
> 
> This simulation uses a café scenario to demonstrate core thread concepts in Java.
> 
> | Thread Concept | Where It Appears |
> | --- | --- |
> | Thread creation (`extends Thread`) | `Worker.java` |
> | Thread creation (`implements Runnable`) | `Customer.java` |
> | Thread lifecycle states (NEW → TERMINATED) | `Main.java` — printed live via `getState()` |
> | Shared memory (heap) | `orderQueue` and `inventory` in `Main.java` |
> | Race condition risk | `Inventory.java` — explained in comments |
> | Critical section (`synchronized`) | All methods in `Inventory.java` |
> | Producer-Consumer pattern | `Customer` → `BlockingQueue` → `Worker` |
> | `BlockingQueue` coordination | `Customer.java` `put()` / `Worker.java` `take()` |
> | `start()` vs `run()` | `Main.java` — always `start()`, never `run()` |
> | `join()` — waiting for termination | `Main.java` — customers, workers, restocker |
> | Poison pill shutdown | `Order.POISON_PILL` + `Worker.java` |
> | `volatile` keyword | `Restocker.java` — `running` flag |
> | Graceful thread shutdown | `Restocker.shutdown()` in `Main.java` |
> 
> ---
> 
> ## Full console output
> 
> ```java
> TCafe — Multithreaded Simulation
> 
> ── Initial inventory: {Milk=6, Chocolate=4, Vanilla=4, Cups=6}
> ── Starting 2 workers, 6 customers, 1 restocker
> 
> Main: Worker-1 state before start → NEW
> Main: Worker-1 state after  start → RUNNABLE
> Main: Worker-2 state before start → NEW
> Main: Worker-2 state after  start → RUNNABLE
> Main: Restocker state before start → NEW
> Worker-2 started. Ready to serve customers.
> Main: Restocker state after  start → RUNNABLE
> 
> Worker-1 started. Ready to serve customers.
> Worker-2 waiting for next order...
> Worker-1 waiting for next order...
> Restocker started. Monitoring inventory every 500ms.
> Main: Customer-1 state before start → NEW
> Main: Customer-2 state before start → NEW
> Main: Customer-3 state before start → NEW
> Main: Customer-4 state before start → NEW
> Main: Customer-5 state before start → NEW
> Customer-3 has arrived. Will place 3 order(s).
> Customer-2 has arrived. Will place 3 order(s).
> Main: Customer-6 state before start → NEW
> Customer-5 has arrived. Will place 3 order(s).
> Customer-6 has arrived. Will place 3 order(s).
> Customer-4 has arrived. Will place 3 order(s).
> Customer-1 has arrived. Will place 3 order(s).
> 
> Customer-5 placed Order{customer=5, ingredient='Vanilla', amount=1}
> Worker-2 processing Order{customer=5, ingredient='Vanilla', amount=1}
> Worker-1 processing Order{customer=4, ingredient='Vanilla', amount=1}
>     [Inventory] Vanilla      consumed 1 unit(s). Remaining: 3
> Customer-4 placed Order{customer=4, ingredient='Vanilla', amount=1}
>     [Inventory] Vanilla      consumed 1 unit(s). Remaining: 2
> Customer-6 placed Order{customer=6, ingredient='Vanilla', amount=2}
> Customer-1 placed Order{customer=1, ingredient='Vanilla', amount=2}
> Worker-2 ✓ served Customer-5 (Vanilla x1)
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=6, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      consumed 2 unit(s). Remaining: 0
> Worker-1 ✓ served Customer-4 (Vanilla x1)
> Worker-1 waiting for next order...
> Worker-2 ✓ served Customer-6 (Vanilla x2)
> Worker-2 waiting for next order...
> Customer-2 placed Order{customer=2, ingredient='Vanilla', amount=2}
> Worker-1 processing Order{customer=1, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 0)
> Worker-2 processing Order{customer=2, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 0)
> Worker-2 ✗ cannot serve Customer-2 — Vanilla out of stock. Re-queuing.
> Worker-1 ✗ cannot serve Customer-1 — Vanilla out of stock. Re-queuing.
> Customer-5 placed Order{customer=5, ingredient='Milk', amount=2}
> Customer-4 placed Order{customer=4, ingredient='Vanilla', amount=2}
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=2, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 0)
> Worker-1 waiting for next order...
> Worker-2 ✗ cannot serve Customer-2 — Vanilla out of stock. Re-queuing.
> Worker-1 processing Order{customer=1, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 0)
> Worker-1 ✗ cannot serve Customer-1 — Vanilla out of stock. Re-queuing.
> Customer-3 placed Order{customer=3, ingredient='Vanilla', amount=1}
> Customer-2 placed Order{customer=2, ingredient='Milk', amount=2}
>     [Inventory] Vanilla      restocked +5. Now: 5
> Restocker snapshot: {Milk=6, Chocolate=4, Vanilla=5, Cups=6}
> Customer-6 placed Order{customer=6, ingredient='Cups', amount=1}
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=5, ingredient='Milk', amount=2}
> Customer-4 placed Order{customer=4, ingredient='Vanilla', amount=1}
>     [Inventory] Milk         consumed 2 unit(s). Remaining: 4
> Worker-2 ✓ served Customer-5 (Milk x2)
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=4, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      consumed 2 unit(s). Remaining: 3
> Customer-4 finished ordering. Leaving café.
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=2, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      consumed 2 unit(s). Remaining: 1
> Worker-2 ✓ served Customer-4 (Vanilla x2)
> Worker-1 ✓ served Customer-2 (Vanilla x2)
> Worker-1 waiting for next order...
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=1, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 1)
> Worker-1 processing Order{customer=3, ingredient='Vanilla', amount=1}
>     [Inventory] Vanilla      consumed 1 unit(s). Remaining: 0
> Worker-2 ✗ cannot serve Customer-1 — Vanilla out of stock. Re-queuing.
> Worker-1 ✓ served Customer-3 (Vanilla x1)
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=2, ingredient='Milk', amount=2}
>     [Inventory] Milk         consumed 2 unit(s). Remaining: 2
> Worker-1 ✓ served Customer-2 (Milk x2)
> Worker-1 waiting for next order...
> Customer-1 placed Order{customer=1, ingredient='Chocolate', amount=1}
> Customer-3 placed Order{customer=3, ingredient='Vanilla', amount=2}
> Worker-1 processing Order{customer=6, ingredient='Cups', amount=1}
>     [Inventory] Cups         consumed 1 unit(s). Remaining: 5
> Worker-1 ✓ served Customer-6 (Cups x1)
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=4, ingredient='Vanilla', amount=1}
>     [Inventory] Vanilla      OUT OF STOCK (requested 1, have 0)
> Worker-1 ✗ cannot serve Customer-4 — Vanilla out of stock. Re-queuing.
> Customer-5 placed Order{customer=5, ingredient='Chocolate', amount=2}
> Customer-5 finished ordering. Leaving café.
> Customer-2 placed Order{customer=2, ingredient='Chocolate', amount=2}
> Customer-2 finished ordering. Leaving café.
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=1, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 0)
> Worker-2 ✗ cannot serve Customer-1 — Vanilla out of stock. Re-queuing.
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=1, ingredient='Chocolate', amount=1}
>     [Inventory] Chocolate    consumed 1 unit(s). Remaining: 3
> Worker-1 ✓ served Customer-1 (Chocolate x1)
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=3, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 0)
> Worker-1 ✗ cannot serve Customer-3 — Vanilla out of stock. Re-queuing.
> Customer-1 placed Order{customer=1, ingredient='Vanilla', amount=2}
> Customer-1 finished ordering. Leaving café.
> Customer-3 placed Order{customer=3, ingredient='Cups', amount=2}
> Customer-3 finished ordering. Leaving café.
> Customer-6 placed Order{customer=6, ingredient='Milk', amount=1}
> Customer-6 finished ordering. Leaving café.
> 
> Main: All customers have finished placing orders.
> Main: Sending 2 STOP signal(s) to workers...
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=4, ingredient='Vanilla', amount=1}
>     [Inventory] Vanilla      OUT OF STOCK (requested 1, have 0)
> Worker-2 ✗ cannot serve Customer-4 — Vanilla out of stock. Re-queuing.
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=5, ingredient='Chocolate', amount=2}
>     [Inventory] Chocolate    consumed 2 unit(s). Remaining: 1
> Worker-1 ✓ served Customer-5 (Chocolate x2)
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=2, ingredient='Chocolate', amount=2}
>     [Inventory] Chocolate    OUT OF STOCK (requested 2, have 1)
> Worker-1 ✗ cannot serve Customer-2 — Chocolate out of stock. Re-queuing.
> Worker-2 waiting for next order...
> Worker-2 processing Order{customer=1, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      OUT OF STOCK (requested 2, have 0)
> Worker-2 ✗ cannot serve Customer-1 — Vanilla out of stock. Re-queuing.
>     [Inventory] Milk         restocked +5. Now: 7
>     [Inventory] Chocolate    restocked +5. Now: 6
>     [Inventory] Vanilla      restocked +5. Now: 5
> Restocker snapshot: {Milk=7, Chocolate=6, Vanilla=5, Cups=5}
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=3, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      consumed 2 unit(s). Remaining: 3
> Worker-1 ✓ served Customer-3 (Vanilla x2)
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=1, ingredient='Vanilla', amount=2}
>     [Inventory] Vanilla      consumed 2 unit(s). Remaining: 1
> Worker-1 ✓ served Customer-1 (Vanilla x2)
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=3, ingredient='Cups', amount=2}
>     [Inventory] Cups         consumed 2 unit(s). Remaining: 3
> Worker-1 ✓ served Customer-3 (Cups x2)
> Worker-1 waiting for next order...
> Worker-1 processing Order{customer=6, ingredient='Milk', amount=1}
>     [Inventory] Milk         consumed 1 unit(s). Remaining: 6
> Worker-1 ✓ served Customer-6 (Milk x1)
> Worker-1 waiting for next order...
> Worker-1 received STOP signal. Ending shift.
> Worker-1 shift ended.
> Main: Worker-1 final state → TERMINATED
> Worker-2 waiting for next order...
> Worker-2 received STOP signal. Ending shift.
> Worker-2 shift ended.
> Main: Worker-2 final state → TERMINATED
> Restocker received shutdown signal.
> Main: Restocker final state → TERMINATED
> 
> TCafe closed. All threads terminated.
> Final inventory: {Milk=6, Chocolate=6, Vanilla=1, Cups=3}
> ```
> 
> ---
> 
> ## Thread Lifecycle — What the Output Shows
> 
> The output directly maps to the lifecycle diagram:
> 
> ```jsx
> 	[NEW]    <--- w.getState() printed before start()
>        |
>    start()
>        ↓
>  [RUNNABLE] <--- w.getState() printed immediately after start()
>        |
>   CPU picks it
>        ↓
>  [RUNNING]  <--- thread is inside run(), printing messages
>        |
>   take() or put() blocks
>        ↓
>  [WAITING]  <--- thread suspended, waiting for queue item
>        |
>   Thread.sleep()
>        ↓
>  [TIMED_WAITING] <--- thread suspended for fixed duration
>        |
>   POISON_PILL received / shutdown()
>        ↓
>  [TERMINATED] <--- w.getState() printed after join()
> ```
> 
> ---
> 
> ## How Each Team Member Uses This in the Report
> 
> | Member | File to Reference | Key Point |
> | --- | --- | --- |
> | Student 2 (Why Threads) | `Main.java` — 6 customers run concurrently | Without threads, customers served one at a time |
> | Student 3 (Process) | `Main.java` — `orderQueue` and `inventory` passed to all threads | All threads share the same heap objects (shared memory) |
> | Student 4 (Thread Concept) | `Customer.java` — lifecycle comments | Every state transition is documented in the code |
> | Student 4 (Sync) | `Inventory.java` — `synchronized` methods | Race condition explained in comments |
> | Student 5 (Analysis) | `Worker.java` — re-queue logic, `Restocker.java` | Performance bottleneck, overhead discussion |
> | Abdirizak (Demo) | `Main.java` — `getState()` output | Show lifecycle states live during presentation |
> 
> ---
> 
> ## Key Concepts Glossary
> 
> | Term | Definition | In Code |
> | --- | --- | --- |
> | Thread | Smallest unit of execution inside a process | Every class with `run()` |
> | Process | Running program with its own memory | The JVM running `Main` |
> | Shared memory | Heap objects accessible by all threads | `orderQueue`, `inventory` |
> | Race condition | Two threads corrupt shared data simultaneously | `Inventory` without `synchronized` |
> | Critical section | Code that must only run in one thread at a time | `synchronized` methods in `Inventory` |
> | BlockingQueue | Thread-safe queue — blocks on empty/full | `orderQueue` |
> | `volatile` | Forces variable reads/writes to main memory | `running` in `Restocker` |
> | Poison pill | Sentinel value that signals a consumer thread to stop | `Order.POISON_PILL` |
> | `join()` | Makes calling thread wait for target to terminate | All `join()` calls in `Main` |
