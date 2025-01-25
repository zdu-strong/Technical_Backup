use crate::model::pagination_model::PaginationModel;
use async_recursion::async_recursion;

#[async_recursion]
pub async fn pagination_example() {
    let ref mut pagination_model = PaginationModel::new(1, 1, ["tom", "jerry"].to_vec());
    let ref mut pagination_json_string = serde_json::to_string(pagination_model).unwrap();
    println!("pagination_model: {}", pagination_json_string);
    let ref mut pagination_one_model: PaginationModel<String> =
        serde_json::from_str(pagination_json_string).unwrap();
    println!(
        "parse json string pagination_model:{}",
        serde_json::to_string(pagination_one_model).unwrap()
    );
}
