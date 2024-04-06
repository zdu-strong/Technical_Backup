export const existsWindow = (() => {
  try {
    if (window) {
      return true;
    } else {
      return false;
    }
  } catch {
    return false;
  }
})();
