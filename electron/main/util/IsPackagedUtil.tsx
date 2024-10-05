export const isPackaged = (() => {
  try {
    const { app } = require("electron");
    return app.isPackaged;
  } catch {
    const isPackagedOfApp: any = process.env["ELECTRON_IS_PACKAGED"];
    if (isPackagedOfApp === "true") {
      return true;
    } else if (isPackagedOfApp === "false") {
      return false;
    } else {
      throw new Error(
        "Does not support obtaining whether the program is packaged"
      );
    }
  }
})();
