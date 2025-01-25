use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct PaginationModel<T> {
    pub page_num: usize,
    pub page_size: usize,
    pub total_record: usize,
    pub total_page: usize,
    pub list: Vec<T>,
}

impl<T> PaginationModel<T> {
    pub fn new(page_num: usize, page_size: usize, list: Vec<T>) -> PaginationModel<T> {
        if page_num < 1 {
            panic!("page_num cannot be less than 1")
        }
        if page_size < 1 {
            panic!("page_size cannot be less than 1")
        }
        PaginationModel {
            page_num: page_num,
            page_size: page_size,
            total_record: list.len(),
            total_page: (list.len() / page_size) + (if list.len() % page_size > 0 { 1 } else { 0 }),
            list: list
                .into_iter()
                .skip((page_num - 1) * page_size)
                .take(page_size)
                .collect::<Vec<_>>(),
        }
    }
}
