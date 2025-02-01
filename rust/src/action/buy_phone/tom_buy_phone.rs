use crate::traits::phone::iphone_trait::IPhoneTrait;
use crate::traits::phone::phone_trait::PhoneTrait;
use crate::traits::phone::pixel_trait::PixelTrait;

pub async fn tom_buy_phone() {
    let ref mut phone_list = vec![
        Box::new(IPhoneTrait {
            price: "10,000".to_string(),
            owner: "Tom".to_string(),
        }) as Box<dyn PhoneTrait>,
        Box::new(PixelTrait {
            price: "10,000".to_string(),
            owner: "Tom".to_string(),
        }),
    ];
    println!("phone_service {:?}", phone_list);
    for phone in phone_list {
        phone.buy().await;
    }
}
