use crate::traits::phone::phone_trait::PhoneTrait;
use async_trait::async_trait;

#[derive(Debug)]
pub struct IPhoneTrait {
    pub price: String,
    pub owner: String,
}

#[async_trait]
impl PhoneTrait for IPhoneTrait {
    async fn buy(&mut self) {
        println!("{} cost {} buy iphone", self.owner, self.price);
    }
}
