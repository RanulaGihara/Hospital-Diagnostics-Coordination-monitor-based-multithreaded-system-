package com.hospital.core; 
import java.util.LinkedList;
import java.util.Queue;

/**
 * Part A: Monitor-Based Synchronization
 * Protects the shared sample queue and system configuration.
 */
public class HospitalSystemMonitor implements HospitalMonitor {

    private final Queue<String> sampleQueue = new LinkedList<>();
    private final int maxQueueCapacity;
    
    private int activeReaders = 0;      
    private int waitingWriters = 0;     
    private boolean isWriting = false; 

    private volatile boolean isShutdown = false; 

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
        while (sampleQueue.size() >= maxQueueCapacity && !isShutdown) {
            System.out.println("   [Monitor] Queue Full. Clinic waiting...");
            wait(); 
        }

        if (isShutdown) {
            System.out.println("   [Monitor] System shutdown. Rejecting sample: " + sampleId);
            totalSamplesRejected++;
            return;
        }

        sampleQueue.add(sampleId);
        System.out.println(" + [Monitor] Sample Added: " + sampleId + " (Queue: " + sampleQueue.size() + ")");

        notifyAll(); 
    }

    /**
     * CONSUMER: Removes a sample from the queue.
     * Blocks if the queue is empty.
     * Returns null if system is shutting down.
     */
    public synchronized String processSample() throws InterruptedException {
        while (sampleQueue.isEmpty() && !isShutdown) {
            wait(); 
        }

        if (sampleQueue.isEmpty() && isShutdown) {
            return null; 
        }

        String sample = sampleQueue.poll();
        totalSamplesProcessed++;
        
        notifyAll();
        
        return sample;
    }


    public synchronized void startReading() throws InterruptedException {
        while (isWriting || waitingWriters > 0) {
            wait();
        }
        activeReaders++;
    }

    public synchronized void stopReading() {
        activeReaders--;
        if (activeReaders == 0) {
            notifyAll(); 
        }
    }

    public synchronized void startWriting() throws InterruptedException {
        waitingWriters++;
        while (activeReaders > 0 || isWriting) {
            wait();
        }
        waitingWriters--;
        isWriting = true;
    }

    public synchronized void stopWriting() {
        isWriting = false;
        notifyAll(); 
    }

    // ============================================================
    // UTILITIES
    // ============================================================

    public synchronized void shutdown() {
        isShutdown = true;
        notifyAll();
    }
    
    public synchronized String getStats() {
        return String.format("Queue: %d/%d | Processed: %d | Rejected: %d", 
            sampleQueue.size(), maxQueueCapacity, totalSamplesProcessed, totalSamplesRejected);
    }
}