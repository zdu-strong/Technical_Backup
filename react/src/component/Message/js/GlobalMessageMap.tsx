import { observable } from "mobx-react-use-autorun";
import { MessageType } from "./MessageType";

export const globalMessageMap = observable({

}) as Record<string, MessageType>;