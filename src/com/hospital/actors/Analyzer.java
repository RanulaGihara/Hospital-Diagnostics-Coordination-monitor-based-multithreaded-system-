package com.hospital.actors;

import com.hospital.core.HospitalMonitor;
import java.util.Random;

public class Analyzer implements Runnable {
    private final HospitalMonitor monitor;
    private final String id;
    private final int sleepTime; // <--- NEW: Stores the speed setting
    private final Random random = new Random();

    // Constructor now asks for 'sleepTime'
    public Analyzer(HospitalMonitor monitor, String id, int sleepTime) {
        this.monitor = monitor;
        this.id = id;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String sample = monitor.processSample();
                
                if (sample == null) break; 

                System.out.println("      ==> [" + id + "] Processed " + sample);
                
                // Sleep for the configured processing time
                Thread.sleep(sleepTime + random.nextInt(100)); 
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}