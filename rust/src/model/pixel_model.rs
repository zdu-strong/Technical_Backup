use crate::common::traits::buy::Buy;
use async_trait::async_trait;
use bigdecimal::BigDecimal;
use derive_more::Display;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;
use std::fmt::Debug;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct PixelModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub price: BigDecimal,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub owner: String,
}

#[async_trait]
impl Buy for PixelModel {
    async fn buy(&mut self) {
        println!(
            "{} cost {} buy pixel",
            self.owner,
            self.price.to_plain_string()
        );
    }
}
