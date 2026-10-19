use crate::common::traits::buy::Buy;
use crate::model::pixel_model::PixelModel;
use bigdecimal::BigDecimal;
use serde_json::to_string_pretty;
use std::str::FromStr;

pub async fn tom_buy_phone() {
    let ref mut phone_list = vec![
        PixelModel {
            price: BigDecimal::from_str("10000").unwrap(),
            owner: "Tom".to_string(),
        },
        PixelModel {
            price: BigDecimal::from_str("10000").unwrap(),
            owner: "Tom".to_string(),
        },
    ];
    println!(
        "phone_service_list: \n{}",
        to_string_pretty(phone_list).unwrap()
    );
    for phone in phone_list {
        phone.buy().await;
    }
}
