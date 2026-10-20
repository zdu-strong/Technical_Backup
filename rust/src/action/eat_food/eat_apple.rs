use std::collections::VecDeque;

pub async fn eat_apple(username: &String, max_apple_count: i128) {
    let surplus_apple_count = max_apple_count;
    let mut apple_deque = VecDeque::from([surplus_apple_count]);
    while !apple_deque.is_empty() {
        let mut surplus_apple_count = apple_deque.pop_front().unwrap();
        if max_apple_count < 0 {
            panic!("illegal max apple count");
        }
        if surplus_apple_count < 0 {
            panic!("illegal surplus apple count");
        }
        if surplus_apple_count > max_apple_count {
            panic!("illegal surplus apple count");
        }
        if surplus_apple_count == 0 {
            continue;
        }
        surplus_apple_count -= 1;
        println!(
            "{} eat {}{} {}",
            username,
            max_apple_count - surplus_apple_count,
            match max_apple_count - surplus_apple_count {
                1 => "st",
                2 => "nd",
                3 => "rd",
                _ => "th",
            },
            "apple"
        );
        apple_deque.push_back(surplus_apple_count);
    }
}
