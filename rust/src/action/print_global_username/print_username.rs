use async_recursion::async_recursion;
use crate::enumable::global_user_enum::USERNAME;

#[async_recursion]
pub async fn print_username() {
    let ref mut username = *USERNAME.lock().await;
    *username = "Jerry".to_string();
    println!("My name is {}", username);
}