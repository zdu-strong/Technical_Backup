use futures::stream::iter;
use futures::StreamExt;
use crate::service::phone_service::iphone_service::IPhoneService;
use crate::service::phone_service::phone_service::PhoneService;
use crate::service::phone_service::pixel_service::PixelService;

pub async fn jerry_buy_phone() {
    let ref mut phone_list = [
        PhoneService::IPhone(IPhoneService {
            price: "10,000".to_string(),
            owner: "Jerry".to_string(),
        }),
        PhoneService::Pixel(PixelService {
            price: "3,000".to_string(),
            owner: "Jerry".to_string(),
        }),
    ]
    .to_vec();
    iter(phone_list)
        .for_each_concurrent(10, |phone| async move {
            phone.buy().await;
        })
        .await;
}
