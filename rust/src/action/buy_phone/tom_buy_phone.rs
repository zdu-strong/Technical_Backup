use crate::service::phone_service::iphone_service::IPhoneService;
use crate::service::phone_service::phone_service::PhoneService;
use crate::service::phone_service::pixel_service::PixelService;

pub async fn tom_buy_phone() {
    let ref mut phone_list = vec![
        Box::new(IPhoneService {
            price: "10,000".to_string(),
            owner: "Tom".to_string(),
        }) as Box<dyn PhoneService>,
        Box::new(PixelService {
            price: "10,000".to_string(),
            owner: "Tom".to_string(),
        }),
    ];
    println!("{:?}", phone_list);
    for phone in phone_list {
        phone.buy().await;
    }
}
