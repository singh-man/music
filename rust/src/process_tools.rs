use anyhow::{bail, Context, Result};
use std::process::{Command, Stdio};

pub fn exec_cmd_shell(cmd: &str) -> Result<()> {
    let output = Command::new("sh")
        .arg("-c")
        .arg(cmd)
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .output()
        .with_context(|| format!("failed to execute command: {cmd}"))?;

    if !output.stderr.is_empty() {
        eprintln!("{}", String::from_utf8_lossy(&output.stderr));
    }
    if !output.status.success() {
        bail!("command failed: {cmd}");
    }
    Ok(())
}

pub fn run_capture(program: &str, args: &[&str]) -> Result<String> {
    let output = Command::new(program)
        .args(args)
        .output()
        .with_context(|| format!("failed to run {} {:?}", program, args))?;
    if !output.status.success() {
        bail!(
            "command failed {} {:?}: {}",
            program,
            args,
            String::from_utf8_lossy(&output.stderr)
        );
    }
    Ok(String::from_utf8_lossy(&output.stdout).to_string())
}
