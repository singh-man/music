use anyhow::{Context, Result};
use chrono::{DateTime, Local};
use regex::Regex;
use std::fs;
use std::path::Path;

pub fn replace_file_ext(input_file: &str, target_ext: &str) -> Result<String> {
    let re = Regex::new(r"\\.(?=[^\\.]+$)")?;
    let replacement = if target_ext.starts_with('.') {
        target_ext.to_string()
    } else {
        format!(".{target_ext}")
    };
    if re.is_match(input_file) {
        Ok(re.replace(input_file, replacement).to_string())
    } else {
        Ok(format!("{input_file}{replacement}"))
    }
}

pub fn quote_file_path(name: &str) -> String {
    format!("\"{name}\"")
}

pub fn get_modified_timestamp_from_file(file: &str) -> Result<String> {
    let metadata = fs::metadata(file).with_context(|| format!("failed to read metadata for {file}"))?;
    let modified = metadata
        .modified()
        .with_context(|| format!("failed to get mtime for {file}"))?;
    let modified: DateTime<Local> = modified.into();
    Ok(modified.format("%Y%m%d_%H%M%S").to_string())
}

pub fn file_stem_and_ext(path: &str) -> (String, String) {
    let p = Path::new(path);
    let stem = p
        .file_stem()
        .map(|s| s.to_string_lossy().to_string())
        .unwrap_or_default();
    let ext = p
        .extension()
        .map(|e| format!(".{}", e.to_string_lossy()))
        .unwrap_or_default();
    (stem, ext)
}
