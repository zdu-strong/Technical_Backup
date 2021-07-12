export type MessageType = {
    id: string,
    isDelete: boolean,
    isRecall: boolean,
    createDate: string,
    updateDate: string,
    content: string,
    totalPage: number,
    pageNum: number
    user: {
        id: string,
        username: string,
        email: string,
    }
}