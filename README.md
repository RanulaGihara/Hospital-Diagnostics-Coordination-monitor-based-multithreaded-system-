# Hospital Diagnostics Coordination System

**Student ID:** W2053386

**Module:** 7SENG007C Concurrent and Distributed Systems

## Overview

This project simulates a concurrent hospital diagnostic system with Producers (Clinics), Consumers (Analyzers), Readers (Auditors), and Writers (Supervisors). It demonstrates thread safety using two different mechanisms:

1. **Part A:** Java Monitors (`synchronized`, `wait`, `notifyAll`).
2. **Part B:** Java Locks (`ReentrantLock`, `Condition`, `ReentrantReadWriteLock`).

## How to Compile and Run

**Prerequisites:** Java 11 or higher.

1. **Compile:**
   Open a terminal in the project root and run:
   ```bash
   javac -d bin src/com/hospital/core/*.java src/com/hospital/actors/*.java
   ```
1. **Run:**

   ```bash
      java -cp bin com.hospital.core HospitalSystem
   ```

## Workload Configuration & Parameters

The system behavior is controlled by variables in `src/com/hospital/core/HospitalSystem.java`. Open this file to configure the following scenarios:

### Scenario 1: Calm Workload (Low Contention)

Use this to see smooth processing where consumption meets production.

- **Clinics:** 2 (Sleep: 1000ms)
- **Analyzers:** 2 (Sleep: 500ms)

### Scenario 2: Surge Workload (High Contention)

Use this to observe queue saturation and bottleneck handling.

- **Clinics:** 4 (Sleep: 200ms)
- **Analyzers:** 1 (Sleep: 1000ms)

### To Switch Implementations:

In `HospitalSystem.java`, comment/uncomment the monitor initialization lines:

```java
// Option 1: Part A (Monitor)
HospitalMonitor monitor = new HospitalSystemMonitor(5);

// Option 2: Part B (Locks) - Uncomment to use
// HospitalMonitor monitor = new HospitalSystemMonitorPartB(5);
```

## Architecture Overview

The project is organized into two main packages to separate the core synchronization logic from the concurrent actors:

- **`com.hospital.core`**
  Contains the main entry point (`HospitalSystem`), the synchronization interface (`HospitalMonitor`), and its two implementations (`HospitalSystemMonitor` and `HospitalSystemMonitorPartB`).

- **`com.hospital.actors`**
  Contains the runnable thread classes:

  - **Producers:** `Clinic`
  - **Consumers:** `Analyzer`
  - **Readers:** `Auditor`
  - **Writers:** `Supervisor`

- **Design Patterns**
  The system implements two classic concurrency patterns:
  - **Producer-Consumer:** Managed via the bounded buffer (Queue).
  - **Readers-Writers:** Managed via the shared monitor/lock state.

## Known Limitations

1. **Scalability (Part A):** The Monitor-based implementation utilizes `notifyAll()`, which wakes every waiting thread regardless of their role. In a system scaled to thousands of threads, this would result in a "Thundering Herd" problem, causing significant CPU contention.
2. **Fairness (Part A):** Thread scheduling in Part A relies entirely on the underlying JVM and OS scheduler. It does not guarantee First-In-First-Out (FIFO) fairness, meaning a thread could theoretically starve under extreme contention.
3. **Console Logging:** The extensive use of `System.out.println` for logging during the "Surge" workload creates I/O blocking. This may slightly alter the precise timing of thread interleaving compared to a production system using an asynchronous logger.
