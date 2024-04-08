package com.music;

import com.music.v1.FFmpeg;
import com.music.v1.IAudioVideo;
import com.music.v1.OS;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FFmpegTest {

    private IAudioVideo ffmpeg;

    @BeforeEach
    public void setUp() throws Exception {
        ffmpeg = new FFmpeg("ffmpeg", OS.getEnv());
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void volume() {
        String val = ffmpeg.volume("//in.mp4", "//out.mp4", 11);
        Assertions.assertEquals("ffmpeg -i \"//in.mp4\" -map 0 -c copy -c:a aac -af \"volume=11dB\" \"//out.mp4\"",
                val);
    }

    @Test
    public void encode() {
        String val = ffmpeg.encode("//in.mp4", "//out.mp4", 11, FFmpeg.getEncoders().get(2), IAudioVideo.videoResolution.get(2));
        Assertions.assertEquals("ffmpeg -i \"//in.mp4\" -vf scale=-1:360 -map 0 -c copy -c:v libaom-av1 -preset medium -crf 11 \"//out.mp4\"",
                val);
    }

    @Test
    public void importSubtitles() {
        String val = ffmpeg.importSubtitles("//in.mp4", "//out.mp4", new IAudioVideo.Subtitles(new File("//in.srt"), "en"));
        Assertions.assertEquals("ffmpeg -i \"//in.mp4\" -sub_charenc UTF-8 -i \"//in.srt\" -map 0:v -map 0:a -c copy -map 1 -c:s:0 srt -metadata:s:s:0 language=en \"//out.mp4\"",
                val);
    }

    @Test
    public void importMultipleSubtitles() {
        IAudioVideo.Subtitles[] subtitles = {new IAudioVideo.Subtitles(new File("//en.srt"), "en"), new IAudioVideo.Subtitles(new File("//hi.srt"), "hi")};
        String val = ffmpeg.importSubtitles("//in.mp4", "//out.mp4", subtitles);
        Assertions.assertEquals("ffmpeg -i \"//in.mp4\" -sub_charenc UTF-8 -i \"\\\\en.srt\" -i \"\\\\hi.srt\" -map 0 -c copy -map 1 -map 2 -c:s:0 srt -metadata:s:s:0 language=en -c:s:1 srt -metadata:s:s:1 language=hi \"//out.mp4\"",
                val);
    }

    @Test
    public void sample_1() {
        List<Function<String, String>> l = new ArrayList<>();
        l.add(f -> ffmpeg.importSubtitles(f, f, new IAudioVideo.Subtitles(new File(f), "en")));

        l.stream().map(e -> e).forEach(System.out::println);
        l.stream().map(e -> e.apply("//in.mp4")).forEach(System.out::println);
    }

    @Test
    public void sample_2() {
        File out = new File("//out.mp4");

        List<Function<String, String>> l = new ArrayList<>();
        l.add(f -> ffmpeg.importSubtitles(f, "//out.mp4", new IAudioVideo.Subtitles(new File(f), "en")));

        l.stream().map(e -> e.apply("//in.mp4")).forEach(System.out::println);
    }
}