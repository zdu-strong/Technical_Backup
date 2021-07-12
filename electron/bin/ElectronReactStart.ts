import execa from 'execa';
import getPort from 'get-port';
import treeKill from 'tree-kill';
import util from 'util';
import path from 'path';

const run = async () => {
    const avaliablePort = await getPort();
    const childProcessOfReact = execa.command(
        [
            "cross-env",
            "BROWSER=NONE",
            "PORT=" + String(avaliablePort),
            "react-scripts start",
        ].join(" "),

        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
    await Promise.race([childProcessOfReact, execa.command(`wait-on http://localhost:${avaliablePort}`)]);
    const childProcessOfElectron = execa.command(
        [
            "cross-env",
            "ELECTRON_DISABLE_SECURITY_WARNINGS=true",
            "PORT=" + String(avaliablePort),
            "electron .",
        ].join(" "),

        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    )
    await Promise.race([childProcessOfReact, childProcessOfElectron]);
    await util.promisify(treeKill)(childProcessOfReact.pid!).catch(async () => null);
    await util.promisify(treeKill)(childProcessOfElectron.pid!).catch(async () => null);
    process.exit();
};

export default run();