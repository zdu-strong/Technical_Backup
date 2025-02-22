const execa = require('execa')
const os = require('os')
const treeKill = require('tree-kill')
const util = require('util')
const path = require('path')
const inquirer = require('inquirer')
const linq = require('linq')
const fs = require('fs')
const CapacitorConfig = require('../capacitor.config')

async function main() {
  const isRunAndroid = await getIsRunAndroid();
  const androidSdkRootPath = await getAndroidSdkRootPath();
  await addPlatformSupport(isRunAndroid, androidSdkRootPath);
  await buildReact();
  await runAndroidOrIOS(isRunAndroid, androidSdkRootPath);
  await copySignedApk(isRunAndroid);
  process.exit();
}

async function runAndroidOrIOS(isRunAndroid, androidSdkRootPath) {
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
        "ANDROID_HOME": `${androidSdkRootPath}`,
      } : {
      }),
    }
  );
  await addAndroidPermissions(isRunAndroid);
  const apkSignerParentFolderPath = await getApkSignerParentFolderPath();
  const childProcess = execa.command(
    [
      `cap build`,
      ...(isRunAndroid ? [`--keystorepath ${path.relative(path.join(__dirname, "..", "android"), CapacitorConfig.android.buildOptions.keystorePath)}`] : []),
      ...(isRunAndroid ? [`--keystorepass ${CapacitorConfig.android?.buildOptions?.keystorePassword}`] : []),
      ...(isRunAndroid ? [`--keystorealias ${CapacitorConfig.android?.buildOptions?.keystoreAlias}`] : []),
      ...(isRunAndroid ? [`--keystorealiaspass ${CapacitorConfig.android?.buildOptions?.keystoreAliasPassword}`] : []),
      ...(isRunAndroid ? [`--signing-type ${CapacitorConfig.android?.buildOptions?.signingType}`] : []),
      ...(isRunAndroid ? [`--androidreleasetype ${CapacitorConfig.android?.buildOptions?.releaseType}`] : []),
      `${isRunAndroid ? "android" : "ios"}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`,
        ...(os.platform() === "win32" ? {
          Path: `${process.env.Path};${apkSignerParentFolderPath}`
        } : {}),
        ...(os.platform() !== "win32" ? {
          PATH: `${process.env.PATH}:${apkSignerParentFolderPath}`
        } : {}),
      } : {
      }),
    }
  );
  await childProcess;
  await util.promisify(treeKill)(childProcess.pid).catch(async () => null);
  await Promise.resolve(null);
}

async function buildReact() {
  await execa.command(
    [
      "react-app-rewired build",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "GENERATE_SOURCEMAP": "false",
      },
    }
  );
}

async function getAndroidSdkRootPath() {
  let androidSdkRootPath = path.join(os.homedir(), "AppData/Local/Android/sdk").replace(new RegExp("\\\\", "g"), "/");
  if (os.platform() !== "win32") {
    androidSdkRootPath = path.join(os.homedir(), "Android/Sdk").replace(new RegExp("\\\\", "g"), "/");
  }
  return androidSdkRootPath;
}

async function getApkSignerParentFolderPath() {
  const androidSdkRootPath = await getAndroidSdkRootPath();
  const buildToolsPath = path.join(androidSdkRootPath, "build-tools");
  const fileNameList = await fs.promises.readdir(buildToolsPath);
  const fileName = linq.from(fileNameList).select(s => s.split(".")).select(s => linq.from(s).first()).select(s => Number(s)).orderByDescending(s => s).first();
  const apkSignerParentFolderPath = path.normalize(path.join(buildToolsPath, linq.from(fileNameList).where(s => s.startsWith(String(fileName) + ".")).first()));
  return apkSignerParentFolderPath;
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

async function copySignedApk(isRunAndroid) {
  if (isRunAndroid) {
    const apkPath = path.join(__dirname, "..", "android/app/build/outputs/apk/release", "app-release-signed.apk");
    const filePathOfNewApk = path.join(__dirname, "..", "app-release-signed.apk");
    await fs.promises.copyFile(apkPath, filePathOfNewApk);
    console.log("Copied apk to new location.");
    console.log(filePathOfNewApk);
  }
}

async function addPlatformSupport(isRunAndroid, androidSdkRootPath) {
  await execa.command(
    [
      `cap add`,
      `${isRunAndroid ? 'android' : 'ios'}`,
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      env: (isRunAndroid ? {
        "ANDROID_HOME": `${androidSdkRootPath}`,
      } : {
      }),
    }
  );
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

module.exports = main()