use async_trait::async_trait;
use super::phone_service::PhoneService;

#[derive(Debug)]
pub struct PixelService {
    pub price: String,
    pub owner: String,
}

#[async_trait]
impl PhoneService for PixelService {
    async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}
