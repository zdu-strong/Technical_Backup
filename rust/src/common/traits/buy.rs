use async_trait::async_trait;
use std::any::Any;
use std::fmt::Debug;
use std::fmt::Display;

#[async_trait]
pub trait Buy: 'static + Debug + Display + Send + Sync + Any {
    async fn buy(&mut self);
}
