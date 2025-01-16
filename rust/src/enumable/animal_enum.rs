use async_recursion::async_recursion;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum AnimalEnum {
    Tiger,
    Dog,
}

impl AnimalEnum {
    #[async_recursion]
    pub async fn values() -> Vec<AnimalEnum> {
        return [AnimalEnum::Tiger, AnimalEnum::Dog].to_vec();
    }
}

impl AnimalEnum {
    #[async_recursion]
    pub async fn name(&mut self) -> String {
        return match self {
            AnimalEnum::Tiger => "Tiger".to_string(),
            AnimalEnum::Dog => "Dog".to_string(),
        };
    }
    #[async_recursion]
    pub async fn age(&mut self) -> u32 {
        return match self {
            AnimalEnum::Tiger => 2,
            AnimalEnum::Dog => 1,
        };
    }
}
