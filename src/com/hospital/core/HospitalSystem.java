package com.hospital.core; // <--- 1. Define the package

// <--- 2. Import the Actors from the 'actors' package
import com.hospital.actors.Clinic;
import com.hospital.actors.Analyzer;
import com.hospital.actors.Auditor;
import com.hospital.actors.Supervisor;

public class HospitalSystem {
    public static void main(String[] args) throws InterruptedException {
        // 1. Initialize Monitor with queue capacity of 3
        HospitalSystemMonitor monitor = new HospitalSystemMonitor(3);

        // 2. Create Actors (Producers & Consumers)
        Thread clinic1 = new Thread(new Clinic(monitor, "Clinic-A"));
        Thread clinic2 = new Thread(new Clinic(monitor, "Clinic-B"));
        Thread analyzer1 = new Thread(new Analyzer(monitor, "Analyzer-1"));
        Thread analyzer2 = new Thread(new Analyzer(monitor, "Analyzer-2"));

        // 3. Create NEW Actors (Readers & Writers)
        Thread auditor1 = new Thread(new Auditor(monitor, "Auditor-1"));
        Thread auditor2 = new Thread(new Auditor(monitor, "Auditor-2"));
        Thread supervisor = new Thread(new Supervisor(monitor, "Supervisor-Admin"));

        // 4. Start All Threads
        System.out.println("--- HOSPITAL SYSTEM STARTING (FULL) ---");
        clinic1.start();
        clinic2.start();
        analyzer1.start();
        analyzer2.start();
        auditor1.start();
        auditor2.start();
        supervisor.start();

        // 5. Run Simulation for 10 seconds
        Thread.sleep(10000);

        // 6. Shutdown
        System.out.println("\n--- INITIATING SHUTDOWN ---");
        monitor.shutdown();
        
        // Interrupt all threads to wake them from sleep/wait
        clinic1.interrupt();
        clinic2.interrupt();
        analyzer1.interrupt();
        analyzer2.interrupt();
        auditor1.interrupt();
        auditor2.interrupt();
        supervisor.interrupt();
        
        // Join (Wait for them to die)
        clinic1.join();
        clinic2.join();
        analyzer1.join();
        analyzer2.join();
        auditor1.join();
        auditor2.join();
        supervisor.join();
        
        System.out.println("--- SYSTEM TERMINATED ---");
        System.out.println("Final Stats: " + monitor.getStats());
    }
}