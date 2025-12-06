package com.hospital.core;

/**
 * Common Interface for both Part A and Part B.
 * Allows Actors to work with any implementation.
 */
public interface HospitalMonitor {
    // Producer-Consumer Methods
    void addSample(String sampleId) throws InterruptedException;
    String processSample() throws InterruptedException;

    // Reader-Writer Methods
    void startReading() throws InterruptedException;
    void stopReading();
    void startWriting() throws InterruptedException;
    void stopWriting();

    // Utilities
    void shutdown();
    String getStats();
}