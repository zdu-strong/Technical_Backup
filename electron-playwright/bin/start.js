const execa = require('execa')
const getPort = require('get-port')
const treeKill = require('tree-kill')
const util = require('util')
const path = require('path')
const waitOn = require('wait-on')
const { timer } = require('rxjs')

async function main() {
  const { avaliablePort, childProcessOfElectron } = await startElectron();
  const { childProcessOfPlaywright } = await startPlaywright(avaliablePort);

  await Promise.race([childProcessOfElectron, childProcessOfPlaywright]);
  await util.promisify(treeKill)(childProcessOfElectron.pid).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfPlaywright.pid).catch(async () => null);

  process.exit();
}

async function startPlaywright(avaliablePort) {
  const childProcessOfPlaywright = execa.command(
    [
      "jest --runInBand --watch --config ./test/jest.json",
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '..'),
      extendEnv: true,
      env: {
        "ELECTRON_DISABLE_SECURITY_WARNINGS": "true",
        "ELECTRON_PORT": String(avaliablePort),
      }
    }
  );
  return { childProcessOfPlaywright };
}

async function startElectron() {
  const avaliablePort = await getPort();
  const childProcessOfElectron = execa.command(
    [
      'npm start',
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '../..', "electron"),
      extendEnv: true,
      env: {
        "ELECTRON_PORT": String(avaliablePort),
        "ELECTRON_IS_TEST": "true",
      }
    }
  );
  await Promise.race([childProcessOfElectron, waitOn({ resources: [`http://127.0.0.1:${avaliablePort}`] })]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { avaliablePort, childProcessOfElectron };
}

module.exports = main()