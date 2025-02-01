use crate::service::phone_service::phone_service::PhoneService;
use async_trait::async_trait;

#[derive(Debug)]
pub struct IPhoneService {
    pub price: String,
    pub owner: String,
}

#[async_trait]
impl PhoneService for IPhoneService {
    async fn buy(&mut self) {
        println!("{} cost {} buy iphone", self.owner, self.price);
    }
}
