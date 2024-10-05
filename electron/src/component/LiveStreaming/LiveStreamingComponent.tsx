import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { Subscription, concat, concatMap, from, interval, tap, timer } from "rxjs";

export default observer(() => {

  const state = useMobxState({
    sourceBuffer: null as SourceBuffer | null,
    options: { mimeType: "video/webm; codecs=vp8" },
    mediaStream: null as MediaStream | null,
    mediaSource: null as MediaSource | null,
    videoUrl: "" as string,
  }, {
  });

  useMount(async (subscription) => {
    subscription.add(timer(100).pipe(
      concatMap(() => from(init(subscription)))
    ).subscribe());
  })

  async function loadMediaStream(subscription: Subscription) {
    state.mediaStream = await window.navigator.mediaDevices.getUserMedia({
      video: true,
      audio: false,
    });
    subscription.add(() => {
      state.mediaStream!.getTracks().forEach((track) => {
        track.stop();
      })
    });
  }

  async function loadMediaRecorder(subscription: Subscription) {
    const mediaRecorder = new MediaRecorder(state.mediaStream!, state.options);
    subscription.add(() => {
      mediaRecorder.stop();
    });
    mediaRecorder.ondataavailable = async (event) => {
      if (event.data.size > 0) {
        while (true) {
          if (!state.sourceBuffer?.updating) {
            state.sourceBuffer?.appendBuffer(await event.data.arrayBuffer());
            break;
          }
          await Promise.resolve(null);
        }
      }
    };
    mediaRecorder.start(16);
  }

  async function loadMediaSource(subscription: Subscription) {
    state.mediaSource = new MediaSource();
    state.mediaSource.onsourceopen = () => {
      state.sourceBuffer = state.mediaSource!.addSourceBuffer(state.options.mimeType);
      state.sourceBuffer!.mode = "segments";
    };
    const videoSrc = window.URL.createObjectURL(state.mediaSource);
    subscription.add(() => {
      window.URL.revokeObjectURL(videoSrc);
    })
    state.videoUrl = videoSrc;
    const startDate = new Date();

    subscription.add(concat(timer(5 * 1000), interval(1 * 1000)).pipe(
      tap((i) => {
        if (!state.sourceBuffer?.updating) {
          let now = new Date();
          state.sourceBuffer!.appendWindowStart = Math.floor((now.getTime() - startDate.getTime() - 2000) / 1000);
          console.log(`time ${Math.floor((now.getTime() - startDate.getTime()) / 1000)}`)
        }
      })
    ).subscribe());
  }

  async function init(subscription: Subscription) {
    await loadMediaStream(subscription);
    await loadMediaRecorder(subscription);
    await loadMediaSource(subscription);
  }

  return <>
    <video width={400} height={400} controls={true} autoPlay={true} src={state.videoUrl} key={state.videoUrl} >

    </video>
  </>
})