use crate::enumable::phone_enum::PhoneEnum;
use crate::model::iphone_model::IPhoneModel;
use crate::model::pixel_model::PixelModel;
use async_recursion::async_recursion;

impl PhoneEnum {
    #[async_recursion]
    pub async fn buy(&mut self) {
        match self {
            PhoneEnum::IPhone(iphone) => iphone.buy().await,
            PhoneEnum::Pixel(pixel) => pixel.buy().await,
        };
    }
}

impl IPhoneModel {
    #[async_recursion]
    pub async fn buy(&mut self) {
        println!("{} cost {} buy iphone", self.owner, self.price);
    }
}

impl PixelModel {
    #[async_recursion]
    pub async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}
