package com.hospital.actors; 

import com.hospital.core.HospitalMonitor; 
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
                monitor.startReading();
                
                String stats = monitor.getStats();
                System.out.println(" [Auditor] " + id + " reading stats: " + stats);
                Thread.sleep(200); // Simulate report generation time
                
                monitor.stopReading();

                Thread.sleep(random.nextInt(1000) + 1000); 
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}