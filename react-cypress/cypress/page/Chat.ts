export default {
  signOutButton: () => cy.xpath("//Button[.='Sign out']", { timeout: 180000 }),
  MessageContentInput: () => cy.xpath(`//fieldset[contains(., 'Message content')]/../textarea[@rows]`, { timeout: 180000 }),
  Message: (message: string) => cy.xpath(`//div[contains(@style, 'break-word')][contains(., '${message}')]`, { timeout: 180000 }),
  RecallMessageButton: (message: string) => cy.xpath(`//div[contains(@style, 'break-word')][contains(., '${message}')]/..//button[contains(., 'Withdrawn')]`, { timeout: 60000 }),
}