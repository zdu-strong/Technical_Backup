const execa = require('execa')
const getPort = require('get-port')
const treeKill = require('tree-kill')
const util = require('util')
const path = require('path')
const waitOn = require('wait-on')
const { timer } = require('rxjs')
const { default: axios } = require("axios")

async function main() {
  const { availableServerPort, childProcessOfServer } = await startServer();
  const { avaliableClientPort, childProcessOfReact } = await startClient(availableServerPort);
  const { childProcessOfCypress } = await startCypress(avaliableClientPort);
  const { childProcessOfEslint } = await startEslint();

  await Promise.race([childProcessOfServer, childProcessOfReact, childProcessOfCypress, childProcessOfEslint]);
  await util.promisify(treeKill)(childProcessOfServer.pid).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfReact.pid).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfCypress.pid).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfEslint.pid).catch(async () => null);

  process.exit();
}

async function startServer() {
  const availableServerPort = await getPort();
  const childProcessOfServer = execa.command(
    [
      './mvn clean compile spring-boot:run',
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '../../springboot'),
      extendEnv: true,
      env: {
        "SPRING_DATASOURCE_URL": `jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`,
        "SERVER_PORT": `${availableServerPort}`,
        "SPRING_DATASOURCE_DRIVER_CLASS_NAME": `org.h2.Driver`,
        "SPRING_JPA_HIBERNATE_DDL_AUTO": `update`,
        "SPRING_JPA_DATABASE_PLATFORM": `com.springboot.project.common.database.CustomH2Dialect`,
        "SPRING_LIQUIBASE_ENABLED": `false`,
        "PROPERTIES_STORAGE_ROOT_PATH": `defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a`,
      }
    }
  );
  await Promise.race([
    childProcessOfServer,
    waitOn({
      resources: [
        `http://127.0.0.1:${availableServerPort}`
      ]
    })
  ]);
  while (true) {
    const { data: hasInit } = await axios.get(`http://127.0.0.1:${availableServerPort}/system_init`);
    if (hasInit) {
      break;
    }
    await timer(100).toPromise();
  }
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { childProcessOfServer, availableServerPort };
}

async function startClient(availableServerPort) {
  const avaliableClientPort = await getPort();
  const childProcessOfReact = execa.command(
    [
      'npm start',
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '../../react'),
      extendEnv: true,
      env: {
        "BROWSER": 'NONE',
        "PORT": `${avaliableClientPort}`,
        "REACT_APP_SERVER_PORT": `${availableServerPort}`,
      }
    }
  );

  await Promise.race([
    childProcessOfReact,
    waitOn({
      resources: [
        `http://127.0.0.1:${avaliableClientPort}`
      ]
    })
  ]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { avaliableClientPort, childProcessOfReact };
}

async function startCypress(avaliableClientPort) {
  const childProcessOfCypress = execa.command(
    [
      'cypress open',
      "--e2e"
    ].join(' '),
    {
      stdio: 'inherit',
      cwd: path.join(__dirname, '..'),
      extendEnv: true,
      env: {
        "CYPRESS_BASE_URL": `http://127.0.0.1:${avaliableClientPort}`,
        "CYPRESS_VERIFY_TIMEOUT": "120000",
      }
    }
  );
  return { childProcessOfCypress };
}

async function startEslint() {
  const childProcessOfEslint = execa.command(
    [
      "node bin/eslint.js",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, '..'),
      extendEnv: true,
    }
  )
  return { childProcessOfEslint };
}

module.exports = main()