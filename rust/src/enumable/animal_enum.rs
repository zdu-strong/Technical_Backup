use serde::Deserialize;
use serde::Serialize;
use strum::IntoEnumIterator;
use strum_macros::EnumIter;

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter)]
#[serde(rename_all = "camelCase")]
pub enum AnimalEnum {
    Tiger = 1,
    Dog = 2,
}

impl AnimalEnum {
    pub fn values() -> Vec<AnimalEnum> {
        AnimalEnum::iter().collect::<Vec<_>>()
    }

    pub fn of(id: usize) -> AnimalEnum {
        AnimalEnum::values()
            .into_iter()
            .filter(|s| (s.clone() as usize) == id)
            .next()
            .unwrap()
    }

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
