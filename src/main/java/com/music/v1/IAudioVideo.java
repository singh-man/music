package com.music.v1;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IAudioVideo {

    List<String> videoResolution = Collections.unmodifiableList(Arrays.asList("-1:-1", "426:240", "-1:360", "852:480",
            "-1:720", "-1:1080"));

    List<String> videoFileExtensions = Collections.unmodifiableList(Arrays.asList(".mkv", ".avi", ".mp4", ".xvid", ".divx"));

    List<String> videoEncoders = Collections.unmodifiableList(Arrays.asList("libx264", "libx265", "libaom-av1", "libvpx-vp9"));

    String volume(String in, String out, int db);

    String encode(String in, String out, int crf, String encoder, String resolution);

    String importSubtitles(String in, String out, Subtitles... subtitles);

    String importSubtitles(String in, String out, Subtitles subtitle);

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
     *  call example ===> addDoubleQuotes().apply(str)
     */
    default Function<String, String> addDoubleQuotes() {
        return x -> "\"" + x + "\"";
    }

    default String addDoubleQuotes(String str) {
        addDoubleQuotes().apply(str);
        // OR
        strTransform(str, addDoubleQuotes());
        // OR
        return strTransform(str, x -> "\"" + x + "\"");
    }

    /**
     * call example ===> strTransform("qwer", x -> "\"" + x + "\"");
     * @return "qwer"
     */
    default String strTransform(String str, Function<String, String> f) {
        return f.apply(str);
    }

    /**
     * Way to call is ====>  replaceExtension().apply(File, "_.mkv")
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
}