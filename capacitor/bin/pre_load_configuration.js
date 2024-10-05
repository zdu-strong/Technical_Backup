const fs = require('fs')
const path = require('path')
const { execSync } = require('child_process')

async function main() {
  await deletePackageLockFile();
  await deleteBuildFolder();
  await deleteAndroidFolder();
  await deleteIOSFolder();
  await deleteAppDebugApk();
  await deleteAppSignedApk();
  await installDependencies();
  process.exit();
}

async function installDependencies() {
  execSync(
    [
      "npm install",
      "--package-lock=false",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

async function deleteAndroidFolder() {
  const folderPath = path.join(__dirname, "..", "android");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function deleteIOSFolder() {
  const folderPath = path.join(__dirname, "..", "ios");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function deletePackageLockFile() {
  const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
  await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
}

async function deleteBuildFolder() {
  const folderPath = path.join(__dirname, "..", "build");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function deleteAppDebugApk() {
  const filePathOfAppDebugApk = path.join(__dirname, "..", "app-debug.apk");
  await fs.promises.rm(filePathOfAppDebugApk, { recursive: true, force: true });
}

async function deleteAppSignedApk() {
  const filePathOfSignedApk = path.join(__dirname, "..", "app-release-signed.apk");
  await fs.promises.rm(filePathOfSignedApk, { recursive: true, force: true });
}

module.exports = main()