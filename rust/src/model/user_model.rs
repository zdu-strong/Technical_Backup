use chrono::DateTime;
use chrono::Local;
use derive_more::Display;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct UserModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub id: String,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub username: String,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub create_date: DateTime<Local>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub update_date: DateTime<Local>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub access_token: String,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub password: String,
}
