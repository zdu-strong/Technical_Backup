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
        [
            "docker build",
            "-t react",
            "-f ./Dockerfile",
            "../../../",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, "./client"),
        }
    );
    execSync(
        [
            "docker build",
            "-t springboot",
            "-f ./Dockerfile",
            "../../../",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, "./server"),
        }
    );
};

export default run();