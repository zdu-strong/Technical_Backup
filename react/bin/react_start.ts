import { execSync } from "child_process";
import path from 'path';

const run = async () => {
    execSync(
        [
            "cross-env",
            "TS_NODE_SKIP_PROJECT=true",
            "ts-node bin/pre_load_configuration.ts",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
    execSync(
        "react-app-rewired start",
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
        }
    );
};

export default run();