package com.music.v1;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

public class AVTerminal {

    private static Function<String, String> userInputs(String text, String flag) {
        return e -> {
            Scanner s = new Scanner(System.in);
            System.out.println(text + ": ");
            String x = s.nextLine();
            return e + " " + flag + " " + x;
        };
    }

    public static void main(String[] args) {
        String dir = "C:\\mani\\video\\x\\thePromisedNeverland";
        String file = "C:\\mani\\video\\x\\thePromisedNeverland\\01.mp4";
        AVTerminalViewModel avTerminalViewModel = AVTerminalViewModel.INSTANCE;

        Scanner s = new Scanner(System.in);
        System.out.println("enter file");
        String x = s.nextLine();
        Operations o = chose();
        Function<AVTerminalViewModel.CmdsR, AVTerminalViewModel.CmdsR> u = cmdsR -> cmdsR;
        for (; o != Operations.DONE; ) {
            u = u.andThen(o.getFunc()); // andThen returns a new function and apply func is called on the last func in chaining.
            o = chose();
        }

        AVTerminalViewModel.CmdsR cmdsR = new AVTerminalViewModel.CmdsR(null, file);
        AVTerminalViewModel.CmdsR res1 = u.apply(cmdsR);
        res1.getCmd().forEach(System.out::println);
    }

    private static Operations chose() {
        List<String> opr = Operations.ENCODE.getOperations();
        IntStream.range(0, opr.size())
                .forEach(i -> System.out.println(i + " : " + opr.get(i).toString()));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Chose Option: ");
        String s = scanner.nextLine();
        return Operations.values()[Integer.parseInt(s)];
    }

    private static void closureInJava_1() {
        System.out.println("Closure in java:");
        Function<String, Function<String, Integer>> f = s -> {
            AtomicInteger counter = new AtomicInteger(0);
            return s1 -> counter.incrementAndGet();
        };
        Function<String, Integer> f1 = f.apply("");
        System.out.println(f1.apply(""));
        System.out.println(f1.apply(""));
        System.out.println(f1.apply(""));
        System.out.println(f1.apply(""));
    }

    /**
     * This function will be called imediatly and the string x will be calculated and
     * processed in the returning function
     *
     * @param text
     * @param flag
     * @return
     */
    private static Function<String, String> closureInJava_2(String text, String flag) {
        String x = "**" + flag + "**";
        System.out.println("X: is preprocessed and returning func is lazy processed: " + x);
        return e -> e + " " + flag + " " + x;
    }

//    @Test
    public void testClosure() {
        closureInJava_1();
        String x = closureInJava_2("Enter File", "-i")
                .andThen(closureInJava_2("Enter srt", "-f"))
                .apply("ffmpeg");
        System.out.println(x);
    }
}
