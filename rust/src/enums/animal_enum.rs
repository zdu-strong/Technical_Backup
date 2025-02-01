use strum_macros::AsRefStr;
use strum_macros::EnumIter;
use strum_macros::EnumString;

#[derive(Debug, EnumIter, EnumString, AsRefStr)]
pub enum AnimalEnum {
    #[strum(serialize = "TIGER")]
    TIGER,

    #[strum(serialize = "DOG")]
    DOG,
}
