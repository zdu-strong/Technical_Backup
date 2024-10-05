use crate::model::iphone_model::IPhoneModel;
use crate::model::pixel_model::PixelModel;

#[derive(Debug, Clone)]
pub enum PhoneEnum {
    Pixel(PixelModel),
    IPhone(IPhoneModel),
}