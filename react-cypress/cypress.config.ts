import { defineConfig } from "cypress";

export default defineConfig({
    redirectionLimit: -1,
    e2e: {
        setupNodeEvents(on, config) {
            // implement node event listeners here
        },
    },
    viewportHeight: 820,
    viewportWidth: 1180,
    defaultCommandTimeout: 10000,
    video: true,
});
