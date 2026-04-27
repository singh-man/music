use anyhow::{Context, Result};
use std::fs::{self, OpenOptions};
use std::io::Write;
use std::path::Path;
use walkdir::WalkDir;

pub fn is_dir(path: &str) -> bool {
    Path::new(path).is_dir()
}

pub fn is_file(path: &str) -> bool {
    Path::new(path).is_file()
}

pub fn find_files(dir_path: &str, ext_space_sep: &str) -> Result<Vec<String>> {
    let exts: Vec<&str> = if ext_space_sep.trim().is_empty() {
        vec![""]
    } else {
        ext_space_sep.split_whitespace().collect()
    };
    recursively_find_files(dir_path, &exts, true)
}

pub fn recursively_find_files(dir_path: &str, exts: &[&str], recurse: bool) -> Result<Vec<String>> {
    let mut files = Vec::new();
    if recurse {
        for entry in WalkDir::new(dir_path).into_iter().flatten() {
            if entry.file_type().is_file() && extension_match(entry.path(), exts) {
                files.push(entry.path().to_string_lossy().to_string());
            }
        }
    } else {
        for entry in fs::read_dir(dir_path).with_context(|| format!("cannot read directory {dir_path}"))? {
            let entry = entry?;
            let path = entry.path();
            if path.is_file() && extension_match(&path, exts) {
                files.push(path.to_string_lossy().to_string());
            }
        }
    }
    Ok(files)
}

fn extension_match(path: &Path, exts: &[&str]) -> bool {
    if exts.contains(&"") {
        return true;
    }
    let ext = path
        .extension()
        .map(|e| e.to_string_lossy().to_lowercase())
        .unwrap_or_default();
    exts.iter().any(|x| x.eq_ignore_ascii_case(&ext))
}

pub fn write_to_file(lines: &[String], to_file: &str, mode: &str) -> Result<()> {
    let append = mode == "a" || mode == "a+";
    let mut file = OpenOptions::new()
        .create(true)
        .write(true)
        .truncate(!append)
        .append(append)
        .open(to_file)
        .with_context(|| format!("failed to open {to_file}"))?;

    for line in lines {
        writeln!(file, "{line}")?;
    }
    Ok(())
}

pub fn remove_file(path: &str) -> Result<()> {
    fs::remove_file(path).with_context(|| format!("failed to remove file {path}"))
}

pub fn print_list(list: &[String]) {
    println!("Printing list below::->");
    for item in list {
        println!("{item}");
    }
}

pub fn dump_cmd_to_script(cmd_list: &[String], to_path: &str, file_name: &str) -> Result<()> {
    let script = format!("{to_path}{file_name}");
    println!("Should I write to file or execute them - yes, append, no, execute - !... (y/a/n/e) : ");
    let mut input = String::new();
    std::io::stdin().read_line(&mut input)?;
    let option = input.trim();

    match option {
        "y" => write_to_file(cmd_list, &script, "w")?,
        "a" => write_to_file(cmd_list, &script, "a+")?,
        "e" => {
            for cmd in cmd_list {
                crate::process_tools::exec_cmd_shell(cmd)?;
            }
        }
        _ => return Ok(()),
    }

    println!("Run this in shell: {script}");
    Ok(())
}
