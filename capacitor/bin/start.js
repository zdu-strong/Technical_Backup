const execa = require('execa')
const inquirer = require('inquirer')
const linq = require('linq')
const fs = require('fs')
const getPort = require('get-port')
const treeKill = require('tree-kill')
const util = require('util')
const path = require('path')
const waitOn = require('wait-on')
const axios = require('axios')
const os = require('os')

async function main() {
  if (isTestEnvironment()) {
    await runCapacitorForCypress();
    process.exit();
  }
  const avaliablePort = await getPort();
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = getAndroidSdkRootPath();
  await addPlatformSupport(isRunAndroid);
  const deviceList = await getDeviceList(isRunAndroid);
  await buildReact();
  const { childProcessOfReact } = await startReact(avaliablePort);
  const [childProcessOfCapacitor] = await createChildProcessOfCapacitor(isRunAndroid, avaliablePort, androidSdkRootPath, deviceList);
  await Promise.race([childProcessOfReact, childProcessOfCapacitor]);
  await util.promisify(treeKill)(childProcessOfReact.pid).catch(async () => null);
  await util.promisify(treeKill)(childProcessOfCapacitor.pid).catch(async () => null);
  process.exit();
}

async function buildReact() {
  const folderPathOfBuild = path.join(__dirname, "..", "build");
  const folderPathOfPublic = path.join(__dirname, "..", "public");
  await fs.promises.cp(folderPathOfPublic, folderPathOfBuild, { recursive: true, force: true });
}

async function startReact(avaliablePort) {
  const childProcessOfReact = execa.command(
    [
      "react-app-rewired start",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "BROWSER": "NONE",
        "PORT": String(avaliablePort),
        "GENERATE_SOURCEMAP": "false",
      },
    }
  );

  await Promise.race([childProcessOfReact, waitOn({ resources: [`http://127.0.0.1:${avaliablePort}`] })]);
  for (let i = 2000; i > 0; i--) {
    await axios.get(`http://127.0.0.1:${avaliablePort}`);
  }
  return { childProcessOfReact };
}

function isTestEnvironment() {
  return process.env.CAPACITOR_CYPRESS_IS_TEST === "true";
}

async function runCapacitorForCypress() {
  await execa.command(
    [
      "react-app-rewired start",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "BROWSER": "NONE",
        "GENERATE_SOURCEMAP": "false",
      },
    }
  );
}

async function getIsRunAndroid() {
  let isRunAndroid = true;
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
  return isRunAndroid;
}

function getAndroidSdkRootPath() {
  let androidSdkRootPath = path.join(os.homedir(), "AppData/Local/Android/sdk").replace(new RegExp("\\\\", "g"), "/");
  if (os.platform() !== "win32") {
    androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
  }
  return androidSdkRootPath;
}

async function createChildProcessOfCapacitor(isRunAndroid, avaliablePort, androidSdkRootPath, deviceList) {
  await execa.command(
    [
      `cap sync`,
      "--deployment",
      `${isRunAndroid ? "android" : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`
      } : {
      }),
    }
  );
  await addAndroidPermissions(isRunAndroid);
  await usesCleartextTraffic(isRunAndroid);
  await execa.command(
    [
      `cap run`,
      "--no-sync",
      `${deviceList.length === 1 ? `--target=${linq.from(deviceList).single()}` : ''}`,
      `${isRunAndroid ? "android" : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`,
      } : {
      }),
    }
  );
  const childProcess = execa.command(
    [
      `cap run`,
      `--no-sync`,
      `--live-reload`,
      `--port ${avaliablePort}`,
      `${deviceList.length === 1 ? `--target ${linq.from(deviceList).single()}` : ''}`,
      `${isRunAndroid ? 'android' : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`,
      } : {
      }),
    }
  );
  return [childProcess];
}

async function addPlatformSupport(isRunAndroid) {
  await execa.command(
    [
      `cap add`,
      `${isRunAndroid ? 'android' : 'ios'}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

async function getDeviceList(isRunAndroid) {
  let deviceList = [];
  if (isRunAndroid) {
    await execa.command(
      [
        `cap run`,
        `--list`,
        `${isRunAndroid ? 'android' : 'ios'}`
      ].join(" "),
      {
        stdio: "inherit",
        cwd: path.join(__dirname, ".."),
      }
    );
    const { stdout: androidDeviceOutput } = await execa.command(
      [
        `cap run`,
        `--list`,
        `${isRunAndroid ? 'android' : 'ios'}`,
      ].join(" "),
      {
        stdio: "pipe",
        cwd: path.join(__dirname, ".."),
      }
    );

    const androidDeviceOutputList = linq.from(androidDeviceOutput.split("\r\n")).selectMany(item => item.split("\n")).toArray();
    const startIndex = androidDeviceOutputList.findIndex((item) => item.includes('-----'));
    if (startIndex < 0) {
      throw new Error("No available Device!")
    }
    deviceList = linq.from(androidDeviceOutputList)
      .skip(startIndex + 1)
      .select(item => linq.from(item.split(new RegExp("\\s\\s+")))
        .select(item => item.trim()).toArray()
      )
      .where(s => s.some(m => m.trim() === "API 35"))
      .groupBy(() => "")
      .selectMany(s => {
        if (s.count() > 1) {
          return s.where(m => m.some(n => n.includes("Pixel 9"))).toArray();
        }
        return s.toArray();
      })
      .select(s => linq.from(s)
        .last()
      )
      .toArray();
    if (!deviceList.length) {
      throw new Error("No available Device!")
    }
    if (deviceList.length === 1) {
      return deviceList;
    }
    throw new Error("More than one available Device!")
  }
  return deviceList;
}

async function addAndroidPermissions(isRunAndroid) {
  if (!isRunAndroid) {
    return;
  }
  const androidManifestFilePath = path.join(__dirname, "..", "android/app/src/main", "AndroidManifest.xml");
  const content = await fs.promises.readFile(androidManifestFilePath, { encoding: "utf-8" });
  const textList = linq.from(content.split("\r\n")).selectMany(s => s.split("\n")).toArray();
  const permissionList = [
    `    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />`,
    `    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`,
  ];
  const index = textList.findIndex(s => s.includes("</manifest>"));
  if (index < 0) {
    throw new Error("no manifest tag found")
  }
  textList.splice(index, 0, ...permissionList);
  await fs.promises.writeFile(androidManifestFilePath, textList.join("\n"), "utf8");
}

async function usesCleartextTraffic(isRunAndroid) {
  if (!isRunAndroid) {
    return;
  }
  const androidManifestFilePath = path.join(__dirname, "..", "android/app/src/main", "AndroidManifest.xml");
  const content = await fs.promises.readFile(androidManifestFilePath, { encoding: "utf-8" });
  const textList = linq.from(content.split("\r\n")).selectMany(s => s.split("\n")).toArray();
  const applicationAttributeList = [
    `        android:usesCleartextTraffic="true"`,
  ];
  const index = textList.findIndex(s => s.includes("    <application"));
  if (index < 0) {
    throw new Error("no application tag found")
  }
  textList.splice(index + 1, 0, ...applicationAttributeList);
  await fs.promises.writeFile(androidManifestFilePath, textList.join("\n"), "utf-8");
}

module.exports = main()