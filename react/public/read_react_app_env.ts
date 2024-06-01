import path from "path";
import fs from 'fs';
import process from "process";

async function main() {
  await writeReactAppEnvToJsFile();
  await deleteFileOfReadReactAppEnvJs();
}

async function writeReactAppEnvToJsFile() {
  const beforeText = getStringOfNodeEnvProduction();
  const afterText = getStringOfReactAPPEnv();
  if (beforeText === afterText) {
    return;
  }
  const jsFilePathList = await getStaticJsPathList();
  for (const filePathOfJs of jsFilePathList) {
    const textOfJsFile = await fs.promises.readFile(filePathOfJs, { encoding: "utf8" });
    if (!textOfJsFile.includes(beforeText)) {
      continue;
    }
    await fs.promises.writeFile(filePathOfJs, textOfJsFile.replace(getRegexOfNodeEnvProduction(), afterText), { encoding: "utf-8" });
  }
}

function getStringOfReactAPPEnv() {
  let text = "";
  for (const envName of Object.keys(process.env)) {
    if (!envName.startsWith("REACT_APP_")) {
      continue;
    }
    let envValue = process.env[envName];
    if (!envValue) {
      continue;
    }
    if (!envValue.startsWith("https://") && !envValue.startsWith("http://")) {
      continue;
    }
    if (envValue.includes(`"`)) {
      continue;
    }
    if (envValue.endsWith("/")) {
      envValue = envValue.slice(0, envValue.length - 1);
    }
    text += `${envName}:"${envValue}",`
  }
  return getStringOfNodeEnvProduction() + text;
}

async function getStaticJsPathList() {
  const list = await fs.promises.readdir(getFolderPathOfStaticJs(), "utf-8");
  return list.map(s => path.join(getFolderPathOfStaticJs(), s));
}

function getFolderPathOfStaticJs() {
  return path.join(__dirname, "static/js");
}

async function deleteFileOfReadReactAppEnvJs() {
  await fs.promises.rm(getFilePathOfReadReactAppEnvJs(), { recursive: true, force: true });
}

function getFilePathOfReadReactAppEnvJs() {
  return path.join(__dirname, "read_react_app_env.ts");
}

function getStringOfNodeEnvProduction() {
  return `NODE_ENV:"production",`;
}

function getRegexOfNodeEnvProduction() {
  return new RegExp(`NODE_ENV:"production",`, "g");
}

export default main();