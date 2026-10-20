use crate::constant::server_constant::*;
use crate::model::user_model::UserModel;
use serde_json::json;

pub async fn sign_in(username: &String, password: &String) {
    let user = post("/sign-in")
        .await
        .query(&json!({
            "username": username,
            "password": password,
        }))
        .send()
        .await
        .unwrap()
        .error_for_status()
        .unwrap()
        .json::<Option<UserModel>>()
        .await
        .unwrap();
    set_server_user_info(user).await;
}

pub async fn sign_out() {
    if !SERVER_USER_INFO.read().await.access_token.is_empty() {
        let _ = post("/sign-out")
            .await
            .send()
            .await
            .unwrap()
            .error_for_status()
            .unwrap();
        remove_server_user_info().await;
    }
}
