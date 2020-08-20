package com.fau.schedulers;

import com.fau.process.Process;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public abstract class Scheduler {

    public LinkedList<Process> ready;
    public LinkedList<Process> device;
    public LinkedList<Process> terminated;

    public int time_execution = 0; // Tracks the current execution time (Te)

    public double avg_response_time = 0;
    public double avg_wait_time = 0;
    public double avg_turnaround_time = 0;
    public double cpu_util = 0;

    /* this pcb array would better suited as a class; however, created as an array for ease */
    public int[][] pcb; // Holds information such as place in burst arrays (i), response time, wait time, turnaround time
    /* Matrix looks like:       First    i     Tr     Tw     Ttr
                                 [0]    [0]    [0]    [0]     [0]    */

    public Scheduler() {
        ready = new LinkedList<Process>();
        device = new LinkedList<Process>();
        terminated = new LinkedList<Process>();
        pcb = new int[8][5];
    }

    public void add(Process p) {
        ready.add(p);
    }

    public String rq_toString() {
        if (this.ready.isEmpty()) return "Empty";

        StringBuilder sb = new StringBuilder();
        for(Process p : this.ready) {
            sb.append("P" + p.pId + "(" + p.cpu_burst.get(this.pcb[p.pId - 1][1]) + ") ");
        }
        return sb.toString();
    }

    public String devicequeue_toString() {
        if (this.device.isEmpty()) return "Empty";

        StringBuilder sb = new StringBuilder();
        for(Process p : this.device) {
            sb.append("P" + p.pId + "(" + (p.io_wait - this.time_execution) + ") ");
        }
        return sb.toString();
    }

    public void print_analytics() {

        Collections.sort(this.terminated, new Comparator<Process>() { /* Sort terminated queue for easy data analysis */
            @Override
            public int compare(Process o1, Process o2) {
                return o1.pId - o2.pId;
            }
        });

        System.out.println("Time needed to complete all processes: " + this.time_execution + "\n");

        for(Process p : this.terminated) {
            avg_turnaround_time += this.pcb[p.pId - 1][4];
            System.out.println("Turnaround time for P" + p.pId + ": " + this.pcb[p.pId - 1][4]);
        }
        System.out.println("Average turnaround time is: " + (avg_turnaround_time/this.terminated.size()));

        System.out.println();
        for(Process p : this.terminated) {
            avg_response_time += this.pcb[p.pId - 1][2];
            System.out.println("Response time for P" + p.pId + ": " + this.pcb[p.pId - 1][2]);
        }
        System.out.println("Average response time is: " + (avg_response_time /this.terminated.size()));

        System.out.println();
        for(Process p : this.terminated) {
            avg_wait_time += this.pcb[p.pId - 1][3];
            System.out.println("Wait time for P" + p.pId + ": " + this.pcb[p.pId - 1][3]);
        }

        System.out.println("Average wait time is: " + (avg_wait_time/this.terminated.size()));
        System.out.printf("\nCPU Utilization: %.2f", ((this.time_execution - cpu_util) / this.time_execution) * 100);
        System.out.print("%\n");
    }
}
