package com.music.memoryLeak;

import java.util.ArrayList;
import java.util.List;

/**
 * This genearates the MemoryLeak; run it and quickly attach visualVM to generate heap dump
 */
public class MemLeakRunner {

    public static void main(String[] args) throws InterruptedException {

        MemoryLeak manish = new MemoryLeak();
        List<Integer> x = new ArrayList<>();
        try {
//            int[] y = new int[Integer.MAX_VALUE];
            for (int i = 0; i < Integer.MAX_VALUE/30; i++) {
                x.add(i);
                manish.x.add(i);
            }
            Thread.sleep(10000l);
            System.out.println("No print");
        } catch (OutOfMemoryError e) {
            System.out.println("Out of Memory");
            Thread.sleep(50000l);
            x = new ArrayList<>();
            System.out.println("Memory cleaned");
            Thread.sleep(50000l);
            for (int i = 0; i < 100000; i++) {
                x.add(i);
            }
            System.out.println("After memory handling");
//            x = null;
        }
        System.out.println("memory handled");
    }
}
