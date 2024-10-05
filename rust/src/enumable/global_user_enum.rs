use lazy_static::lazy_static;
use tokio::sync::Mutex;

lazy_static! {
    pub static ref USERNAME: Mutex<String> = Mutex::new("tom".to_string());
}