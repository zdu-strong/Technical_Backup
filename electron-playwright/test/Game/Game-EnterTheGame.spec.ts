import { timer } from 'rxjs';
import { electron, page, action } from '@/index';

test('', async () => {
  const GameRenderer = await page.Game.GameRenderer();
  expect(await GameRenderer.isVisible()).toBeTruthy();
  await timer(2000).toPromise();
})

beforeEach(async () => {
  await action.OpenProgram();
  const EnterTheGame = await page.Home.EnterTheGame();
  await EnterTheGame.click()
})

afterEach(async () => {
  await electron.application.close();
})