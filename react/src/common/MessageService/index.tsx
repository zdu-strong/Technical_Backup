import { v1 } from 'uuid';
import { observable } from 'mobx-react-use-autorun'
import { FormattedMessage } from 'react-intl';
import { GlobalExactMessageMatch } from '@/common/MessageService/js/GlobalExactMessageMatch'
import { ReactNode } from 'react';
import { getFuzzyMessageMatch } from '@/common/MessageService/js/GlobalFuzzyMessageMatch';

export const MessageService = {
  error: (message: string | string[] | Error | Error[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.error, message)
  },
  warning: (message: string | string[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.warning, message)
  },
  info: (message: string | string[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.info, message)
  },
  success: (message: string | string[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.success, message)
  }
}

export const GlobalMessageList = observable([]) as { id: string, message: ReactNode, type: string }[];

export const MESSAGE_TYPE_ENUM = {
  error: "error" as "error",
  warning: "warning" as "warning",
  info: "info" as "info",
  success: "success" as "success",
}

export function getMessageObject(type: "error" | "warning" | "info" | "success", message: any) {
  const messageString = getMessageString(message);
  const messageOfI18n = getI18nMessageReactNode(messageString);
  return {
    id: v1(),
    message: messageOfI18n,
    type: type
  }
}

function getI18nMessageReactNode(message: string) {
  let messageOfI18n = getMessageString(message);
  if (GlobalExactMessageMatch[message]) {
    return <FormattedMessage id={GlobalExactMessageMatch[message]} defaultMessage={message} />
  } else {
    return getFuzzyMessageMatch(messageOfI18n);
  }
}

function handleMessage(type: "error" | "warning" | "info" | "success", message: string | string[] | Error | Error[] | any) {
  GlobalMessageList.splice(0, GlobalMessageList.length);
  if (message instanceof Array) {
    for (const messageItem of message) {
      handleMessage(type, messageItem)
    }
  } else {
    GlobalMessageList.push(getMessageObject(type, message))
  }
}

function getMessageString(message: any): string {
  if (typeof message === "object" && message instanceof Array) {
    return getMessageString(message![0]);
  }

  let messageContent = "";
  if (typeof message === "string") {
    messageContent = message;
  } else if (typeof message === "number") {
    messageContent = String(message);
  } else if (typeof message!.message === "string") {
    messageContent = message.message;
  } else if (typeof message!.error === "string") {
    messageContent = message.error;
  }
  return messageContent;
}
