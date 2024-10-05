import { defineConfig } from "cypress";

export default defineConfig({
  redirectionLimit: -1,
  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
  defaultCommandTimeout: 10000,
});
