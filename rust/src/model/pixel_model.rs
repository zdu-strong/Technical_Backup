use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
pub struct PixelModel {
    pub price: String,
    pub owner: String,
}