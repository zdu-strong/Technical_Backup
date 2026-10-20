use crate::model::user_model::UserModel;
use chrono::Local;
use once_cell::sync::Lazy;
use reqwest::Client;
use reqwest::Method;
use reqwest::RequestBuilder;
use reqwest::Url;
use std::sync::Arc;
use tokio::sync::RwLock;
use uuid::Uuid;

pub static SERVER_ADDRESS: Lazy<Arc<RwLock<String>>> =
    Lazy::new(|| Arc::new(RwLock::new(get_server_address())));

pub static SERVER_USER_INFO: Lazy<Arc<RwLock<UserModel>>> =
    Lazy::new(|| Arc::new(RwLock::new(UserModel::default())));

pub const KEY_OF_SERVER_USER_INFO_OF_PERSISTENT: &str =
    "GlobalUserInfo-5a8dcb9f-495f-4894-8dcb-9f495f489409";

pub static SERVER_ERROR: Lazy<Arc<RwLock<Option<String>>>> =
    Lazy::new(|| Arc::new(RwLock::new(None)));

pub async fn remove_server_user_info() {
    *SERVER_USER_INFO.write().await = UserModel::default();
}

pub async fn set_server_user_info(user: Option<UserModel>) {
    let user = user.unwrap_or_default();
    let has_params = !user.access_token.is_empty();
    if !has_params {
        remove_server_user_info().await;
        return;
    }
    *SERVER_USER_INFO.write().await = user;
}

pub async fn get(url: &str) -> RequestBuilder {
    get_request_builder(Method::GET, url).await
}

pub async fn post(url: &str) -> RequestBuilder {
    get_request_builder(Method::POST, url).await
}

async fn get_request_builder(method: Method, url: &str) -> RequestBuilder {
    let server_address = SERVER_ADDRESS.read().await;
    let server_url = get_server_api_url(url, server_address.as_str());
    let mut request_builder = Client::new().request(method.clone(), server_url.clone());
    if server_url.origin().ascii_serialization()
        == Url::parse(server_address.as_str())
            .unwrap()
            .origin()
            .ascii_serialization()
    {
        let access_token = SERVER_USER_INFO.read().await.access_token.clone();
        if !access_token.is_empty() {
            request_builder = request_builder.bearer_auth(access_token);
        }
        if Method::GET != method {
            request_builder = request_builder.header("X-Nonce", Uuid::new_v4().to_string());
            request_builder = request_builder.header(
                "X-Timestamp",
                serde_json::to_string(&Local::now())
                    .unwrap()
                    .replace("\"", ""),
            );
        }
    }

    return request_builder;
}

fn get_server_api_url(url: &str, server_address: &str) -> Url {
    Url::options()
        .base_url(Option::Some(&Url::parse(server_address).unwrap()))
        .parse(url)
        .unwrap()
}

fn get_server_address() -> String {
    "http://127.0.0.1:8080".to_string()
}
