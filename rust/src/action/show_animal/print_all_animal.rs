use crate::enumable::animal_enum::AnimalEnum;
use async_recursion::async_recursion;

#[async_recursion]
pub async fn print_all_animal() {
    let ref mut animal_list = AnimalEnum::values();
    println!(
        "All animal is {}",
        serde_json::to_string(animal_list).unwrap()
    );
    println!(
        "{} is {} year{} old",
        AnimalEnum::Tiger.name(),
        AnimalEnum::Tiger.age(),
        if AnimalEnum::Tiger.age() > 1 { "s" } else { "" }
    );
    println!(
        "{} is {} year{} old",
        AnimalEnum::Dog.name(),
        AnimalEnum::Dog.age(),
        if AnimalEnum::Dog.age() > 1 { "s" } else { "" }
    );
}
