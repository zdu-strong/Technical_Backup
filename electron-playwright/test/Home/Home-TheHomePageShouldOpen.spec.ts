import { electron, page, action } from '@/index';


test('', async () => {
  const CurrentCPUUsage = await page.Home.CurrentRandomNumber();
  expect(await CurrentCPUUsage.isVisible()).toBeTruthy()
})

beforeEach(async () => {
  await action.OpenProgram();
})

afterEach(async () => {
  await electron.application.close();
})