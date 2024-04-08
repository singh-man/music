package com.music.v3;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public interface IAV {

    List<String> videoResolution = Collections.unmodifiableList(Arrays.asList("-1:-1", "426:240", "-1:360", "852:480",
            "-1:720", "-1:1080"));

    List<String> videoFileExtensions = Collections.unmodifiableList(Arrays.asList(".mkv", ".avi", ".mp4", ".xvid", ".divx"));

    List<String> videoEncoders = Collections.unmodifiableList(Arrays.asList("libx264", "libx265", "libaom-av1", "libvpx-vp9"));

    class Subtitles {
        File file;
        String language;

        public Subtitles(File file, String language) {
            this.file = file;
            this.language = language;
        }
    }

    static boolean isValidVideoFileExtension(File file) {
        if (!file.getName().contains(".")) return false;
        String ext = file.getName().substring(file.getName().lastIndexOf('.'));
        return videoFileExtensions.contains(ext);
    }

    static Predicate<File> isValidVideoFileExtension() {
        return f -> isValidVideoFileExtension(f);
    }

    /**
     * call example ===> addDoubleQuotes().apply(str)
     */
    static Function<String, String> addDoubleQuotes() {
        return x -> "\"" + x + "\"";
    }

    static String addDoubleQuotes(String str) {
        addDoubleQuotes().apply(str);
        // OR
        strTransform(str, addDoubleQuotes());
        // OR
        return strTransform(str, x -> "\"" + x + "\"");
    }

    /**
     * call example ===> strTransform("qwer", x -> "\"" + x + "\"");
     *
     * @return "qwer"
     */
    static String strTransform(String str, Function<String, String> f) {
        return f.apply(str);
    }

    /**
     * Way to call is ====>  replaceExtension().apply(File, "_.mkv")
     *
     * @return abc_.mkv
     */
    static BiFunction<File, String, File> replaceExtension() {
        return (f, newExt) -> {
            String ext = f.getName().substring(f.getName().lastIndexOf('.'));
            return new File(f.getAbsolutePath().replace(ext, newExt));
        };
    }

    default File replaceExtension(File f, String newExt) {
//        String ext = f.getName().substring(f.getName().lastIndexOf('.'));
//        return new File(f.getAbsolutePath().replace(ext, newExt));
        return new File(replaceExtension(f.getAbsolutePath(), newExt));
    }

    static String replaceExtension(String f, String newExt) {
        String ext = f.substring(f.lastIndexOf('.'));
        return f.replace(ext, newExt);
    }

    static String timeDIff(String startTime, String endTime) {
        int seconds = (int) Duration.between(LocalTime.parse(startTime), LocalTime.parse(endTime)).getSeconds();
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;

        p2 = p2 / 60;
        System.out.print("HH:MM:SS - " + p2 + ":" + p3 + ":" + p1);
        System.out.print("\n");
        return p2 + ":" + p3 + ":" + p1;
    }

    static AVCommand volume(AVCommand data) {
        String db = input("enter db to raise");
        String out = replaceExtension(data.getOutFile(), "_v" + db + ".mkv");
        String command = String.format("%s -i %s -map 0 -c copy -c:a aac -af \"volume=%sdB\" %s", "ffmpeg", IAV.addDoubleQuotes(data.getOutFile()),
                db, IAV.addDoubleQuotes(out));
        return data.addCmd(out, command);
    }

    static AVCommand encode(AVCommand data) {
        String resolution = input("enter resolution", IAV.videoResolution.toArray(new String[IAV.videoResolution.size()]));
        String encoder = input("enter encoder", IAV.videoEncoders.toArray(new String[IAV.videoEncoders.size()]));
        String crf = input("enter crf");
        String out = replaceExtension(data.getOutFile(), "_" + videoEncoders.get(Integer.parseInt(encoder)) + ".mkv");
        String forAV1 = videoEncoders.get(Integer.parseInt(encoder)).equals("libaom-av1") ? "-cpu-used 4" : "";
        String command = String.format("%s -i %s -vf scale=%s -map 0 -c copy -c:v %s -preset medium %s -crf %s %s",
                "ffmpeg", IAV.addDoubleQuotes(data.getOutFile()), videoResolution.get(Integer.parseInt(resolution)),
                videoEncoders.get(Integer.parseInt(encoder)), forAV1, crf, IAV.addDoubleQuotes(out));
        return data.addCmd(out, command);
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