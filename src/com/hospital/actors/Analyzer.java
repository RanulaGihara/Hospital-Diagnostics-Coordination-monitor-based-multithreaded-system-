package com.hospital.actors; 

import com.hospital.core.HospitalMonitor;
import java.util.Random;

public class Analyzer implements Runnable {
    private final HospitalMonitor monitor;
    private final String id;
    private final Random random = new Random();

    public Analyzer(HospitalMonitor monitor, String id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Get sample (will wait if queue is empty)
                String sample = monitor.processSample();
                
                if (sample == null) break; // Exit on shutdown signal

                // Simulate processing time (Analyzer takes time to test sample)
                System.out.println("      ==> [" + id + "] Processed " + sample);
                Thread.sleep(random.nextInt(1000) + 500); 
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}