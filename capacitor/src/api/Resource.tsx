import { ServerAddress } from "@/common/Server";
import { runWoker } from '@/common/WebWorkerUtils';
import { isMobilePhone } from "@/common/is-mobile-phone";
import axios from "axios";
import { Subject, timer } from "rxjs";

export async function upload(
  file: File,
  uploadProgressSubject?: UploadProgressSubjectType,
): Promise<{ url: string, downloadUrl: string }> {
  if (isMobilePhone) {
    for (let i = 10; i > 0; i--) {
      await timer(1).toPromise();
    }
  }
  /* Each piece is 10MB */
  const everySize = 1024 * 1024 * 10;
  if (file.size <= everySize) {
    const formData = new FormData();
    formData.set("file", file);
    const { data: url } = await axios.post(`/upload/resource`, formData);
    return {
      url: url,
      downloadUrl: `/download${url}`,
    };
  }

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
