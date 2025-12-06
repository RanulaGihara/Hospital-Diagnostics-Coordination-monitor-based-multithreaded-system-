package com.hospital.actors; // Correct package

import com.hospital.core.HospitalMonitor; // Import the Monitor
import java.util.Random;

public class Auditor implements Runnable {
    private final HospitalMonitor monitor;
    private final String id;
    private final Random random = new Random();

    public Auditor(HospitalMonitor monitor, String id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 1. Enter Reader Mode
                monitor.startReading();
                
                // 2. Critical Section (Read Only)
                // Multiple Auditors can do this simultaneously
                String stats = monitor.getStats();
                System.out.println(" [Auditor] " + id + " reading stats: " + stats);
                Thread.sleep(200); // Simulate report generation time
                
                // 3. Exit Reader Mode
                monitor.stopReading();

                // 4. Wait before next audit
                Thread.sleep(random.nextInt(1000) + 1000); 
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}