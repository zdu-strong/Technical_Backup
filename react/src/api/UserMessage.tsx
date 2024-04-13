import { ServerAddress, getWebSocketServerAddress } from "@/common/Server";
import { UserMessageModel } from "@/model/UserMessageModel";
import { UserMessageWebSocketReceiveModel } from "@/model/UserMessageWebSocketReceiveModel";
import axios from "axios";
import { Observable, Subject, map, switchMap } from "rxjs";
import makeWebSocketObservable, { GetWebSocketResponses } from "rxjs-websockets";
import { TypedJSON } from "typedjson";

export async function sendMessage(body: {
  content?: string,
  url?: string,
}) {
  const { data } = await axios.post<UserMessageModel>("/user_message/send", { content: body.content, url: body.url });
  return new TypedJSON(UserMessageModel).parse(data)!;
}

export function getUserMessageWebsocket(websocketInput$: Subject<{
  pageNum: number,
  isCancel: boolean,
}>) {
  const url = getWebSocketServerAddress(`/user_message/websocket`);
  const websocketOutput$ = makeWebSocketObservable(url).pipe(
    switchMap((getResponses: GetWebSocketResponses) => {
      return getResponses(websocketInput$.pipe(map(data => JSON.stringify(data))));
    }),
    map((data) => new TypedJSON(UserMessageWebSocketReceiveModel).parse(data)!),
    map((data) => {
      for (const message of data.list) {
        if (message.url) {
          message.url = `${ServerAddress}/download${message.url}`;
        }
      }
      return data;
    }),
  );
  return websocketOutput$ as any as Observable<UserMessageWebSocketReceiveModel>;
}

export async function recallMessage(id: string) {
  await axios.put<void>("/user_message/recall", null, { params: { id } })
}