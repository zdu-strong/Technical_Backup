import { ReplaySubject } from "rxjs";
import WebworkerPromise from 'webworker-promise'

export async function runWoker(worker: Worker, params?: any) {
  const workerSubject = new ReplaySubject();
  worker.addEventListener("error", () => {
    workerSubject.error(new Error("Network Error"));
  });
  return await Promise.race([new WebworkerPromise(worker).postMessage(params || {}), workerSubject.toPromise()]);
}