use async_recursion::async_recursion;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
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
