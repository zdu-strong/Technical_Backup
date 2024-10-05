use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
pub struct IPhoneModel {
    pub price: String,
    pub owner: String,
}