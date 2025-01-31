use async_trait::async_trait;
use serde::Deserialize;
use serde::Serialize;

use super::phone_service::PhoneService;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
#[serde(rename_all = "camelCase")]
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
