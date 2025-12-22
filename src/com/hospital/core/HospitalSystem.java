package com.hospital.core;

import com.hospital.actors.Analyzer;
import com.hospital.actors.Clinic;
import com.hospital.actors.Auditor;
import com.hospital.actors.Supervisor;

public class HospitalSystem {
    public static void main(String[] args) throws InterruptedException {
        
        // =================================================================
        // 1. SELECT WORKLOAD (Uncomment ONE set of variables)
        // =================================================================
        
        // --- EXPERIMENT A: CALM WORKLOAD ---
        // (Clinics are slow, Analyzers are fast -> Queue stays empty)
        // int nClinics = 2;      int producerSleep = 1000;
        // int nAnalyzers = 2;    int consumerSleep = 500;
        
        // --- EXPERIMENT B: SURGE WORKLOAD ---
        // (Clinics are fast, Analyzers are slow -> Queue fills up)
        int nClinics = 4;      int producerSleep = 200;
        int nAnalyzers = 1;    int consumerSleep = 1000;

        System.out.println("--- CONFIGURATION: Clinics=" + nClinics + " | Analyzers=" + nAnalyzers + " ---");

        // =================================================================
        // 2. SELECT IMPLEMENTATION (Part A or Part B)
        // =================================================================
        
        // Option 1: Part A (Monitor)
        HospitalMonitor monitor = new HospitalSystemMonitor(5);    
        
        // Option 2: Part B (Locks) - Uncomment to use
        // HospitalMonitor monitor = new HospitalSystemMonitorPartB(5); 

        System.out.println("Type: " + monitor.getClass().getSimpleName());

        // =================================================================
        // 3. START ACTORS
        // =================================================================

        Thread[] clinics = new Thread[nClinics];
        for (int i = 0; i < nClinics; i++) {
            clinics[i] = new Thread(new Clinic(monitor, "Clinic-" + i, producerSleep));
            clinics[i].start();
        }

        Thread[] analyzers = new Thread[nAnalyzers];
        for (int i = 0; i < nAnalyzers; i++) {
            analyzers[i] = new Thread(new Analyzer(monitor, "Analyzer-" + i, consumerSleep));
            analyzers[i].start();
        }

        Thread auditor = new Thread(new Auditor(monitor, "Auditor-1"));
        Thread supervisor = new Thread(new Supervisor(monitor, "Supervisor-Admin"));
        auditor.start();
        supervisor.start();

        Thread.sleep(10000);

        System.out.println("\n--- INITIATING SHUTDOWN ---");
        monitor.shutdown();
        
        for (Thread t : clinics) t.interrupt();
        for (Thread t : analyzers) t.interrupt();
        auditor.interrupt();
        supervisor.interrupt();
        
        for (Thread t : clinics) t.join();
        for (Thread t : analyzers) t.join();
        auditor.join();
        supervisor.join();
        
        System.out.println("--- SYSTEM TERMINATED ---");
        System.out.println("Final Stats: " + monitor.getStats());
    }
}