import { v6 } from 'uuid';
import { observable } from 'mobx-react-use-autorun'
import { FormattedMessage } from 'react-intl';
import { GlobalExactMessageMatch } from '@/common/MessageService/js/GlobalExactMessageMatch'
import { ReactNode } from 'react';
import { getFuzzyMessageMatch } from '@/common/MessageService/js/GlobalFuzzyMessageMatch';
import en_US_JSON from '@/i18n/en-US.json'

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

export const GlobalMessageList = observable([]) as { id: string, message: ReactNode, type: "error" | "warning" | "info" | "success" }[];

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
    id: v6(),
    message: messageOfI18n,
    type: type
  }
}

function getI18nMessageReactNode(message: string): ReactNode {
  if (GlobalExactMessageMatch[message]) {
    return <FormattedMessage id={GlobalExactMessageMatch[message]} defaultMessage={message} />
  }

  for (const [key, value] of Object.entries(en_US_JSON)) {
    if (value === message) {
      return <FormattedMessage id={key} defaultMessage={value} />
    }
  }

  return getFuzzyMessageMatch(message);
}

function handleMessage(type: "error" | "warning" | "info" | "success", message: string | string[] | Error | Error[] | any) {
  if (message instanceof Array) {
    GlobalMessageList.splice(0, GlobalMessageList.length);
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

  let messageContent = "Network Error";
  if (typeof message === "string" && message) {
    messageContent = message;
  } else if (typeof message === "number") {
    messageContent = String(message);
  } else if (typeof message === "object" && typeof message!.message === "string" && message!.message) {
    messageContent = message.message;
  } else if (typeof message === "object" && typeof message!.error === "string" && message!.error) {
    messageContent = message.error;
  }
  return messageContent;
}
