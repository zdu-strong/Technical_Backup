import { getDownloadResourceUrl, getWebSocketServerAddress, handleErrorWhenNotSignInToSignIn } from "@/common/Server";
import { UserMessageModel } from "@/model/UserMessageModel";
import { UserMessageWebSocketReceiveModel } from "@/model/UserMessageWebSocketReceiveModel";
import axios from "axios";
import { Subject, catchError, map, switchMap, tap, of, concatMap, ReplaySubject } from "rxjs";
import makeWebSocketObservable, { GetWebSocketResponses } from "rxjs-websockets";
import { TypedJSON } from "typedjson";

export async function sendMessage(body: {
  content?: string,
  url?: string,
}) {
  const { data } = await axios.post("/user-message/send", { content: body.content, url: body.url });
  return new TypedJSON(UserMessageModel).parse(data)!;
}

export async function recallMessage(id: string) {
  await axios.post<void>("/user-message/recall", null, { params: { id } })
}

export async function deleteMessage(id: string) {
  await axios.delete<void>("/user-message/delete", { params: { id } })
}

export function getUserMessageWebsocket(webSocketInput?: Subject<{
  pageNum: number,
  isCancel: boolean,
}>) {
  if (!webSocketInput) {
    webSocketInput = new ReplaySubject<{
      pageNum: number,
      isCancel: boolean,
    }>(0);
  }
  return of("")
    .pipe(
      map(() => getWebSocketServerAddress(`/web-socket/user-message`)),
      concatMap((url) => makeWebSocketObservable(url)),
      switchMap((getResponses: GetWebSocketResponses) => {
        return getResponses(webSocketInput!.pipe(map(data => JSON.stringify(data))));
      }),
      map((data) => new TypedJSON(UserMessageWebSocketReceiveModel).parse(data)!),
      tap((data) => {
        for (const message of data.items) {
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
}