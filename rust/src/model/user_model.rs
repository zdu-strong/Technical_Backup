use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
pub struct UserModel {
    pub id: String,
    pub name: String,
}
