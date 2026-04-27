use anyhow::{anyhow, Result};
use std::collections::HashMap;
use std::io::{self, Write};

use crate::directory_utils;
use crate::process_tools;
use crate::text_utils::{file_stem_and_ext, get_modified_timestamp_from_file, quote_file_path, replace_file_ext};

pub type ActionFn = fn() -> Result<Vec<String>>;

pub struct Action {
    pub name: &'static str,
    pub fun: ActionFn,
}

pub fn get_ffmpeg() -> &'static str {
    "ffmpeg"
}

pub fn actions() -> Vec<Action> {
    vec![
        Action {
            name: "ffmpeg_mp3ToM4a_libfdk_aac",
            fun: ffmpeg_mp3_to_m4a_libfdk_aac,
        },
        Action {
            name: "ffmpeg_mp3ToM4a",
            fun: ffmpeg_mp3_to_m4a,
        },
        Action {
            name: "ffmpeg_incVolume",
            fun: ffmpeg_inc_volume,
        },
        Action {
            name: "ffmpeg_encode",
            fun: ffmpeg_encode,
        },
        Action {
            name: "ffmpeg_cut",
            fun: ffmpeg_cut,
        },
        Action {
            name: "ffmpeg_concat",
            fun: ffmpeg_concat,
        },
        Action {
            name: "ffmpeg_concat2",
            fun: ffmpeg_concat2,
        },
        Action {
            name: "ffmpeg_import",
            fun: ffmpeg_import,
        },
        Action {
            name: "ffmpeg_toGif",
            fun: ffmpeg_to_gif,
        },
        Action {
            name: "ffmpeg_changeContainer",
            fun: ffmpeg_change_container,
        },
        Action {
            name: "ffmpeg_extractAudio",
            fun: ffmpeg_extract_audio,
        },
        Action {
            name: "ffmpeg_removeAudioTrack",
            fun: ffmpeg_remove_audio_track,
        },
        Action {
            name: "rename_filesAsPerModifiedTimestamp",
            fun: rename_files_as_per_modified_timestamp,
        },
    ]
}

fn read_input(prompt: &str) -> Result<String> {
    print!("{prompt}");
    io::stdout().flush()?;
    let mut s = String::new();
    io::stdin().read_line(&mut s)?;
    Ok(s.trim().to_string())
}

fn user_input(prompt: &str, options: &[&str]) -> Result<String> {
    for (idx, option) in options.iter().enumerate() {
        println!("{idx:<3} : {option:>3}");
    }
    let selected = read_input(&format!("{prompt}: "))?;
    if options.is_empty() {
        return Ok(selected);
    }
    let index: usize = selected.parse()?;
    options
        .get(index)
        .map(|s| (*s).to_string())
        .ok_or_else(|| anyhow!("invalid option index"))
}

fn get_file_or_folder_list(ext: &str) -> Result<Vec<String>> {
    let file_or_folder = read_input("Enter file(s) [multiple files can be space seperated] or folder path: ")?;
    if directory_utils::is_dir(&file_or_folder) {
        directory_utils::find_files(&file_or_folder, ext)
    } else {
        Ok(file_or_folder
            .split_whitespace()
            .map(|s| s.to_string())
            .collect())
    }
}

fn in_file(input_file: &str) -> String {
    format!("-i {}", quote_file_path(input_file))
}

fn out_file(output_file: &str) -> String {
    quote_file_path(output_file)
}

fn copy_all_stream() -> &'static str {
    "-map 0 -c copy"
}

fn to_m4a_with_libfdk_aac(input_file: &str, output_file: &str) -> String {
    format!(
        "{} {} -c:a libfdk_aac -profile:a aac_he_v2 -b:a 48k {}",
        get_ffmpeg(),
        in_file(input_file),
        out_file(output_file)
    )
}

fn to_wav_ffmpeg(input_file: &str, output_file: &str) -> String {
    format!("{} {} {}", get_ffmpeg(), in_file(input_file), out_file(output_file))
}

fn to_m4a_with_builtin_aac(input_file: &str, output_file: &str) -> String {
    format!(
        "{} {} -c:a aac -b:a 48k {}",
        get_ffmpeg(),
        in_file(input_file),
        out_file(output_file)
    )
}

fn inc_volume_ffmpeg(input_file: &str, output_file: &str, db: &str) -> String {
    format!(
        "{} {} {} -c:a aac -af \"volume={}dB\" {}",
        get_ffmpeg(),
        in_file(input_file),
        copy_all_stream(),
        db,
        out_file(output_file)
    )
}

fn encode_ffmpeg(
    input_file: &str,
    output_file: &str,
    encoder: &str,
    crf: &str,
    resolution: &str,
    is_8bit: &str,
) -> String {
    let to_8bit = if is_8bit == "y" { "-pix_fmt yuv420p" } else { "" };
    format!(
        "{} {} -vf scale={} {} -c:v {} -preset medium -crf {} {} {}",
        get_ffmpeg(),
        in_file(input_file),
        resolution,
        copy_all_stream(),
        encoder,
        crf,
        to_8bit,
        out_file(output_file)
    )
}

