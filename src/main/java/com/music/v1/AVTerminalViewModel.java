package com.music.v1;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public enum AVTerminalViewModel {

    INSTANCE;

    private IAudioVideo iav = new FFmpeg("ffmpeg", OS.getEnv());

    public static class CmdsR {
        private List<String> cmd;
        private String name;

        public CmdsR(List<String> cmd, String name) {
            this.cmd = Optional.ofNullable(cmd).orElse(new ArrayList<>());
            this.name = name;
        }

        public List<String> getCmd() {
            return cmd;
        }
    }

    public String input(String x, String... y) {
        Scanner scan = new Scanner(System.in);
        if (y != null) {
            IntStream.range(0, y.length)
                    .forEach(i -> System.out.println(i + " : " + y[i]));
        }
        System.out.println(x + ": \n");
        return scan.nextLine();
    }

    public Function<CmdsR, CmdsR> encode() {
        return e -> {
            String crf = input("Enter CRF", null);
            String lib = input("Chose encoder", IAudioVideo.videoEncoders.toArray(new String[IAudioVideo.videoEncoders.size()]));
            String out = IAudioVideo.replaceExtension(e.name, "_" + lib + ".mkv");
            String x = iav.encode(e.name, out, Integer.parseInt(crf), lib, "-1:-1");
            e.cmd.add(x);
            e.name = out;
            return e;
        };
//
//        String dirOrFile = input("Enter file or folder", null);

//        List<String> encode = new FFmpegController()
//                .encode(dirOrFile, Integer.parseInt(crf), IAudioVideo.videoEncoders.get(Integer.parseInt(lib)), "-1:-1");
//        return encode;
    }

    public Function<CmdsR, CmdsR> volumes() {
        return e -> {
            String db = input("Enter decibel", null);
            String out = IAudioVideo.replaceExtension(e.name, "_v" + db + ".mkv");
            String volume = iav.volume(e.name, out, Integer.parseInt(db));
            e.cmd.add(volume);
            e.name = out;
            return e;
        };
    }

    public List<String> importSubtitles() {
        String file = input("Enter file", null);
        String subtitle = input("Enter subtitle file", null);
        if (subtitle.isEmpty()) subtitle = IAudioVideo.replaceExtension(file, ".srt");
        String subtitles = new AVController().importSubtitles(file, subtitle, "en");
        return Arrays.asList(subtitles);
    }

    public Function<CmdsR, CmdsR> importSubtitless() {
        return e -> {
            String subtitle = input("Enter subtitle file", null);
            String in = e.name;
            String out = IAudioVideo.replaceExtension(in, "_en.mkv");
            if (subtitle.isEmpty()) subtitle = IAudioVideo.replaceExtension(in, ".srt");
//            String subtitles = new FFmpegController().importSubtitles(file, subtitle, "en");
            String en = iav.importSubtitles(in, out, new IAudioVideo.Subtitles(new File(subtitle), "en"));
            e.getCmd().add(en);
            e.name = out;
            return e;
        };
    }

}
