import path from 'path';
import { _electron } from 'playwright'
import os from 'os'
import { page, setApplication, setWindow } from '@/index'

export async function OpenProgram(): Promise<void> {
  const electronOfThis = await _electron.launch({
    args: [path.join(__dirname, "../../..", "electron", "dist/index.js")],
    executablePath: getExecutablePath(),
    cwd: path.join(__dirname, "../../..", "electron"),
    locale: "en-US",
  });
  const window = await electronOfThis.firstWindow();
  setApplication(electronOfThis);
  setWindow(window);
  const CPUUsageText = await page.Home.CurrentRandomNumber()
  expect(await CPUUsageText.isVisible()).toBeTruthy()
}

function getExecutablePath() {
  let executablePath = path.join(__dirname, "../../..", "electron");
  if (os.platform() === "win32") {
    executablePath = path.join(executablePath, "node_modules/.bin/electron.cmd");
  } else {
    executablePath = path.join(executablePath, "node_modules/.bin/electron");
  }
  return executablePath;
}