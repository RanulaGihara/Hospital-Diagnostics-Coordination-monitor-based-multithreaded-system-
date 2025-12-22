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

    // --- PART B  ---
    
    private final ReentrantLock queueLock = new ReentrantLock(true); 
    private final Condition notFull = queueLock.newCondition();
    private final Condition notEmpty = queueLock.newCondition();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true); 

    public HospitalSystemMonitorPartB(int capacity) {
        this.maxQueueCapacity = capacity;
    }

    @Override
    public void addSample(String sampleId) throws InterruptedException {
        queueLock.lock(); 
        try {
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
            
            notEmpty.signalAll();
        } finally {
            queueLock.unlock(); 
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
            
            notFull.signalAll();
            return sample;
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void startReading() throws InterruptedException {
        rwLock.readLock().lock(); 
    }

    @Override
    public void stopReading() {
        rwLock.readLock().unlock();
    }

    @Override
    public void startWriting() throws InterruptedException {
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
            notEmpty.signalAll(); 
            notFull.signalAll();  
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public String getStats() {
        rwLock.readLock().lock();
        try {
            return String.format("Queue: %d/%d | Processed: %d | Rejected: %d", 
                sampleQueue.size(), maxQueueCapacity, totalSamplesProcessed, totalSamplesRejected);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}