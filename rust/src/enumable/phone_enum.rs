use crate::model::iphone_model::IPhoneModel;
use crate::model::pixel_model::PixelModel;
use async_recursion::async_recursion;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub enum PhoneEnum {
    Pixel(PixelModel),
    IPhone(IPhoneModel),
}

impl PhoneEnum {
    #[async_recursion]
    pub async fn buy(&mut self) {
        match self {
            PhoneEnum::IPhone(iphone) => iphone.buy().await,
            PhoneEnum::Pixel(pixel) => pixel.buy().await,
        };
    }
}
