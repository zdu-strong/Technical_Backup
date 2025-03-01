use std::collections::VecDeque;

pub async fn eat_apple(
    username: &mut String,
    surplus_apple_count: &mut i32,
    max_apple_count: &mut i32,
) {
    let ref mut vec_deque = VecDeque::from([(username, surplus_apple_count, max_apple_count)]);
    while !vec_deque.is_empty() {
        let (username, surplus_apple_count, max_apple_count) = vec_deque.pop_front().unwrap();
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
        vec_deque.push_back((username, surplus_apple_count, max_apple_count));
    }
}
