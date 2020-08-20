package com.fau;

import com.fau.process.Process;

import com.fau.schedulers.FCFS;
import com.fau.schedulers.MLFQ;
import com.fau.schedulers.SJF;

public class Main {

    public static void main(String[] args) {
        Process p1 = new Process(1, new int[] {5, 27, 3, 31, 5, 43, 4, 18, 6, 22, 4, 26, 3, 24, 4});
        Process p2 = new Process(2, new int[] {4, 48, 5, 44, 7, 42, 12, 37, 9, 76, 4, 41, 9, 31, 7, 43, 8});
        Process p3 = new Process(3, new int[] {8, 33, 12, 41, 18, 65, 14, 21, 4, 61, 15, 18, 14, 26, 5, 31, 6});
        Process p4 = new Process(4, new int[] {3, 35, 4, 41, 5, 45, 3, 51, 4, 61, 5, 54, 6, 82, 5, 77, 3});
        Process p5 = new Process(5, new int[] {16, 24, 17, 21, 5, 36, 16, 26, 7, 31, 13, 28, 11, 21, 6, 13, 3, 11, 4});
        Process p6 = new Process(6, new int[] {11, 22, 4, 8, 5, 10, 6, 12, 7, 14, 9, 18, 12, 24, 15, 30, 8});
        Process p7 = new Process(7, new int[] {14, 46, 17, 41, 11, 42, 15, 21, 4, 32, 7, 19, 16, 33, 10});
        Process p8 = new Process(8, new int[] {4, 14, 5, 33, 6, 51, 14, 73, 16, 87, 6});

        p1.parse();
        p2.parse();
        p3.parse();
        p4.parse();
        p5.parse();
        p6.parse();
        p7.parse();
        p8.parse();

        FCFS fcfs = new FCFS();
        fcfs.add(p1);
        fcfs.add(p2);
        fcfs.add(p3);
        fcfs.add(p4);
        fcfs.add(p5);
        fcfs.add(p6);
        fcfs.add(p7);
        fcfs.add(p8);
        //fcfs.start();

        SJF sjf = new SJF();
        sjf.add(p1);
        sjf.add(p2);
        sjf.add(p3);
        sjf.add(p4);
        sjf.add(p5);
        sjf.add(p6);
        sjf.add(p7);
        sjf.add(p8);
        //sjf.start();

        MLFQ mlfq = new MLFQ();
        mlfq.add(p1);
        mlfq.add(p2);
        mlfq.add(p3);
        mlfq.add(p4);
        mlfq.add(p5);
        mlfq.add(p6);
        mlfq.add(p7);
        mlfq.add(p8);
        mlfq.start();
    }
}
