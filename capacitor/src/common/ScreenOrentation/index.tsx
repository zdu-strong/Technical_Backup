import { StatusBar } from '@capacitor/status-bar'
import { ScreenOrientation } from '@awesome-cordova-plugins/screen-orientation'
import { Capacitor } from '@capacitor/core'
import { AndroidFullScreen } from '@awesome-cordova-plugins/android-full-screen'
import { ReplaySubject, concatMap, from, retry } from 'rxjs'

const subject = new ReplaySubject<"LANDSCAPE" | "PORTRAIT_PRIMARY">(1);

subject.next("PORTRAIT_PRIMARY");

subject.pipe(
  concatMap((type) => from((async () => {
    if (type === "LANDSCAPE") {
      if (Capacitor.getPlatform() === "web") {
        return;
      }

      await ScreenOrientation.lock(ScreenOrientation.ORIENTATIONS.LANDSCAPE);

      if (Capacitor.getPlatform() === "android") {
        await AndroidFullScreen.immersiveMode()
      }

      await StatusBar.hide();
      await StatusBar.setOverlaysWebView({ overlay: true });
    } else if (type === "PORTRAIT_PRIMARY") {
      if (Capacitor.getPlatform() === "web") {
        return;
      }

      await ScreenOrientation.lock(ScreenOrientation.ORIENTATIONS.PORTRAIT_PRIMARY);
      await StatusBar.setOverlaysWebView({ overlay: false });

      if (Capacitor.getPlatform() === "android") {
        await AndroidFullScreen.showSystemUI()
      }

      await StatusBar.show();
    }
  })())),
  retry(),
).subscribe();

export function LANDSCAPE() {
  subject.next("LANDSCAPE");
}

export function PORTRAIT_PRIMARY() {
  subject.next("PORTRAIT_PRIMARY");
}