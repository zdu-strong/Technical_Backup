const execa = require("execa")
const path = require("path")

async function main() {
  await startClient();
  process.exit();
}

async function startClient() {
  await execa.command(
    "react-app-rewired start",
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "GENERATE_SOURCEMAP": "false"
      }
    }
  );
}

module.exports = main()