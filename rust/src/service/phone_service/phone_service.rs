use std::fmt::Debug;
use async_trait::async_trait;

#[async_trait]
pub trait PhoneService : Debug  {
    async fn buy(&mut self);
}
