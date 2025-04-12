use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
#[serde(rename_all = "camelCase")]
pub struct PaginationModel<T> {
    pub page_num: usize,
    pub page_size: usize,
    pub total_records: usize,
    pub total_pages: usize,
    pub items: Vec<T>,
}

impl<T> PaginationModel<T> {
    pub fn new(page_num: usize, page_size: usize, items: Vec<T>) -> PaginationModel<T> {
        if page_num < 1 {
            panic!("page_num cannot be less than 1")
        }
        if page_size < 1 {
            panic!("page_size cannot be less than 1")
        }
        PaginationModel {
            page_num: page_num,
            page_size: page_size,
            total_records: items.len(),
            total_pages: (items.len() / page_size) + (if items.len() % page_size > 0 { 1 } else { 0 }),
            items: items
                .into_iter()
                .skip((page_num - 1) * page_size)
                .take(page_size)
                .collect::<Vec<_>>(),
        }
    }
}
