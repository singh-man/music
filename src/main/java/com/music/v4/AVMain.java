package com.music.v4;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.IntStream;

public class AVMain {

    public static void main(String[] args) {

        String inFile = "05.mkv";
        AVCommand avData = new AVCommand(inFile);

//        AVCommand apply = encode().andThen(volume()).apply(avData);
//
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

    static Function<AVCommand, AVCommand> encode() {
        String resolution = input("enter resolution", IAV.videoResolution.toArray(new String[IAV.videoResolution.size()]));
        IVideoEncoder encoder = FFMPEG_videoEncoder.getEncoder(input("enter encoder", FFMPEG_videoEncoder.allNames()));
        int crf = Integer.parseInt(input("enter crf"));
        return avCommand -> {
            String out = IAV.replaceExtension(new File(avCommand.getOprFile()), "_" + encoder.getName() + ".mkv");
            return avCommand.newAVCommand(out, new FFMPEG_AudioVideo().encode(new File(avCommand.getOprFile()), resolution, encoder, crf, new File(out)));
        };
    }

    static Function<AVCommand, AVCommand> volume() {
        int db = Integer.parseInt(input("enter db to raise"));
        return avCommand -> {
            String out = IAV.replaceExtension(new File(avCommand.getOprFile()), "_v" + db + ".mkv");
            return avCommand.newAVCommand(out, new FFMPEG_AudioVideo().volume(new File(avCommand.getOprFile()), db, new File(out)));
        };
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
