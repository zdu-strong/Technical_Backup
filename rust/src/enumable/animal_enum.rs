use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub enum AnimalEnum {
    Tiger,
    Dog,
}

impl AnimalEnum {
    pub fn values() -> Vec<AnimalEnum> {
        return [AnimalEnum::Tiger, AnimalEnum::Dog].to_vec();
    }
}

impl AnimalEnum {
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
