import axios from "axios";
import { WebSocketServerAddress } from "../common/Server";
import qs from 'qs'

export function sendMessage(email: string, content: string) {
    return axios.post("/user_message/send", { content, user: { email } });
}

export function getUserMessageWebsocketAddress(email: string) {
    return `${WebSocketServerAddress}/message?${qs.stringify({ email })}`;
}

export function getMessageByIndex(email: string, index: number) {
    return axios.get("/user_message/get_by_index", { params: { email, index } })
}

export function recallMessage(id: string) {
    return axios.post("/user_message/recall", null, { params: { id } })
}