use async_recursion::async_recursion;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
#[serde(rename_all = "camelCase")]
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
