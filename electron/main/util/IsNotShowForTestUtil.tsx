export const isNotShowForTest = (process.env[
  "ELECTRON_IS_TEST_AND_NOT_SHOW"
] === "true") as boolean;
