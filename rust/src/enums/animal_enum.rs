use serde::Deserialize;
use serde::Serialize;
use strum_macros::AsRefStr;
use strum_macros::EnumIter;
use strum_macros::EnumString;

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, AsRefStr)]
#[serde(rename_all = "camelCase")]
pub enum AnimalEnum {
    #[strum(serialize = "TIGER")]
    Tiger,

    #[strum(serialize = "DOG")]
    Dog,
}

impl AnimalEnum {
    pub fn parse(value: &str) -> AnimalEnum {
        value.parse::<AnimalEnum>().unwrap()
    }
}
