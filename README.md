# ☕ TCafe — Multithreaded Order Simulation


## What This Project Demonstrates

This simulation uses a café scenario to demonstrate core thread concepts in Java.

| Thread Concept | Where It Appears |
|----------------|-----------------|
| Thread creation (`extends Thread`) | `Worker.java` |
| Thread creation (`implements Runnable`) | `Customer.java` |
| Thread lifecycle states (NEW → TERMINATED) | `Main.java` — printed live via `getState()` |
| Shared memory (heap) | `orderQueue` and `inventory` in `Main.java` |
| Race condition risk | `Inventory.java` — explained in comments |
| Critical section (`synchronized`) | All methods in `Inventory.java` |
| Producer-Consumer pattern | `Customer` → `BlockingQueue` → `Worker` |
| `BlockingQueue` coordination | `Customer.java` `put()` / `Worker.java` `take()` |
| `start()` vs `run()` | `Main.java` — always `start()`, never `run()` |
| `join()` — waiting for termination | `Main.java` — customers, workers, restocker |
| Poison pill shutdown | `Order.POISON_PILL` + `Worker.java` |
| `volatile` keyword | `Restocker.java` — `running` flag |
| Graceful thread shutdown | `Restocker.shutdown()` in `Main.java` |

---

## Project Structure

```
tcafe/
├── README.md
├── Customer.java
├── Inventory.java
├── Main.java
├── Order.java
├── Restocker.java
└── Worker.java
```

---

## How to Compile and Run

```bash
# From the project root
javac *.java

# Run
java Main
```

---

## Expected Output (Sample)

```
TCafe — Multithreaded Simulation

── Initial inventory: {Milk=6, Chocolate=4, Vanilla=4, Cups=6}

Main: Worker-1 state before start → NEW
Main: Worker-1 state after  start → RUNNABLE
Main: Worker-2 state before start → NEW
Main: Worker-2 state after  start → RUNNABLE
Main: Restocker state before start → NEW
Main: Restocker state after  start → RUNNABLE

Main: Customer-1 state before start → NEW
...

Customer-1 has arrived. Will place 3 order(s).
Worker-1 started. Ready to serve customers.
Worker-2 started. Ready to serve customers.
Restocker started. Monitoring inventory every 500ms.

Customer-1 placed Order{customer=1, ingredient='Milk', amount=1}
Worker-1 waiting for next order...
Worker-1 processing Order{customer=1, ingredient='Milk', amount=1}
    [Inventory] Milk         consumed 1 unit(s). Remaining: 5
Worker-1 ✓ served Customer-1 (Milk x1)
...
Restocker snapshot: {Milk=5, Chocolate=4, Vanilla=3, Cups=4}
...

Main: All customers have finished placing orders.
Main: Sending 2 STOP signal(s) to workers...
Worker-1 received STOP signal. Ending shift.
Worker-2 received STOP signal. Ending shift.
Main: Worker-1 final state → TERMINATED
Main: Worker-2 final state → TERMINATED
Restocker received shutdown signal.
Main: Restocker final state → TERMINATED

TCafe closed. All threads terminated.
Final inventory: {Milk=3, Chocolate=1, Vanilla=2, Cups=0}
```

---

## Thread Lifecycle — What the Output Shows

The output directly maps to the lifecycle diagram:

```
     [NEW]  ←── w.getState() printed before start()
       |
   start()
       ↓
  [RUNNABLE] ←── w.getState() printed immediately after start()
       |
  CPU picks it
       ↓
  [RUNNING]  ←── thread is inside run(), printing messages
       |
  take() or put() blocks
       ↓
  [WAITING]  ←── thread suspended, waiting for queue item
       |
  Thread.sleep()
       ↓
[TIMED_WAITING] ←── thread suspended for fixed duration
       |
  POISON_PILL received / shutdown()
       ↓
[TERMINATED] ←── w.getState() printed after join()
```

---

## How Each Team Member Uses This in the Report

| Member | File to Reference | Key Point |
|--------|------------------|-----------|
| Student 2 (Why Threads) | `Main.java` — 6 customers run concurrently | Without threads, customers served one at a time |
| Student 3 (Process) | `Main.java` — `orderQueue` and `inventory` passed to all threads | All threads share the same heap objects (shared memory) |
| Student 4 (Thread Concept) | `Customer.java` — lifecycle comments | Every state transition is documented in the code |
| Student 4 (Sync) | `Inventory.java` — `synchronized` methods | Race condition explained in comments |
| Student 5 (Analysis) | `Worker.java` — re-queue logic, `Restocker.java` | Performance bottleneck, overhead discussion |
| Abdirizak (Demo) | `Main.java` — `getState()` output | Show lifecycle states live during presentation |

---

## Key Concepts Glossary

| Term | Definition | In Code |
|------|-----------|---------|
| Thread | Smallest unit of execution inside a process | Every class with `run()` |
| Process | Running program with its own memory | The JVM running `Main` |
| Shared memory | Heap objects accessible by all threads | `orderQueue`, `inventory` |
| Race condition | Two threads corrupt shared data simultaneously | `Inventory` without `synchronized` |
| Critical section | Code that must only run in one thread at a time | `synchronized` methods in `Inventory` |
| BlockingQueue | Thread-safe queue — blocks on empty/full | `orderQueue` |
| `volatile` | Forces variable reads/writes to main memory | `running` in `Restocker` |
| Poison pill | Sentinel value that signals a consumer thread to stop | `Order.POISON_PILL` |
| `join()` | Makes calling thread wait for target to terminate | All `join()` calls in `Main` |
