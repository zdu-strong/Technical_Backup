import { execSync } from "child_process";
import path from 'path';
import os from 'os';
import inquirer from "inquirer";
import linq from 'linq';

const run = async () => {
    if (os.platform() !== "win32" && os.platform() !== "darwin") {
        throw new Error("The development of linux has not been considered yet");
    }
    let isRunAndroid = true;
    let androidSdkRootPath = path.join(os.homedir(), "AppData/Local/Android/sdk").replace(new RegExp("\\\\", "g"), "/");
    if (os.platform() === "darwin") {
        androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
    }
    if (os.platform() === "darwin") {
        const MOBILE_PHONE_ENUM = {
            iOS: "iOS",
            Android: "Android"
        };
        const answers = await inquirer.prompt([{
            type: "list",
            name: "mobile phone",
            message: "Do you wish to develop for android or ios?",
            default: MOBILE_PHONE_ENUM.iOS,
            choices: [
                {
                    name: MOBILE_PHONE_ENUM.iOS,
                    value: MOBILE_PHONE_ENUM.iOS,
                },
                {
                    name: MOBILE_PHONE_ENUM.Android,
                    value: MOBILE_PHONE_ENUM.Android,
                },
            ],
        }]);
        const chooseAnswer = linq.from(Object.values(answers)).single();
        if (chooseAnswer === MOBILE_PHONE_ENUM.iOS) {
            isRunAndroid = false;
        } else if (chooseAnswer === MOBILE_PHONE_ENUM.Android) {
            isRunAndroid = true;
        } else {
            throw new Error("Please select the type of mobile phone system to be developed!");
        }
    }
    if (isRunAndroid) {
        execSync(
            [
                "cross-env",
                "ANDROID_SDK_ROOT=\"" + androidSdkRootPath + "\"",
                "ionic capacitor build android",
            ].join(" "),
            {
                stdio: "inherit",
                cwd: path.join(__dirname, ".."),
            }
        );
    } else {
        execSync(
            [
                "cross-env",
                "ionic capacitor build ios",
            ].join(" "),
            {
                stdio: "inherit",
                cwd: path.join(__dirname, ".."),
            }
        );
    }
};

export default run();