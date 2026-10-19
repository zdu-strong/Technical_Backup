use crate::common::traits::buy::Buy;
use crate::model::pixel_model::PixelModel;
use bigdecimal::BigDecimal;
use futures::prelude::*;
use futures::stream::iter;
use std::str::FromStr;

pub async fn jerry_buy_phone() {
    let phone_list: Vec<Box<dyn Buy>> = vec![
        Box::new(PixelModel {
            price: BigDecimal::from_str("10000").unwrap(),
            owner: "Jerry".to_string(),
        }),
        Box::new(PixelModel {
            price: BigDecimal::from_str("3000").unwrap(),
            owner: "Jerry".to_string(),
        }),
    ];
    let _ = iter(phone_list)
        .map(move |mut phone| async move {
            phone.buy().await;
        })
        .buffer_unordered(10)
        .collect::<Vec<_>>()
        .await;
}
