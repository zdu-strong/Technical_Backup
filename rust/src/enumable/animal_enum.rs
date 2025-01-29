use serde::Deserialize;
use serde::Serialize;
use strum_macros::AsRefStr;
use strum_macros::EnumIter;
use strum_macros::EnumString;

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, AsRefStr)]
#[serde(rename_all = "camelCase")]
pub enum AnimalEnum {
    #[strum(serialize = "1")]
    Tiger,

    #[strum(serialize = "2")]
    Dog,
}

impl AnimalEnum {
    // pub fn values() -> Vec<AnimalEnum> {
    //     AnimalEnum::iter().collect::<Vec<_>>()
    // }

   

    pub fn name(&mut self) -> String {
        return match self {
            AnimalEnum::Tiger => "Tiger".to_string(),
            AnimalEnum::Dog => "Dog".to_string(),
        };
    }
    pub fn age(&mut self) -> u32 {
        return match self {
            AnimalEnum::Tiger => 2,
            AnimalEnum::Dog => 1,
        };
    }
}
