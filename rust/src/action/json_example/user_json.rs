use crate::model::user_model::UserModel;
use chrono::Local;
use serde_json::to_string_pretty;
use uuid::Uuid;

pub async fn json_to_string() -> String {
    let ref mut user_list = vec![UserModel {
        id: Uuid::new_v4().to_string(),
        name: "Tom".to_string(),
        create_date: Local::now(),
        update_date: Local::now(),
        access_token: String::default(),
        password: String::default(),
    }];
    let ref mut json_string = serde_json::to_string(user_list).unwrap();
    println!("{}", json_string);
    return json_string.clone();
}

pub async fn string_to_json() {
    let ref mut user_list = vec![UserModel {
        id: Uuid::new_v4().to_string(),
        name: "Jerry".to_string(),
        create_date: Local::now(),
        update_date: Local::now(),
        access_token: String::default(),
        password: String::default(),
    }];
    let ref mut json_string = serde_json::to_string(user_list).unwrap();
    let ref mut user_list = serde_json::from_str::<Vec<UserModel>>(json_string).unwrap();
    println!("{}", to_string_pretty(user_list).unwrap());
}
