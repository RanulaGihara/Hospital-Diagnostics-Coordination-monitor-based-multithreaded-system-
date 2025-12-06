package com.hospital.actors; // Correct package

import com.hospital.core.HospitalSystemMonitor; // Import the Monitor
import java.util.Random;

public class Supervisor implements Runnable {
    private final HospitalSystemMonitor monitor;
    private final String id;
    private final Random random = new Random();

    public Supervisor(HospitalSystemMonitor monitor, String id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Wait a long time between updates (Supervisors work occasionally)
                Thread.sleep(random.nextInt(3000) + 4000); 

                // 1. Enter Writer Mode (Exclusive Lock)
                monitor.startWriting();

                // 2. Critical Section (Modifying System)
                // No one else can read or write during this time
                System.out.println("!!! [Supervisor] " + id + " CHANGING POLICIES (LOCK HELD) !!!");
                Thread.sleep(500); // Simulate reconfiguration time
                
                // 3. Exit Writer Mode
                monitor.stopWriting();
                System.out.println("!!! [Supervisor] " + id + " released lock.");
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}