fn split_time(value: &str) -> Result<(u64, u64, u64)> {
    if value.contains(':') {
        let parts: Vec<&str> = value.split(':').collect();
        if parts.len() != 3 {
            return Err(anyhow!("time must be HH:MM:SS"));
        }
        return Ok((parts[0].parse()?, parts[1].parse()?, parts[2].parse()?));
    }
    if value.len() != 6 {
        return Err(anyhow!("time without separators must be HHMMSS"));
    }
    Ok((
        value[0..2].parse()?,
        value[2..4].parse()?,
        value[4..6].parse()?,
    ))
}

fn cut_ffmpeg(input_file: &str, output_file: &str, start_time: &str, end_time: &str) -> Result<String> {
    let (sh, sm, ss) = split_time(start_time)?;
    let (eh, em, es) = split_time(end_time)?;
    let start_seconds = sh * 3600 + sm * 60 + ss;
    let end_seconds = eh * 3600 + em * 60 + es;
    if end_seconds <= start_seconds {
        return Err(anyhow!("end time must be greater than start time"));
    }
    let duration = end_seconds - start_seconds;
    let dh = duration / 3600;
    let dm = (duration % 3600) / 60;
    let ds = duration % 60;
    let start_fmt = format!("{sh:02}:{sm:02}:{ss:02}");
    let duration_fmt = format!("{dh:02}:{dm:02}:{ds:02}");

    Ok(format!(
        "{} -ss {} {} -t {} -c copy -avoid_negative_ts make_zero {}",
        get_ffmpeg(),
        start_fmt,
        in_file(input_file),
        duration_fmt,
        out_file(output_file)
    ))
}

fn concat_ffmpeg(input_file: &str, output_file: &str) -> String {
    format!(
        "{} -f concat {} -c copy {}",
        get_ffmpeg(),
        in_file(input_file),
        out_file(output_file)
    )
}

fn import_ffmpeg(input_file: &str, output_file: &str, srt_file: &str) -> String {
    let input_quoted = quote_file_path(input_file);
    let output_quoted = quote_file_path(output_file);
    if input_file == srt_file || srt_file.is_empty() {
        format!(
            "{} -i {} -c:a aac -vf subtitles={} {}",
            get_ffmpeg(),
            input_quoted,
            input_quoted,
            output_quoted
        )
    } else {
        format!(
            "{} -i {} -sub_charenc UTF-8 -i {} -map 0:v -map 0:a -c copy -map 1 -c:s:0 srt -metadata:s:s:0 language=en {}",
            get_ffmpeg(),
            input_quoted,
            quote_file_path(srt_file),
            output_quoted
        )
    }
}

fn to_gif_ffmpeg(input_file: &str, output_file: &str) -> String {
    format!("{} {} -f gif {}", get_ffmpeg(), in_file(input_file), out_file(output_file))
}

fn change_container(input_file: &str, output_file: &str) -> String {
    format!(
        "{} {} -vcodec copy -acodec copy {}",
        get_ffmpeg(),
        in_file(input_file),
        out_file(output_file)
    )
}

fn extract_audio(input_file: &str, output_file: &str) -> String {
    format!(
        "{} {} -vn -acodec copy {}",
        get_ffmpeg(),
        in_file(input_file),
        out_file(output_file)
    )
}

fn remove_audio_track(input_file: &str, output_file: &str) -> String {
    format!(
        "{} {} -vcodec copy -acodec copy -map 0 -map -0:a:? {}",
        get_ffmpeg(),
        in_file(input_file),
        out_file(output_file)
    )
}

pub fn ffmpeg_mp3_to_m4a_libfdk_aac() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("mp3")?;
    let mut cmds = Vec::new();
    for f in files {
        let target = replace_file_ext(&f, ".m4a")?;
        cmds.push(to_m4a_with_libfdk_aac(&f, &target));
    }
    Ok(cmds)
}

pub fn ffmpeg_mp3_to_m4a() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("mp3")?;
    let mut cmds = Vec::new();
    for f in &files {
        let wav_file = replace_file_ext(f, ".wav")?;
        let m4a_file = replace_file_ext(f, ".m4a")?;
        cmds.push(to_wav_ffmpeg(f, &wav_file));
        cmds.push(to_m4a_with_builtin_aac(&wav_file, &m4a_file));
    }
    directory_utils::print_list(&cmds);
    for cmd in &cmds {
        let _ = process_tools::exec_cmd_shell(cmd);
    }
    for f in files {
        let wav_file = replace_file_ext(&f, ".wav")?;
        let _ = directory_utils::remove_file(&wav_file);
    }
    Ok(cmds)
}

pub fn ffmpeg_inc_volume() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("")?;
    let db = read_input("Provide db: ")?;
    let mut cmds = Vec::new();
    for f in files {
        let out = replace_file_ext(&f, &format!("_fv{db}.mkv"))?;
        cmds.push(inc_volume_ffmpeg(&f, &out, &db));
    }
    Ok(cmds)
}

