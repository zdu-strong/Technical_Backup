import axios, { AxiosResponse } from "axios";
import { EMPTY, concat, concatMap, from, interval, map, of, take, lastValueFrom, catchError } from "rxjs";

export async function getLongTermTask(callback: () => Promise<AxiosResponse<string>>): Promise<any> {
  return await lastValueFrom(of(null).pipe(
    concatMap(() => from(callback())),
    map((response) => response.data),
    concatMap((id: string) => {
      return concat(of(null), interval(1000)).pipe(
        concatMap(() => from(axios.get<boolean>(`/long-term-task/is-done`, { params: { id } }))),
        concatMap((response) => {
          if (response.data) {
            return of(id);
          } else {
            return EMPTY;
          }
        }),
        take(1)
      );
    }),
    concatMap((id) => from(axios.get(`/long-term-task`, { params: { id } }))),
    map((response) => {
      return response.data!;
    }),
    catchError((error, caught) => {
      if (typeof error!.message === 'string' && error.message.includes(ErrorMessageOfTheTaskFailedBecauseItStopped)) {
        return caught;
      } else {
        throw error;
      }
    })
  ));
}

export const ErrorMessageOfTheTaskFailedBecauseItStopped = "The task failed because it stopped";
