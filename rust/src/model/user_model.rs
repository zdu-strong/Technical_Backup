use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct UserModel {
    pub id: String,
    pub name: String,
}
