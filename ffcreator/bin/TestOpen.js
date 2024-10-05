const path = require('path')
const execa = require('execa')

async function main() {
  await jest();
  process.exit();
}

async function jest() {
  execa.commandSync(
    "jest --watch --config ./test/jest.json",
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
    }
  );
}

module.exports = main()