use crate::common::traits::buy::Buy;
use crate::model::pixel_model::PixelModel;
use bigdecimal::BigDecimal;
use std::any::Any;
use std::str::FromStr;

pub async fn downcast_any_example() {
    let phone: Box<dyn Buy> = Box::new(PixelModel {
        price: BigDecimal::from_str("10000").unwrap(),
        owner: "Jerry".to_string(),
    });
    let mut pixel = *(phone as Box<dyn Any>).downcast::<PixelModel>().unwrap();
    pixel.buy().await;
}
