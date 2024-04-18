import { Subject } from "rxjs";
import { ServerAddress } from "@/common/Server";
import { runWoker } from '@/common/WebWorker/WebWorkerUtils';

export async function upload(
  file: File,
  uploadProgressSubject?: UploadProgressSubjectType,
): Promise<{ url: string, downloadUrl: string }> {
  const worker = new Worker(new URL('../common/WebWorker/UploadResource/UploadResource.worker', import.meta.url), { type: "module" });
  if (uploadProgressSubject) {
    worker.addEventListener("message", (e) => {
      if (e.data[2] !== "onUploadProgress") {
        return;
      }
      uploadProgressSubject.next(e.data[3]);
    });
  }
  return await runWoker(worker,
    {
      ServerAddress,
      file: file
    }
  );
}

export type UploadProgressSubjectType = Subject<{
  total: number,
  loaded: number,
  // unit is B/second. It is the average speed when the upload is complete.
  speed: number,
}>;
