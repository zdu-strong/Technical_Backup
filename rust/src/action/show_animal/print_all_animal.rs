use crate::enums::animal_enum::AnimalEnum;
use strum::IntoEnumIterator;

pub async fn print_all_animal() {
    let ref mut animal_list = AnimalEnum::iter().collect::<Vec<_>>();
    println!(
        "All animal is {}",
        serde_json::to_string(&animal_list.iter().map(|s| s.as_ref()).collect::<Vec<_>>()).unwrap()
    );
    println!(
        "Parse string to animal: {}",
        AnimalEnum::parse("DOG").as_ref()
    );
}