pub fn ffmpeg_encode() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("")?;
    let encoder = user_input("Provide encoder", &["libx264", "libx265", "libaom-av1", "libvpx-vp9"])?;
    let crf = user_input("Provide CRF : [0-23-51 least]", &[])?;
    let resolution = user_input(
        "Provide vertical resolution",
        &["-1:-1", "426:240", "-1:360", "852:480", "-1:720", "-1:1080"],
    )?;
    let to_8bit = user_input("Should convert to 8 bit video [y/n]", &[])?;

    let mut cmds = Vec::new();
    for f in files {
        let out = replace_file_ext(&f, &format!("_{encoder}.mkv"))?;
        cmds.push(encode_ffmpeg(&f, &out, &encoder, &crf, &resolution, &to_8bit));
    }
    Ok(cmds)
}

pub fn ffmpeg_cut() -> Result<Vec<String>> {
    let input_file = read_input("Enter file: ")?;
    let (stem, ext) = file_stem_and_ext(&input_file);
    let s_time = read_input("Enter start time <hh:mm:ss | hhmmss>: ")?;
    let e_time = read_input("Enter end time <hh:mm:ss | hhmmss>: ")?;
    let out = format!("{stem}_cut{ext}");
    Ok(vec![cut_ffmpeg(&input_file, &out, &s_time, &e_time)?])
}

pub fn ffmpeg_concat() -> Result<Vec<String>> {
    let in_file = read_input("Enter txt file: ")?;
    let out_file = read_input("Enter ouput file: ")?;
    Ok(vec![concat_ffmpeg(&in_file, &out_file)])
}

pub fn ffmpeg_concat2() -> Result<Vec<String>> {
    let temp_file = "file.txt";
    let in_files = read_input("Enter space seperated input files name only!: ")?;
    let lines: Vec<String> = in_files
        .split_whitespace()
        .map(|x| format!("file '{x}'"))
        .collect();
    directory_utils::write_to_file(&lines, temp_file, "w")?;

    let out_file = read_input("Enter ouput file: ")?;
    let cmd = concat_ffmpeg(temp_file, &out_file);
    directory_utils::print_list(std::slice::from_ref(&cmd));
    let _ = process_tools::exec_cmd_shell(&cmd);
    let _ = directory_utils::remove_file(temp_file);
    Ok(vec![cmd])
}

pub fn ffmpeg_import() -> Result<Vec<String>> {
    let in_file = read_input("Enter file: ")?;
    let mut srt_file = read_input("Provide srt file or leave blank if same as input file: ")?;
    if srt_file.is_empty() {
        let candidate = replace_file_ext(&in_file, ".srt")?;
        if directory_utils::is_file(&candidate) {
            srt_file = candidate;
        }
    }
    let out_file = replace_file_ext(&in_file, "_en.mkv")?;
    Ok(vec![import_ffmpeg(&in_file, &out_file, &srt_file)])
}

pub fn ffmpeg_to_gif() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("")?;
    let mut cmds = Vec::new();
    for f in files {
        let out = replace_file_ext(&f, ".gif")?;
        cmds.push(to_gif_ffmpeg(&f, &out));
    }
    Ok(cmds)
}

pub fn ffmpeg_change_container() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("")?;
    let container = user_input("Chose conatiner to use", &["mkv", "mp4"])?;
    let mut cmds = Vec::new();
    for f in files {
        let out = replace_file_ext(&f, &format!(".{container}"))?;
        cmds.push(change_container(&f, &out));
    }
    Ok(cmds)
}

pub fn ffmpeg_extract_audio() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("")?;
    let mut cmds = Vec::new();
    for f in files {
        let out = replace_file_ext(&f, ".aac")?;
        cmds.push(extract_audio(&f, &out));
    }
    Ok(cmds)
}

pub fn ffmpeg_remove_audio_track() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("")?;
    let mut cmds = Vec::new();
    for f in files {
        let out = replace_file_ext(&f, ".mkv")?;
        cmds.push(remove_audio_track(&f, &out));
    }
    Ok(cmds)
}

pub fn rename_files_as_per_modified_timestamp() -> Result<Vec<String>> {
    let files = get_file_or_folder_list("jpg JPG MOV HEIC mov heic mp4")?;
    let mut cmd_like_output = Vec::new();
    let mut previous = HashMap::<String, usize>::new();

    for f in files {
        let (_, ext) = file_stem_and_ext(&f);
        let base = format!("IMG_{}000", get_modified_timestamp_from_file(&f)?);
        let counter = previous.entry(base.clone()).or_insert(0);
        let unique = format!("{}{}", &base[..base.len() - 1], *counter);
        *counter += 1;
        let new_name = format!("{unique}{ext}");
        cmd_like_output.push(format!("{f} -> renamed -> {new_name}"));
    }

    Ok(cmd_like_output)
}
