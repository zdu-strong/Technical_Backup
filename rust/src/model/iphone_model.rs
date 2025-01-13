use async_recursion::async_recursion;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
pub struct IPhoneModel {
    pub price: String,
    pub owner: String,
}

impl IPhoneModel {
    #[async_recursion]
    pub async fn buy(&mut self) {
        println!("{} cost {} buy iphone", self.owner, self.price);
    }
}
