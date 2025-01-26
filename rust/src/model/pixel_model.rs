use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
#[serde(rename_all = "camelCase")]
pub struct PixelModel {
    pub price: String,
    pub owner: String,
}

impl PixelModel {
    pub async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}
