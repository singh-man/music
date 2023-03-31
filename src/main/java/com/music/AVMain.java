package com.music;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class AVMain {

    public static void main(String[] args) {

        Function<String, String> f1 = s -> s + "a";
        Function<String, String> f2 = s -> s + "b";
        Function<String, String> f3 = s -> s + "c";
        Function<String, String> f4 = s -> s + "z";

        String x = f1
                .andThen(f2)
                .andThen(f3)
                .compose(f4)
                .apply("x");

        System.out.println(x);

        OPREnum chose = chose();

        BiFunction<String, String, String> opr = chose.getOpr();

        for (OPREnum e : OPREnum.values()) {
            opr.andThen(s -> "");
        }


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
