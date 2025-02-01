use std::str::FromStr;
use crate::enums::animal_enum::AnimalEnum;
use strum::IntoEnumIterator;

pub async fn print_all_animal() {
    let ref mut animal_list = AnimalEnum::iter().collect::<Vec<_>>();
    println!("All animal is {:?}", animal_list);
    println!(
        "Parse string to animal: {:?}",
        AnimalEnum::from_str("DOG").unwrap()
    );
    println!(
        "Print animal value: {:?}",
        AnimalEnum::TIGER.as_ref()
    );
}
