package com.music.v4;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.IntStream;

public class AVMain {

    public static void main(String[] args) {

        String inFile = "05.mkv";
        AVCommand avData = new AVCommand(inFile);

//        AVCommand apply = AV_FFMPEG.encode().andThen(AV_FFMPEG.volume()).apply(avData);
//        System.out.println(apply.getCmd());

        Function<AVCommand, AVCommand> x;
        OPREnum chose = chose();
        x = chose.getOpr();

        for (; chose != OPREnum.DONE; ) {
            chose = chose();
            x = x.andThen(chose.getOpr());
        }
        AVCommand apply = x.apply(avData);
        System.out.println(apply.toString());
    }

    static String input(String x, String... y) {
        Scanner scan = new Scanner(System.in);
        if (y != null) {
            IntStream.range(0, y.length)
                    .forEach(i -> System.out.println(i + " : " + y[i]));
        }
        System.out.println(x + ": \n");
        return scan.nextLine();
    }

    private static OPREnum chose() {
        List<OPREnum> opr = Arrays.asList(OPREnum.values());
        IntStream.range(0, opr.size())
                .forEach(i -> System.out.println(i + " : " + opr.get(i).toString()));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Chose Option: ");
        String s = scanner.nextLine();
        return OPREnum.values()[Integer.parseInt(s)];
    }

}
