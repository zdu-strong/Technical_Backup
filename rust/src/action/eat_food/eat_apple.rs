use async_recursion::async_recursion;

#[async_recursion]
pub async fn eat_apple(
    username: &mut String,
    surplus_apple_count: &mut i32,
    max_apple_count: &mut i32,
) {
    if *max_apple_count < 0 {
        panic!("illegal max apple count");
    }
    if *surplus_apple_count < 0 {
        panic!("illegal surplus apple count");
    }
    if surplus_apple_count > max_apple_count {
        panic!("illegal surplus apple count");
    }
    if *surplus_apple_count == 0 {
        return;
    }
    *surplus_apple_count -= 1;
    println!(
        "{} eat {}{} {}",
        username,
        *max_apple_count - *surplus_apple_count,
        match *max_apple_count - *surplus_apple_count {
            1 => "st",
            2 => "nd",
            3 => "rd",
            _ => "th",
        },
        "apple"
    );
    eat_apple(username, surplus_apple_count, max_apple_count).await;
}
