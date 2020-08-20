package com.fau.schedulers;

import java.util.*;

import com.fau.process.Process;

public class MLFQ extends Scheduler {

    private final int RR_TQ_QUEUE_1 = 5;
    private final int RR_TQ_QUEUE_2 = 10;

    private Queue<Process> queue_1;
    private Queue<Process> queue_2;
    private Queue<Process> queue_3;

    public MLFQ() {
        queue_1  = new LinkedList();
        queue_2  = new LinkedList();
        queue_3  = new LinkedList();
    }

    public void start() {
        for(Process p : ready) {
            queue_1.add(p);
        }

        while(!queue_1.isEmpty() || !queue_2.isEmpty() || !queue_3.isEmpty() || !device.isEmpty()) {

            while(!queue_1.isEmpty()) {
                System.out.println();
                System.out.println();
                System.out.println("Q1: " + this.q1_toString() );
                System.out.println("Q2: " + this.q2_toString() );
                System.out.println("Q3: " + this.q3_toString() );
                System.out.println("DQ: " + this.devicequeue_toString() );

                Process p = queue_1.remove();
                if(pcb[p.pId - 1][0] == 0) { pcb[p.pId - 1][0] = 1; pcb[p.pId - 1][2] = time_execution; } // time response

                if(p.cpu_burst.get(pcb[p.pId - 1][1]) <= RR_TQ_QUEUE_1) { // run in full
                    time_execution += p.cpu_burst.get(pcb[p.pId - 1][1]);
                    System.out.println("[Te: " + time_execution + "] - Process " + p.pId + " ran");
                    System.out.print("[Te: " +  time_execution + "] Total execution has finished: ");
                    try {
                        pcb[p.pId - 1][3] += (time_execution - p.cpu_burst.get(pcb[p.pId - 1][1])) - p.io_wait;
                        p.io_wait = time_execution + p.io_burst.get(pcb[p.pId - 1][1]);
                        device.add(p);
                        System.out.print("No");
                    } catch(IndexOutOfBoundsException e) {
                        terminated.add(p);
                        System.out.println("Yes");
                        pcb[p.pId - 1][4] = time_execution;
                    }

                } else { // dont run in full, downgrade process
                    time_execution += RR_TQ_QUEUE_1;
                    System.out.println("[Te: " + time_execution + "] - Process " + p.pId + " ran");
                    p.cpu_burst.set(pcb[p.pId - 1][1], p.cpu_burst.get(pcb[p.pId - 1][1]) - RR_TQ_QUEUE_1);
                    queue_2.add(p);
                    p.queue = 2; // track which queue process belongs in (processes can never be upgraded after being downgraded)
                    System.out.println("[Te: " + time_execution + "] Total execution has finished: No");
                }

                check_io();
            }

            while(!queue_2.isEmpty() && queue_1.isEmpty()) {
                System.out.println();
                System.out.println();
                System.out.println("Q1: " + this.q1_toString() );
                System.out.println("Q2: " + this.q2_toString() );
                System.out.println("Q3: " + this.q3_toString() );
                System.out.println("DQ: " + this.devicequeue_toString() );


                Process p = queue_2.remove();
                if(p.cpu_burst.get(pcb[p.pId - 1][1]) <= RR_TQ_QUEUE_2) { // run in full
                    time_execution += p.cpu_burst.get(pcb[p.pId - 1][1]);
                    System.out.println("[Te: " + time_execution + "] - Process " + p.pId + " ran");
                    System.out.print("[Te: " +  time_execution + "] Total execution has finished: ");

                    try {
                        pcb[p.pId - 1][3] += (time_execution - p.cpu_burst.get(pcb[p.pId - 1][1])) - p.io_wait;
                        p.io_wait = time_execution + p.io_burst.get(pcb[p.pId - 1][1]);
                        device.add(p);
                        System.out.print("No");
                    } catch(IndexOutOfBoundsException e) {
                        terminated.add(p);
                        pcb[p.pId - 1][4] = time_execution;
                        System.out.print("Yes");
                    }
                } else { // dont run in full, downgrade process
                    time_execution += RR_TQ_QUEUE_2;
                    System.out.println("[Te: " + time_execution + "] - Process " + p.pId + " ran");
                    System.out.print("[Te: " +  time_execution + "] Total execution has finished: No");

                    p.cpu_burst.set(pcb[p.pId - 1][1], p.cpu_burst.get(pcb[p.pId - 1][1]) - RR_TQ_QUEUE_2);
                    queue_3.add(p);
                    p.queue = 3; // track which queue process belongs in (processes can never be upgraded after being downgraded)
                }
                check_io();
            }

            while(!queue_3.isEmpty() && queue_1.isEmpty() && queue_2.isEmpty()) { // fcfs ** possibly where things are going wrong
                System.out.println();
                System.out.println();

                System.out.println("Q1: " + this.q1_toString() );
                System.out.println("Q2: " + this.q2_toString() );
                System.out.println("Q3: " + this.q3_toString() );
                System.out.println("DQ: " + this.devicequeue_toString() );

                Process p = queue_3.remove();
                time_execution += p.cpu_burst.get(pcb[p.pId - 1][1]);
                System.out.println("[Te: " + time_execution + "] - Process " + p.pId + " ran");
                System.out.print("[Te: " +  time_execution + "] Total execution has finished: ");

                try {
                    pcb[p.pId - 1][3] += (time_execution - p.cpu_burst.get(pcb[p.pId - 1][1])) - p.io_wait;
                    p.io_wait = time_execution + p.io_burst.get(pcb[p.pId - 1][1]);
                    device.add(p);
                    System.out.print("No");
                } catch(IndexOutOfBoundsException e) {
                    terminated.add(p);
                    pcb[p.pId - 1][4] = time_execution;
                    System.out.print("Yes");
                }

                check_io();
            }
        }

        super.print_analytics();
    }

