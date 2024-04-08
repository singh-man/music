package com.music.v2;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.IntStream;

public class AVMain {

    public static void main(String[] args) {

        String inFile = "05.mkv";
        AVData avData = new AVData(inFile);

//        Function<AVData, AVData> encode = avData1 -> IAV.encode(avData1);
//        Function<AVData, AVData> volume = avData1 -> IAV.volume(avData1);
//
//        Function<AVData, AVData> avDataAVDataFunction = encode.andThen(volume);
//        AVData apply = avDataAVDataFunction.apply(avData);
//

        Function<AVData, AVData> x;
        OPREnum chose = chose();
        x = chose.getOpr();

        for(;chose != OPREnum.DONE;) {
            chose = chose();
            x = x.andThen(chose.getOpr());
        }
        x.apply(avData);
        avData.getCommands().forEach(System.out::println);

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
