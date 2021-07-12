import execa from 'execa';
import getPort from 'get-port';
import treeKill from 'tree-kill';
import util from 'util';
import path from 'path';

const run = async () => {
    const availableServerPort = await getPort();
    const childProcessOfServer = execa.command(
        [
            "./mvn clean spring-boot:run --define StorageRootPath=defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, "../../springboot"),
            env: {
                SPRING_DATASOURCE_URL: "jdbc:h2:mem:wiki_test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&createDatabaseIfNotExist=true;MODE=MYSQL;DB_CLOSE_DELAY=-1",
                SERVER_PORT: `${availableServerPort}`,
                SPRING_DATASOURCE_USERNAME: 'sa',
                SPRING_DATASOURCE_PASSWORD: 'sa',
                SPRING_DATASOURCE_DRIVER_CLASS_NAME: 'org.h2.Driver',
                SPRING_JPA_HIBERNATE_DDL_AUTO: 'update',
                SPRING_JPA_DATABASE_PLATFORM: 'com.springboot.project.common.mysql.CustomH2Dialect',
                SPRING_LIQUIBASE_ENABLED: 'false',
            }
        }
    );
    await Promise.race([childProcessOfServer, execa.command(`wait-on http://localhost:${availableServerPort}`)]);
    const avaliablePort = await getPort();
    const childProcessOfReact = execa.command(
        [
            "cross-env",
            "BROWSER=NONE",
            `PORT=${avaliablePort}`,
            `REACT_APP_SERVER_PORT=${availableServerPort}`,
            "npm start",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, "../../react"),
        }
    );
    await Promise.race([childProcessOfReact, execa.command(`wait-on http://localhost:${avaliablePort}`)]);
    const childProcessOfCypress = execa.command(
        [
            "cross-env",
            `CYPRESS_BASE_URL=http://localhost:${avaliablePort}`,
            "cypress open"
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
    await Promise.race([childProcessOfReact, childProcessOfCypress, childProcessOfServer]);
    await util.promisify(treeKill)(childProcessOfServer.pid!).catch(async () => null);
    await util.promisify(treeKill)(childProcessOfReact.pid!).catch(async () => null);
    await util.promisify(treeKill)(childProcessOfCypress.pid!).catch(async () => null);
    process.exit();
};


export default run();