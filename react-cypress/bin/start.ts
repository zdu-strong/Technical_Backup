import execa from 'execa'
import getPort from 'get-port'
import treeKill from 'tree-kill'
import util from 'util'
import path from 'path'
import WaitOn from 'wait-on';
import { timer } from 'rxjs'

async function main() {
  const { availableServerPort, childProcessOfServer } = await startServer();
  const { avaliableClientPort, childProcessOfReact } = await startClient(availableServerPort);
  const { childProcessOfCypress } = await startCypress(avaliableClientPort);
  const { childProcessOfEslint } = await startEslint();

  await Promise.race([childProcessOfServer, childProcessOfReact, childProcessOfCypress, childProcessOfEslint]);
  await util.promisify(treeKill)(childProcessOfServer.pid!).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfReact.pid!).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfCypress.pid!).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfEslint.pid!).catch(async () => null);

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
    WaitOn({
      resources: [
        `http://127.0.0.1:${availableServerPort}`
      ]
    })
  ]);
  for (let i = 1000; i > 0; i--) {
    await timer(1).toPromise();
  }
  return { childProcessOfServer, availableServerPort };
}

async function startClient(availableServerPort: number) {
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
    WaitOn({
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

async function startCypress(avaliableClientPort: number) {
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
      "npx -y -p typescript -p ts-node ts-node --skipProject bin/eslint.ts",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, '..'),
      extendEnv: true,
    }
  )
  return { childProcessOfEslint };
}

export default main()