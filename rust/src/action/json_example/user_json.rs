use crate::model::user_model::UserModel;
use async_recursion::async_recursion;
use uuid::Uuid;

#[async_recursion]
pub async fn json_to_string() -> String {
    let ref mut user_list = [UserModel {
        id: Uuid::new_v4().to_string(),
        name: "Tom".to_string(),
    }].to_vec();
    let ref mut json_string = serde_json::to_string(user_list).unwrap();
    println!("{}", json_string);
    return json_string.clone();
}

#[async_recursion]
pub async fn string_to_json() {
    let ref mut user_list = [UserModel {
        id: Uuid::new_v4().to_string(),
        name: "Jerry".to_string(),
    }].to_vec();
    let ref mut json_string = serde_json::to_string(user_list).unwrap();
    let ref mut user_list: Vec<UserModel> = serde_json::from_str(json_string.as_str()).unwrap();
    println!("{:?}", user_list);
}
