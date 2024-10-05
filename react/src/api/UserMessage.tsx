import { getDownloadResourceUrl, getWebSocketServerAddress, handleErrorWhenNotSignInToSignIn } from "@/common/Server";
import { UserMessageModel } from "@/model/UserMessageModel";
import { UserMessageWebSocketReceiveModel } from "@/model/UserMessageWebSocketReceiveModel";
import axios from "axios";
import { Subject, catchError, map, switchMap, tap } from "rxjs";
import makeWebSocketObservable, { GetWebSocketResponses } from "rxjs-websockets";
import { TypedJSON } from "typedjson";

export async function sendMessage(body: {
  content?: string,
  url?: string,
}) {
  const { data } = await axios.post<UserMessageModel>("/user_message/send", { content: body.content, url: body.url });
  return new TypedJSON(UserMessageModel).parse(data)!;
}

export async function recallMessage(id: string) {
  await axios.post<void>("/user_message/recall", null, { params: { id } })
}

export async function deleteMessage(id: string) {
  await axios.delete<void>("/user_message/delete", { params: { id } })
}

export function getUserMessageWebsocket(webSocketInput: Subject<{
  pageNum: number,
  isCancel: boolean,
}>) {
  const url = getWebSocketServerAddress(`/user_message/websocket`);
  const webSocketOutput = makeWebSocketObservable(url).pipe(
    switchMap((getResponses: GetWebSocketResponses) => {
      return getResponses(webSocketInput.pipe(map(data => JSON.stringify(data))));
    }),
    map((data) => new TypedJSON(UserMessageWebSocketReceiveModel).parse(data)!),
    tap((data) => {
      for (const message of data.list) {
        if (message.url) {
          message.url = getDownloadResourceUrl(message.url);
        }
      }
    }),
    catchError((error) => {
      handleErrorWhenNotSignInToSignIn(error);
      throw error;
    }),
  );
  return webSocketOutput;
}