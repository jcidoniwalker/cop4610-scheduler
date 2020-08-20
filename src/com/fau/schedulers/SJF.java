package com.fau.schedulers;

import com.fau.process.Process;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class SJF extends Scheduler {

    public void start() {

        while (!ready.isEmpty()) {

            Collections.sort(ready, new Comparator<Process>() { // Sort the ready queue, so that the shortest job "pops"
                @Override
                public int compare(Process o1, Process o2) {
                    return o1.cpu_burst.get(pcb[o1.pId - 1][1]) - o2.cpu_burst.get(pcb[o2.pId - 1][1]);
                }
            });


            System.out.println("[Information] RQ: " + rq_toString());
            System.out.println("[Information] DQ: " + devicequeue_toString());
            Process process = ready.peek(); // get process with shortest cpu burst
            for(Process p : ready) {
                if(p.cpu_burst.get(pcb[p.pId - 1][1]) == process.cpu_burst.get(pcb[process.pId - 1][1]) && p.pId != process.pId) { // if burst == another burst, fcfs (pick one with lesser arrival time)
                    System.out.println("Process " + p.pId + " has the same CPU burst (" + p.cpu_burst.get(pcb[p.pId - 1][1]) + ") as Process " + process.pId);
                    if(p.io_wait < process.io_wait) {
                        int arrival_time = process.io_wait;
                        process = p;
                        System.out.println("Running Process " + process.pId + " because " + process.io_wait + " < " + arrival_time);
                    }
                }
            }

            if(pcb[process.pId - 1][0] == 0) { // for recording response time
                pcb[process.pId - 1][2] = super.time_execution;
                pcb[process.pId - 1][0] = 1;
            }

            // for recording wait time
            int wait_time = super.time_execution - process.io_wait;
            pcb[process.pId - 1][3] += wait_time;

            super.time_execution += process.cpu_burst.get(pcb[process.pId - 1][1]); // "run" the process
            System.out.println("[Te: " + super.time_execution + "] - " + "Process " + process.pId + " has ran");

            System.out.print("Total execution has finished: ");
            /* in the future, try and catch should not be used for flow control; only simluation project! */
            try { // if this block does not enter the exception, there are more io bursts
                process.io_wait = super.time_execution + process.io_burst.get(pcb[process.pId - 1][1]);
                device.add(process);
                System.out.println("No");

            } catch(IndexOutOfBoundsException e) { // if exception occurs, there are no more io bursts
                pcb[process.pId - 1][4] = super.time_execution;
                terminated.add(process);
                System.out.println("Yes");
            }

            ready.remove(process); // process ran, remove from ready queue

            if(!device.isEmpty()) { // check if any processes have become available from device queue

                LinkedList<Process> tmp = new LinkedList<>(); // temp store all processes that have become available
                for(Process p : device) {
                    if(p.io_wait <= super.time_execution) {
                        tmp.add(p);
                        ready.add(p); // add processes that have become available to ready queue
                    }
                }

                for(Process p : tmp) { // remove all processes that have become available from device queue
                    pcb[p.pId - 1][1] += 1;
                    device.remove(p);
                }
            }

            if(ready.isEmpty() && !device.isEmpty()) { // if this is true, cpu must idle
                Process temp_p = device.peek();
                for(Process p : device) { // find the process that is soonest to become available
                    if(p.io_wait < temp_p.io_wait) {
                        temp_p = p;
                    }
                }

                cpu_util += (temp_p.io_wait - super.time_execution);

                super.time_execution += (temp_p.io_wait - super.time_execution);

                ready.add(temp_p);
                pcb[temp_p.pId - 1][1] += 1;
                device.remove(temp_p);
            }

            System.out.println();

        }

        super.print_analytics();
    }
}
