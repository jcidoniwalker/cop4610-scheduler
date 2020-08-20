package com.fau.schedulers;

import com.fau.process.Process;
import java.util.Collections;
import java.util.Comparator;

public class FCFS extends Scheduler {

    public void start() {
        while(!super.ready.isEmpty()) {
            System.out.println("[Information] RQ: " + rq_toString());
            System.out.println("[Information] DQ: " + devicequeue_toString());
            Process process = super.ready.remove(); // remove process from the queue

            if(super.pcb[process.pId - 1][0] == 0) { // if the process is running for the first time
                super.pcb[process.pId - 1][2] = super.time_execution; // record response time
                super.pcb[process.pId - 1][3] += super.time_execution; // save time waiting in ready queue
                super.pcb[process.pId - 1][0] = 1; // save that the process has ran
            }

            super.time_execution += process.cpu_burst.get(super.pcb[process.pId - 1][1]); // run a cpu burst
            System.out.println("[Te: " + super.time_execution + "] Process " + process.pId + " has ran");

            /* This should be modified in the future to not depend on an exception */
            System.out.print("[Te: " + super.time_execution + "] Total execution finished: ");
            try {
                process.io_wait = super.time_execution + process.io_burst.get(super.pcb[process.pId - 1][1]); // store the time that the process will become available
                System.out.println("No");
                super.device.add(process); // Add to the device/io queue
            } catch (IndexOutOfBoundsException e) { // If this exception occurs, the process is finished (There are no more IO-bursts)
                super.terminated.add(process); // Add to the terminated queue
                super.pcb[process.pId - 1][4] = super.time_execution; // Record turn around time
                System.out.println("Yes");
            } finally { System.out.println(); }

            if(super.ready.isEmpty() && !super.device.isEmpty()) {
                Process p = super.device.peek();
                for(Process temp_p : super.device) { // This loop finds the process in the device queue that is soonest to become eligible to run
                    if(temp_p.io_wait < p.io_wait) {
                        p = temp_p;
                    }
                }

                if(super.time_execution < p.io_wait) { // If the soonest available process is greater than the current time execution, idle
                    System.out.println("[CPU] - Idling");
                    cpu_util += (p.io_wait - super.time_execution); // add idle time for analytics
                    super.time_execution += p.io_wait - super.time_execution;
                }

                super.pcb[p.pId - 1][1] +=1 ;
                super.ready.add(p);
                super.device.remove(p);
                super.pcb[process.pId - 1][3] += super.time_execution - p.io_wait; // add to wait time, for analytics
            }
        }

        super.print_analytics();
    }

}
