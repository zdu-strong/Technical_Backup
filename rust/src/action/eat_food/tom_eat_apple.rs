use crate::action::eat_food::eat_apple::eat_apple;

pub async fn tom_eat_apple() {
    let ref mut username = "tom".to_string();
    let max_apple_count = 10;
    eat_apple(username, max_apple_count).await;
}
