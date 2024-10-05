const fs = require('fs')
const path = require('path')
const { execSync } = require('child_process')

async function main() {
  await deletePackageLockFile();
  await deleteBuildFolder();
  await installDependencies();
  await eslint()
  process.exit()
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
  const folderPath = path.join(__dirname, "..", "build");
  await fs.promises.rm(folderPath, { recursive: true, force: true });
}

async function eslint() {
  execSync("eslint cypress", {
    stdio: "inherit",
    cwd: path.join(__dirname, "..")
  })
}

module.exports = main()