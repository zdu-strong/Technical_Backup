import '@/common/Server';
import registerWebworker from 'webworker-promise/lib/register'
import axios from "axios";
import { concatMap, from, lastValueFrom, map, range, timer, toArray } from "rxjs";
import * as mathjs from 'mathjs'
import api from '@/api';
import { addMilliseconds } from 'date-fns'
import linq from 'linq'

registerWebworker(async ({
  ServerAddress,
  file
}: {
  ServerAddress: string
  file: File
}, emit) => {
  if (!ServerAddress) {
    throw new Error("Server Address cannot be empty");
  }
  axios.defaults.baseURL = ServerAddress;
  for (let i = 10; i > 0; i--) {
    await timer(1).toPromise();
  }
  /* Each piece is 10MB */
  const everySize = 1024 * 1024 * 10;
  // unit is milliseconds
  const durationOfCalculateSpeed = 1000;
  const uplodStartDate = new Date();
  type UploadProgessType = {
    createDate: Date,
    loaded: number,
    total: number,
  };
  let uploadProgressList: UploadProgessType[] = [{
    createDate: new Date(),
    loaded: 0,
    total: file.size,
  }];
  const url = await lastValueFrom(range(1, mathjs.max(mathjs.ceil(mathjs.divide(file.size, everySize)), 1)).pipe(
    concatMap((pageNum) => {
      const formData = new FormData();
      formData.set("file", new File([file.slice((pageNum - 1) * everySize, pageNum * everySize)], file.name, file));
      return from(axios.post(`/upload/resource`, formData, {
        onUploadProgress(progressEvent) {
          const loaded = (pageNum - 1) * everySize + Math.floor(mathjs.divide(mathjs.multiply((formData.get("file") as File).size, progressEvent.loaded), progressEvent.total!));
          const total = file.size;
          const nowDate = new Date();
          const beforeDate = addMilliseconds(nowDate, 0 - durationOfCalculateSpeed);
          let uploadProgess: UploadProgessType = linq.from(uploadProgressList).where(s => s.createDate.getTime() <= beforeDate.getTime()).orderByDescending(s => s.createDate).firstOrDefault()!;
          if (!uploadProgess) {
            uploadProgess = linq.from(uploadProgressList).orderBy(s => s.createDate).first();
          } else {
            while (true) {
              const index = uploadProgressList.findIndex(s => s.createDate.getTime() < uploadProgess.createDate.getTime());
              if (index < 0) {
                break;
              }
              uploadProgressList.splice(0, 1);
            }
          }
          // unit is B/second
          let speed = Math.floor(mathjs.divide(loaded - uploadProgess.loaded, mathjs.divide(nowDate.getTime() - uploadProgess.createDate.getTime(), 1000)));
          if (loaded === total) {
            speed = Math.floor(mathjs.divide(total, mathjs.divide(nowDate.getTime() - uplodStartDate.getTime(), 1000)));
          }
          uploadProgressList.push({
            createDate: nowDate,
            loaded,
            total,
          });
          emit("onUploadProgress", {
            total,
            loaded,
            speed,
          });
        }
      }));
    }),
    map((response) => response.data),
    toArray(),
    concatMap((urlList) => from(api.LongTermTask.getLongTermTask(async () => axios.post<string>(`/upload/merge`, urlList)))),
    map((result) => result as string)
  ));
  return url;
});
