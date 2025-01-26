use crate::action::eat_food::eat_apple::eat_apple;

pub async fn tom_eat_apple() {
    let ref mut username = "tom".to_string();
    let ref mut max_apple_count = 10;
    let ref mut surplus_apple_count = max_apple_count.clone();
    eat_apple(username, surplus_apple_count, max_apple_count).await;
}