use crate::constant::global_user_constant::GLOBAL_USERNAME;

pub async fn print_username() {
    let ref mut username = *GLOBAL_USERNAME.lock().await;
    *username = "Jerry".to_string();
    println!("My name is {}", username);
}