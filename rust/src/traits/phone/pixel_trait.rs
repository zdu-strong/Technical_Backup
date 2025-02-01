use crate::traits::phone::phone_trait::PhoneTrait;
use async_trait::async_trait;

#[derive(Debug)]
pub struct PixelTrait {
    pub price: String,
    pub owner: String,
}

#[async_trait]
impl PhoneTrait for PixelTrait {
    async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}