    public void check_io() {
         if(!device.isEmpty()) { // might want to add multiple processes
            Process p = device.peek();
            for(Process process : device) {
                if(process.io_wait < p.io_wait) {
                    p = process;
                }
            }

            if(p.io_wait <= time_execution) {
                device.remove(p);

                pcb[p.pId - 1][1] += 1;
                switch(p.queue) {
                    case 1: queue_1.add(p); break;
                    case 2: queue_2.add(p); break;
                    case 3: queue_3.add(p); break;
                }
            }
         }

         if(!device.isEmpty() && queue_1.isEmpty() && queue_2.isEmpty() && queue_3.isEmpty()) { // cpu is going to be idle
             Process p = device.peek();
             for(Process process : device) {
                 if(process.io_wait < p.io_wait) {
                     p = process;
                 }
             }

             super.cpu_util += (p.io_wait - time_execution);
             time_execution += p.io_wait - time_execution;
             device.remove(p);

             pcb[p.pId - 1][1] += 1;
             switch(p.queue) {
                 case 1: queue_1.add(p); break;
                 case 2: queue_2.add(p); break;
                 case 3: queue_3.add(p); break;
             }
         }
    }

    private String q1_toString() {
        if (this.queue_1.isEmpty()) return "Empty";

        StringBuilder sb = new StringBuilder();
        for(Process p : this.queue_1) {
            sb.append("P" + p.pId + "(" + p.cpu_burst.get(this.pcb[p.pId - 1][1]) + ") ");
        }
        return sb.toString();
    }

    private String q2_toString() {
        if (this.queue_2.isEmpty()) return "Empty";

        StringBuilder sb = new StringBuilder();
        for(Process p : this.queue_2) {
            sb.append("P" + p.pId + "(" + p.cpu_burst.get(this.pcb[p.pId - 1][1]) + ") ");
        }
        return sb.toString();
    }

    private String q3_toString() {
        if (this.queue_3.isEmpty()) return "Empty";

        StringBuilder sb = new StringBuilder();
        for(Process p : this.queue_3) {
            sb.append("P" + p.pId + "(" + p.cpu_burst.get(this.pcb[p.pId - 1][1]) + ") ");
        }
        return sb.toString();
    }
}
