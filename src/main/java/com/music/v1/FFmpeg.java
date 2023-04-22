package com.music.v1;

import java.util.*;

public class FFmpeg implements IAudioVideo {

    private String library;
    private OS os;

    public FFmpeg(String library, OS os) {
        this.library = library;
        this.os = os;
    }

    @Override
    public String volume(String in, String out, int db) {
        return String.format("%s -i %s -map 0 -c copy -c:a aac -af \"volume=%sdB\" %s", library, addDoubleQuotes(in), db, addDoubleQuotes(out));
    }

    @Override
    public String encode(String in, String out, int crf, String encoder, String resolution) {
        return String.format("%s -i %s -vf scale=%s -map 0 -c copy -c:v %s -preset medium -crf %s %s",
                library, addDoubleQuotes(in), resolution, encoder, crf, addDoubleQuotes(out));
    }

    public String importSubtitldes(String in, String out, Subtitles subtitles) {
        Map<Integer, String> m = new HashMap<>();
        m.put(1, "%s -i %s -sub_charenc UTF-8 -i %s -map 0:v -map 0:a -c copy -map 1 -c:s:0 srt -metadata:s:s:0 language=en %s");
        m.put(2, "%s -i %s -c:a aac -vf subtitles=%s %s");
        String cmd = null;
        if(subtitles.file == null || in.equals(subtitles.file.getAbsolutePath()))
            cmd = String.format(m.get(2), library, addDoubleQuotes(in), addDoubleQuotes(in), addDoubleQuotes(out));
        else
            cmd = String.format(m.get(1), library, addDoubleQuotes(in), addDoubleQuotes(subtitles.file.getAbsolutePath()), addDoubleQuotes(out));
        return cmd;
    }

    /**
     * ffmpeg -i "in.mp4" -sub_charenc UTF-8 -i "en.srt" -i "hi.srt"
     * -map 0 -c copy -map 1 -map 2
     * -c:s:0 srt -metadata:s:s:0 language=en
     * -c:s:1 srt -metadata:s:s:1 language=hi
     * "out.mp4"
     * @param in
     * @param out
     * @param subtitles
     * @return
     */
    @Override
    public String importSubtitles(String in, String out, Subtitles... subtitles) {
        StringBuilder x = new StringBuilder();
        x.append(String.format("%s -i %s -sub_charenc UTF-8", library, addDoubleQuotes(in)));
        for(Subtitles s : subtitles) x.append(" -i " + addDoubleQuotes(s.file.getAbsolutePath()));
        x.append(" -map 0 -c copy");
        for(int i =1; i <= subtitles.length; i++) x.append(" " + String.format("-map %s", i));
        for(int i =0; i < subtitles.length; i++) {
            x.append(" " + String.format("-c:s:%s srt -metadata:s:s:%s language=%s", i, i, subtitles[i].language));
        }
        x.append(" " + addDoubleQuotes(out));
        return x.toString();
    }

    @Override
    public String importSubtitles(String in, String out, Subtitles subtitle) {
        return null;
    }

    public static List<String> getEncoders() {
        return Collections.unmodifiableList(Arrays.asList("libx264", "libx265", "libaom-av1", "libvpx-vp9"));
    }
}
