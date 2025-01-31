use crate::enums::phone_enum::PhoneEnum;
use crate::model::iphone_model::IPhoneModel;
use crate::model::pixel_model::PixelModel;

pub async fn tom_buy_phone() {
    let ref mut phone_list = [
        PhoneEnum::IPhone(IPhoneModel {
            price: "10,000".to_string(),
            owner: "Tom".to_string(),
        }),
        PhoneEnum::Pixel(PixelModel {
            price: "3,000".to_string(),
            owner: "Tom".to_string(),
        }),
    ]
    .to_vec();
    for phone in phone_list {
        phone.buy().await;
    }
}
