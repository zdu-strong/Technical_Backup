use async_trait::async_trait;

#[async_trait]
pub trait PhoneService {
    async fn buy(&mut self);
}
