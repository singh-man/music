use anyhow::Result;
use reqwest::blocking::Client;
use serde_json::{json, Value};

fn rest_call(url: &str) -> Result<(u16, Value)> {
    let client = Client::new();
    let resp = client.get(url).send()?;
    let code = resp.status().as_u16();
    if code != 200 {
        return Ok((code, json!("")));
    }
    let payload = resp.json::<Value>().unwrap_or_else(|_| json!(""));
    Ok((code, payload))
}

pub fn cbas() -> Result<Value> {
    let server = "https://stage-external-api-account.quest.com/idp/healthcheck";
    let db = "https://stage-external-api-account.quest.com/idp/healthcheck/getdbdate";
    let (x, y) = rest_call(server)?;
    let (a, b) = rest_call(db)?;
    Ok(json!({"server": [x, y], "db": [a, b]}))
}

pub fn profile() -> Result<Value> {
    let server = "https://stage-profile.quest.com/healthcheck";
    let db = "https://stage-profile.quest.com/healthcheck/getDbDate";
    let (x, y) = rest_call(server)?;
    let (a, b) = rest_call(db)?;
    let y_resp = y.get("response").cloned().unwrap_or(y);
    let b_resp = b.get("response").cloned().unwrap_or(b);
    Ok(json!({"server": [x, y_resp], "db": [a, b_resp]}))
}

pub fn qib() -> Result<Value> {
    let well_known = "https://id-qa.quest.com/auth/realms/quest/.well-known/openid-configuration";
    let certs = "https://id-qa.quest.com/auth/realms/quest/protocol/openid-connect/certs";
    let (x1, _) = rest_call(well_known)?;
    let (x2, _) = rest_call(certs)?;
    Ok(json!({"Well-known": x1, "certs": x2}))
}
