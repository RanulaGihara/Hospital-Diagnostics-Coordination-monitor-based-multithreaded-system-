package com.hospital.actors; 

import com.hospital.core.HospitalMonitor;
import java.util.Random;

public class Supervisor implements Runnable {
    private final HospitalMonitor monitor;
    private final String id;
    private final Random random = new Random();

    public Supervisor(HospitalMonitor monitor, String id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(random.nextInt(3000) + 4000); 

                monitor.startWriting();

                System.out.println("!!! [Supervisor] " + id + " CHANGING POLICIES (LOCK HELD) !!!");
                Thread.sleep(500); 
                
                monitor.stopWriting();
                System.out.println("!!! [Supervisor] " + id + " released lock.");
            }
        } catch (InterruptedException e) {
            System.out.println(id + " stopped.");
        }
    }
}