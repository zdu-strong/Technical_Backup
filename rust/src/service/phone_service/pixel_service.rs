use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
#[serde(rename_all = "camelCase")]
pub struct PixelService {
    pub price: String,
    pub owner: String,
}

impl PixelService {
    pub async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}
