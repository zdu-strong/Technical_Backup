use crate::service::phone_service::iphone_service::IPhoneService;
use crate::service::phone_service::phone_service::PhoneService;
use crate::service::phone_service::pixel_service::PixelService;
use futures::stream::iter;
use futures::StreamExt;

pub async fn jerry_buy_phone() {
    let ref mut phone_list = vec![
        Box::new(IPhoneService {
            price: "10,000".to_string(),
            owner: "Jerry".to_string(),
        }) as Box<dyn PhoneService>,
        Box::new(PixelService {
            price: "3,000".to_string(),
            owner: "Jerry".to_string(),
        }),
    ];
    iter(phone_list)
        .for_each_concurrent(10, |phone| async move {
            phone.buy().await;
        })
        .await;
}
