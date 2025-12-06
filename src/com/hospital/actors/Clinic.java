package com.hospital.actors; // Correct package

import com.hospital.core.HospitalSystemMonitor; // Import the Monitor
import java.util.Random;

public class Clinic implements Runnable {
    private final HospitalSystemMonitor monitor;
    private final String id;
    private final Random random = new Random();

    public Clinic(HospitalSystemMonitor monitor, String id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Simulate variable arrival rate of patients
                Thread.sleep(random.nextInt(500) + 100); 
                
                String sampleId = id + "-S" + System.nanoTime();
                
                // Add to monitor (will wait if queue is full)
                monitor.addSample(sampleId);
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}