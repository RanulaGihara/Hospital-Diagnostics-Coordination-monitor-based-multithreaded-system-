package com.hospital.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Part B: Advanced Synchronization using Locks & Conditions.
 * Replaces 'synchronized' with ReentrantLock and ReentrantReadWriteLock.
 */
public class HospitalSystemMonitorPartB implements HospitalMonitor {

    // Shared Resources
    private final Queue<String> sampleQueue = new LinkedList<>();
    private final int maxQueueCapacity;
    private volatile boolean isShutdown = false;
    
    // Stats
    private int totalSamplesProcessed = 0;
    private int totalSamplesRejected = 0;

    // --- PART B MECHANISMS ---
    
    // 1. For Producer-Consumer: ReentrantLock + Conditions [cite: 73]
    private final ReentrantLock queueLock = new ReentrantLock(true); // 'true' = Fair lock
    private final Condition notFull = queueLock.newCondition();
    private final Condition notEmpty = queueLock.newCondition();

    // 2. For Reader-Writer: ReentrantReadWriteLock [cite: 74]
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true); // Fair policy

    public HospitalSystemMonitorPartB(int capacity) {
        this.maxQueueCapacity = capacity;
    }

    @Override
    public void addSample(String sampleId) throws InterruptedException {
        queueLock.lock(); // Acquire lock explicitly
        try {
            // While queue is full, await (replaces wait())
            while (sampleQueue.size() >= maxQueueCapacity && !isShutdown) {
                System.out.println("   [PartB] Queue Full. Waiting...");
                notFull.await(); 
            }

            if (isShutdown) {
                System.out.println("   [PartB] Shutdown. Rejecting: " + sampleId);
                totalSamplesRejected++;
                return;
            }

            sampleQueue.add(sampleId);
            System.out.println(" + [PartB] Added: " + sampleId);
            
            // Signal consumers (replaces notifyAll())
            notEmpty.signalAll();
        } finally {
            queueLock.unlock(); // Always unlock in finally!
        }
    }

    @Override
    public String processSample() throws InterruptedException {
        queueLock.lock();
        try {
            while (sampleQueue.isEmpty() && !isShutdown) {
                notEmpty.await();
            }

            if (sampleQueue.isEmpty() && isShutdown) {
                return null;
            }

            String sample = sampleQueue.poll();
            totalSamplesProcessed++;
            
            // Signal producers that space is available
            notFull.signalAll();
            return sample;
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void startReading() throws InterruptedException {
        // Lock specifically for reading
        rwLock.readLock().lock(); 
        // Note: ReadWriteLock handles the "wait if writer active" logic automatically!
    }

    @Override
    public void stopReading() {
        rwLock.readLock().unlock();
    }

    @Override
    public void startWriting() throws InterruptedException {
        // Lock specifically for writing (exclusive)
        rwLock.writeLock().lock();
    }

    @Override
    public void stopWriting() {
        rwLock.writeLock().unlock();
    }

    @Override
    public void shutdown() {
        queueLock.lock();
        try {
            isShutdown = true;
            notEmpty.signalAll(); // Wake up consumers
            notFull.signalAll();  // Wake up producers
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public String getStats() {
        // Even this needs thread safety!
        rwLock.readLock().lock();
        try {
            return String.format("Queue: %d/%d | Processed: %d | Rejected: %d", 
                sampleQueue.size(), maxQueueCapacity, totalSamplesProcessed, totalSamplesRejected);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}