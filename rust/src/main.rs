use crate::action::buy_phone::jerry_buy_phone::jerry_buy_phone;
use crate::action::buy_phone::tom_buy_phone::tom_buy_phone;
use crate::action::downcast_example::downcast_example::downcast_any_example;
use crate::action::eat_food::tom_eat_apple::tom_eat_apple;
use crate::action::json_example::user_json::json_to_string;
use crate::action::json_example::user_json::string_to_json;
use crate::action::pagination::pagination::pagination_example;
use crate::action::print_global_username::print_username::print_username;
use crate::action::show_animal::print_all_animal::print_all_animal;
use crate::action::spawn_blocking_async_task::spawn_blocking_async_task::spawn_blocking_async_task;
pub mod action;
pub mod api;
pub mod constant;
pub mod enums;
pub mod model;
pub mod common;

#[tokio::main(flavor = "multi_thread")]
async fn main() {
    println!("Hello, world!");
    print_username().await;
    tom_buy_phone().await;
    jerry_buy_phone().await;
    tom_eat_apple().await;
    json_to_string().await;
    string_to_json().await;
    print_all_animal().await;
    pagination_example().await;
    spawn_blocking_async_task().await;
    downcast_any_example().await;
}
