package com.music.v4;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public interface IAV {

    String volume(File inFile, int volume, File outFile);

    String encode(File inFile, String resolution, IVideoEncoder encoder, int crf, File outFile);

    List<String> videoResolution = List.of("-1:-1", "426:240", "-1:360", "852:480", "-1:720", "-1:1080");

    List<String> videoFileExtensions = List.of(".mkv", ".avi", ".mp4", ".xvid", ".divx");

    class Subtitles {
        File file;
        String language;

        public Subtitles(File file, String language) {
            this.file = file;
            this.language = language;
        }
    }

    static String replaceExtension(File file, String newExt) {
        String ext = file.getName().substring(file.getName().lastIndexOf('.'));
        return file.getName().replace(ext, newExt);
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
}
