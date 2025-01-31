use crate::model::user_model::UserModel;
use chrono::Local;
use uuid::Uuid;

pub async fn json_to_string() -> String {
    let ref mut user_list = vec![UserModel {
        id: Uuid::new_v4().to_string(),
        name: "Tom".to_string(),
        create_date: Some(Local::now()),
        update_date: Some(Local::now()),
    }];
    let ref mut json_string = serde_json::to_string(user_list).unwrap();
    println!("{}", json_string);
    return json_string.clone();
}

pub async fn string_to_json() {
    let ref mut user_list = vec![UserModel {
        id: Uuid::new_v4().to_string(),
        name: "Jerry".to_string(),
        create_date: Some(Local::now()),
        update_date: Some(Local::now()),
    }];
    let ref mut json_string = serde_json::to_string(user_list).unwrap();
    let ref mut user_list: Vec<UserModel> = serde_json::from_str(json_string).unwrap();
    println!("{}", serde_json::to_string(user_list).unwrap());
}
