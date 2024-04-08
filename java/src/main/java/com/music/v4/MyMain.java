package com.music.v4;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MyMain {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<String> submit = executorService.submit(() -> {
            System.out.println("going to sleep for 5 seconds!");
            Thread.sleep(5000l);
            return "i am done!";
        });

        List<String> a = List.of("a", "b", "c", "d", "e", "f");

        List<String> c = a.stream().filter(e -> !e.equals("c")).collect(Collectors.toList());
        System.out.println(c);

        Runnable g = () -> {
            try {
                Thread.sleep(3000l);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("I am adding g");
            a.remove("d");
        };
        new Thread(g).start();

        try {
            a.stream().forEach(e -> {
                System.out.println(e);
                try {
                    Thread.sleep(3000l);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getLocalizedMessage());
        }

        try {
            Thread.sleep(20000l);
            System.out.println(submit.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("I am here!");
        executorService.shutdown();
    }
}
