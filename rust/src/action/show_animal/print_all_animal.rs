use crate::enumable::animal_enum::AnimalEnum;
use async_recursion::async_recursion;

#[async_recursion]
pub async fn print_all_animal() {
    let ref mut animal_list = AnimalEnum::values().await;
    println!(
        "All animal is {}",
        serde_json::to_string(animal_list).unwrap()
    );
    println!(
        "{} is {} year old",
        AnimalEnum::Tiger.name().await,
        AnimalEnum::Tiger.age().await
    );
    println!(
        "{} is {} years old",
        AnimalEnum::Dog.name().await,
        AnimalEnum::Dog.age().await
    );
}
