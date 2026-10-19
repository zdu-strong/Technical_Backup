use std::collections::VecDeque;

pub async fn eat_apple(
    username: &mut String,
    surplus_apple_count: &mut i128,
    max_apple_count: &mut i128,
) {
    let ref mut apple_deque = VecDeque::from([(username, surplus_apple_count, max_apple_count)]);
    while !apple_deque.is_empty() {
        let (username, surplus_apple_count, max_apple_count) = apple_deque.pop_front().unwrap();
        if *max_apple_count < 0 {
            panic!("illegal max apple count");
        }
        if *surplus_apple_count < 0 {
            panic!("illegal surplus apple count");
        }
        if *surplus_apple_count > *max_apple_count {
            panic!("illegal surplus apple count");
        }
        if *surplus_apple_count == 0 {
            continue;
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
        apple_deque.push_back((username, surplus_apple_count, max_apple_count));
    }
}
