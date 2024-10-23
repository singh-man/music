package com.music.v4;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.IntStream;

public interface IAV {

    String changeContainer(File inFile, String container, File outFile);

    String volume(File inFile, int volume, File outFile);

    String encode(File inFile, String resolution, IVideoEncoder encoder, int crf, File outFile);

    List<String> videoResolution = List.of("-1:-1", "426:240", "-1:360", "852:480", "-1:720", "-1:1080");

    List<String> videoFileExtensions = List.of(".mkv", ".avi", ".mp4", ".xvid", ".divx");

    List<String> videoContainer = List.of(".mkv", ".mp4");

    class Subtitles {
        File file;
        String language;

        public Subtitles(File file, String language) {
            this.file = file;
            this.language = language;
        }
    }

    static String replaceExtension(File file, String newExt) {
        return file.getName().substring(0, file.getName().lastIndexOf('.')) + newExt;
    }

    default String addDoubleQuotes(String text) {
        return "\"" + text + "\"";
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

    default AVCommand encode(AVCommand avCommand) {
        String resolution = IAV.input("enter resolution", IAV.videoResolution.toArray(new String[IAV.videoResolution.size()]));
        IVideoEncoder encoder = FFMPEG_videoEncoder.getEncoder(IAV.input("enter encoder", FFMPEG_videoEncoder.allNames()));
        int crf = Integer.parseInt(IAV.input("enter crf"));
        String out = IAV.replaceExtension(new File(avCommand.oprFile()), "_" + encoder.getName() + ".mkv");
        return avCommand.newAVCommand(out, this.encode(new File(avCommand.oprFile()), resolution, encoder, crf, new File(out)));
    }

    default AVCommand volume(AVCommand avCommand) {
        int db = Integer.parseInt(IAV.input("enter db to raise"));
        String out = IAV.replaceExtension(new File(avCommand.oprFile()), "_v" + db + ".mkv");
        return avCommand.newAVCommand(out, this.volume(new File(avCommand.oprFile()), db, new File(out)));
    }

    default AVCommand changeContainer(AVCommand avCommand) {
        String container = IAV.videoContainer.get(Integer.parseInt(IAV.input("choose container", IAV.videoContainer.toArray(new String[IAV.videoContainer.size()]))));
        String out = IAV.replaceExtension(new File(avCommand.oprFile()), container);
        return avCommand.newAVCommand(out, this.changeContainer(new File(avCommand.oprFile()), container, new File(out)));
    }
}
