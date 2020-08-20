package com.fau.process;

import java.util.LinkedList;
import java.util.List;

public class Process {

    public int pId;

    int[] burst;

    public List<Integer> cpu_burst;
    public List<Integer> io_burst;

    public int io_wait = 0;

    public int queue = 1;

    public Process(int pId, int[] burst) {
        this.pId = pId;
        this.burst = burst;

        cpu_burst = new LinkedList<Integer>();
        io_burst = new LinkedList<Integer>();
    }

    public void parse() {
        for(int i = 0; i < burst.length; i++) {
            if(i % 2 == 0) {
                cpu_burst.add(burst[i]);
            } else {
                io_burst.add(burst[i]);
            }
        }
    }
}
