const fs = require('fs')
const path = require('path')
const { execSync } = require('child_process')

async function main() {
  await deletePackageLockFile();
  await deleteBuildFolder();
  await deleteDistFolder();
  await deleteOutputFolder();
  await installDependencies();
  await copyExeToAppDataForCache();
  await rebuildDependenciesOfElectron();
  await compileCode();
  process.exit();
}

async function installDependencies() {
  execSync(
    [
      "npm install",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      env: {
        ...process.env,
        "npm_config_package_lock": "false",
      },
    }
  );
}

async function deletePackageLockFile() {
  const filePathOfPackageLockFile = path.join(__dirname, "..", "package-lock.json");
  await fs.promises.rm(filePathOfPackageLockFile, { recursive: true, force: true });
}

async function deleteBuildFolder() {
  const folderPathOfBuild = path.join(__dirname, "..", "build");
  await fs.promises.rm(folderPathOfBuild, { recursive: true, force: true });
}

async function deleteOutputFolder() {
  const folderPathOfOutput = path.join(__dirname, "..", "output");
  await fs.promises.rm(folderPathOfOutput, { recursive: true, force: true });
}

async function deleteDistFolder() {
  const folderPathOfDist = path.join(__dirname, "..", "dist");
  await fs.promises.rm(folderPathOfDist, { recursive: true, force: true });
}

async function rebuildDependenciesOfElectron() {
  execSync(
    [
      "electron-builder install-app-deps"
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      env: {
        ...process.env,
        "npm_config_package_lock": "false",
      },
    }
  );
}

async function compileCode() {
  execSync(
    [
      `eslint main/**/*.tsx --config electron.eslintrc.js`
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
  execSync(
    [
      "nest build --path tsconfig.electron.main.json",
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

async function copyExeToAppDataForCache() {
  execSync(
    [
      "node bin/copy-exe-to-app-data-for-cache.js"
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
    }
  );
}

module.exports = main()