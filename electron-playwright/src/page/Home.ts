import { electron } from "@/index"

export default {
  CurrentRandomNumber: async () => {
    const xpath = "//div[contains(@class, 'batteryContainer')]/div/div[contains(., 'Random number')]"
    await electron.window.waitForSelector(xpath, { timeout: 36000000 })
    return electron.window.locator(xpath)
  },
  EnterTheGame: async () => {
    const xpath = `//button[contains(.,'Enter the game')]`
    await electron.window.waitForSelector(xpath)
    return electron.window.locator(xpath)
  }
}