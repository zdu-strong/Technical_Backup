export default {
  username: () => cy.xpath("//legend/span[.='Account ID']/../../..//input", { timeout: 180000 }),
  password: () => cy.xpath(`//textarea[@rows='6']`),
  signInButton: () => cy.xpath(`//button[.='Sign In']`),
  showPasswordButton: () => cy.xpath(`//button[.='The password has been filled in, click Edit']`),
  incorrectPasswordOrUsernameDialog: () => cy.xpath(`//div[contains(@class, "MuiAlert-message")][contains(., "Incorrect username or password")]`, { timeout: 180000 }),
}