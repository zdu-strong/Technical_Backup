const execa = require('execa')
const getPort = require('get-port')
const treeKill = require('tree-kill')
const util = require('util')
const path = require('path')
const waitOn = require('wait-on')
const { timer } = require('rxjs')

async function main() {
  const { avaliablePort, childProcessOfReact } = await startReact();
  const { childProcessOfCypress } = await startCypress(avaliablePort);

  await Promise.race([childProcessOfReact, childProcessOfCypress]);
  await util.promisify(treeKill)(childProcessOfReact.pid).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfCypress.pid).catch(async () => null);

  process.exit();
}

async function startCypress(avaliablePort) {
  const childProcessOfCypress = execa.command(
    [
      'cypress run',
      "--e2e"
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '..'),
      extendEnv: true,
      env: {
        "CYPRESS_BASE_URL": `http://127.0.0.1:${avaliablePort}`,
        "CYPRESS_VERIFY_TIMEOUT": "120000",
      },
    }
  );
  return { childProcessOfCypress };
}

async function startReact() {
  const avaliablePort = await getPort();
  const childProcessOfReact = execa.command(
    [
      'npm start',
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '../../capacitor'),
      extendEnv: true,
      env: {
        "PORT": `${avaliablePort}`,
        "CAPACITOR_CYPRESS_IS_TEST": 'true',
      },
    }
  );
  await Promise.race([childProcessOfReact, waitOn({ resources: [`http://127.0.0.1:${avaliablePort}`] })]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { avaliablePort, childProcessOfReact };
}

module.exports = main()