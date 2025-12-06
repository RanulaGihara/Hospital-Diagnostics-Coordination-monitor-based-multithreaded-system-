package com.hospital.core; // <--- THIS IS THE KEY UPDATE

import java.util.LinkedList;
import java.util.Queue;

/**
 * Part A: Monitor-Based Synchronization
 * Protects the shared sample queue and system configuration.
 */
public class HospitalSystemMonitor implements HospitalMonitor {

    // --- Shared Resources ---
    private final Queue<String> sampleQueue = new LinkedList<>();
    private final int maxQueueCapacity;
    
    // --- Reader-Writer State (Auditors vs Supervisors) ---
    private int activeReaders = 0;      // How many Auditors are currently reading
    private int waitingWriters = 0;     // How many Supervisors want to write
    private boolean isWriting = false;  // Is a Supervisor currently writing?

    // --- Shutdown Flag ---
    private volatile boolean isShutdown = false; 

    // --- Statistics for Performance Evaluation ---
    private int totalSamplesProcessed = 0;
    private int totalSamplesRejected = 0;

    public HospitalSystemMonitor(int capacity) {
        this.maxQueueCapacity = capacity;
    }

    // ============================================================
    // PRODUCER-CONSUMER (Clinics & Analyzers)
    // ============================================================

    /**
     * PRODUCER: Adds a sample to the queue.
     * Blocks if the queue is full.
     */
    public synchronized void addSample(String sampleId) throws InterruptedException {
        // Guard: Wait if queue is full or if system is shutting down
        while (sampleQueue.size() >= maxQueueCapacity && !isShutdown) {
            System.out.println("   [Monitor] Queue Full. Clinic waiting...");
            wait(); // Releases lock, enters WAITING state 
        }

        if (isShutdown) {
            System.out.println("   [Monitor] System shutdown. Rejecting sample: " + sampleId);
            totalSamplesRejected++;
            return;
        }

        // Critical Section
        sampleQueue.add(sampleId);
        System.out.println(" + [Monitor] Sample Added: " + sampleId + " (Queue: " + sampleQueue.size() + ")");

        // Notification: Wake up waiting Analyzers (Consumers)
        notifyAll(); 
    }

    /**
     * CONSUMER: Removes a sample from the queue.
     * Blocks if the queue is empty.
     * Returns null if system is shutting down.
     */
    public synchronized String processSample() throws InterruptedException {
        // Guard: Wait if queue is empty
        while (sampleQueue.isEmpty() && !isShutdown) {
            wait(); 
        }

        if (sampleQueue.isEmpty() && isShutdown) {
            return null; // Signal to Analyzer to stop
        }

        // Critical Section
        String sample = sampleQueue.poll();
        totalSamplesProcessed++;
        
        // Notification: Wake up waiting Clinics (Producers)
        notifyAll();
        
        return sample;
    }

    // ============================================================
    // READER-WRITER (Auditors & Supervisors)
    // ============================================================

    /**
     * READER ENTRY: Request permission to read statistics.
     * Policy: Writer-Preference (to prevent starvation of Supervisors)
     */
    public synchronized void startReading() throws InterruptedException {
        // Wait if a writer is active OR if writers are waiting (Fairness)
        while (isWriting || waitingWriters > 0) {
            wait();
        }
        activeReaders++;
    }

    /**
     * READER EXIT: Finished reading.
     */
    public synchronized void stopReading() {
        activeReaders--;
        if (activeReaders == 0) {
            notifyAll(); // Wake up waiting Writers
        }
    }

    /**
     * WRITER ENTRY: Request permission to modify config.
     * Grants exclusive access.
     */
    public synchronized void startWriting() throws InterruptedException {
        waitingWriters++;
        // Wait until NO readers and NO other writers are active
        while (activeReaders > 0 || isWriting) {
            wait();
        }
        waitingWriters--;
        isWriting = true;
    }

    /**
     * WRITER EXIT: Finished modifying.
     */
    public synchronized void stopWriting() {
        isWriting = false;
        notifyAll(); // Wake up Readers and other Writers
    }

    // ============================================================
    // UTILITIES
    // ============================================================

    public synchronized void shutdown() {
        isShutdown = true;
        notifyAll(); // Wake up everyone so they can check the shutdown flag and exit
    }
    
    // For Auditors to read safely
    public synchronized String getStats() {
        return String.format("Queue: %d/%d | Processed: %d | Rejected: %d", 
            sampleQueue.size(), maxQueueCapacity, totalSamplesProcessed, totalSamplesRejected);
    }
}