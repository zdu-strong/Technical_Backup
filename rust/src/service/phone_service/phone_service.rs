use crate::service::phone_service::iphone_service::IPhoneService;
use crate::service::phone_service::pixel_service::PixelService;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub enum PhoneService {
    Pixel(PixelService),
    IPhone(IPhoneService),
}

impl PhoneService {
    pub async fn buy(&mut self) {
        match self {
            PhoneService::IPhone(iphone) => iphone.buy().await,
            PhoneService::Pixel(pixel) => pixel.buy().await,
        };
    }
}
