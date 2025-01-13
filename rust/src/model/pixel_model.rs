use serde::Deserialize;
use serde::Serialize;
use async_recursion::async_recursion;

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
pub struct PixelModel {
    pub price: String,
    pub owner: String,
}

impl PixelModel {
    #[async_recursion]
    pub async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}