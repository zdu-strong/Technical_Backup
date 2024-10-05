import { timer } from 'rxjs';
import { electron, page, action } from '@/index';

test('', async () => {
  const GameRenderer = await page.Game.GameRenderer();
  await GameRenderer.hover({ position: { x: 0, y: 0 } })
  await electron.window.mouse.move(200, 200)
  await electron.window.mouse.down({ button: 'left' })
  await electron.window.mouse.move(200, 210);
  await electron.window.mouse.up({ button: "left" });
  await timer(2000).toPromise();
})

beforeEach(async () => {
  await action.OpenProgram();
  const EnterTheGame = await page.Home.EnterTheGame();
  await EnterTheGame.click()
  const GameRenderer = await page.Game.GameRenderer();
  expect(await GameRenderer.isVisible()).toBeTruthy();
})

afterEach(async () => {
  await electron.application.close();
})