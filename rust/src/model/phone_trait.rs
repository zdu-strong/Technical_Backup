use crate::model::iphone_model::IPhoneModel;
use crate::model::pixel_model::PixelModel;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub enum PhoneTrait {
    Pixel(PixelModel),
    IPhone(IPhoneModel),
}

impl PhoneTrait {
    pub async fn buy(&mut self) {
        match self {
            PhoneTrait::IPhone(iphone) => iphone.buy().await,
            PhoneTrait::Pixel(pixel) => pixel.buy().await,
        };
    }
}
