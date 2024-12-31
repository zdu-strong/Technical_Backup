export default {
  userDetailButton: (username: string) => cy.xpath(`//div[contains(., "${username}")][@role='gridcell']/..//button`),
  closeUserDetailDialogButton: () => cy.xpath(`//h2/div[contains(., 'User Detail')]/../button`),
}