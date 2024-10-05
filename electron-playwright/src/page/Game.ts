import { timer } from 'rxjs'
import { electron } from "@/index"

export default {
  GameRenderer: async () => {
    const xpath = `//div[@role="dialog"]//canvas`;
    const loadingXpath = `//canvas/../div`;
    await electron.window.waitForSelector(xpath, { timeout: 10000 })
    for (let i = 1000 * 60; i >= 0; i--) {
      const isHidden = await electron.window.isHidden(loadingXpath);
      if (isHidden) {
        break;
      }
      await timer(1).toPromise();
      if (i === 0) {
        throw new Error("Game failed to load!")
      }
    }
    return electron.window.locator(xpath)
  },
}