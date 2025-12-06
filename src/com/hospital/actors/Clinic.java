package com.hospital.actors;

import com.hospital.core.HospitalMonitor;
import java.util.Random;

public class Clinic implements Runnable {
    private final HospitalMonitor monitor;
    private final String id;
    private final int sleepTime; // <--- NEW: Stores the speed setting
    private final Random random = new Random();

    // Constructor now asks for 'sleepTime'
    public Clinic(HospitalMonitor monitor, String id, int sleepTime) {
        this.monitor = monitor;
        this.id = id;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Sleep for the configured time (plus small random variance of 0-100ms)
                Thread.sleep(sleepTime + random.nextInt(100)); 
                
                String sampleId = id + "-S" + System.nanoTime();
                monitor.addSample(sampleId);
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}