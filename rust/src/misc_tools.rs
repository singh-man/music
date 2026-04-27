use anyhow::Result;

pub fn cbas_profile_qib_healthcheck() -> Result<()> {
    let mut result = serde_json::Map::new();
    result.insert("CBAS".to_string(), crate::qib::cbas()?);
    result.insert("PROFILE".to_string(), crate::qib::profile()?);
    result.insert("QIB".to_string(), crate::qib::qib()?);
    println!("{}", serde_json::Value::Object(result));
    Ok(())
}
