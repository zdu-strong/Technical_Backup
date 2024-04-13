import axios from "axios";
import { EMPTY, concat, concatMap, from, interval, of, take } from "rxjs";
import { Serializable, TypedJSON } from "typedjson";

export async function getLongTermTask<T>(url: string, rootConstructor?: Serializable<T>): Promise<T> {
  return await concat(of(null), interval(1000)).pipe(
    concatMap(() => from(axios.get(url))),
    concatMap((response) => {
      if (response.data.isDone) {
        if (rootConstructor) {
          response.data.result = new TypedJSON(rootConstructor).parse(response.data.result)!;
        }
        return of(response.data.result);
      } else {
        return EMPTY;
      }
    }),
    take(1),
  ).toPromise()!;
}

export const ErrorMessageOfTheTaskFailedBecauseItStopped = "The task failed because it stopped";
