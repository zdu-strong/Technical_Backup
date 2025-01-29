use crate::enumable::animal_enum::AnimalEnum;
use strum::IntoEnumIterator;

pub async fn print_all_animal() {
    let ref mut animal_list = AnimalEnum::iter().collect::<Vec<_>>();
    println!(
        "All animal is {}",
        serde_json::to_string(animal_list).unwrap()
    );
    println!(
        "dog to string: {}, string to dog: {}",
        AnimalEnum::Dog.as_ref(),
        ("DOG".parse::<AnimalEnum>().unwrap()).as_ref()
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
