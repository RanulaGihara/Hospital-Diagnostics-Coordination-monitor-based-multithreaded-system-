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
