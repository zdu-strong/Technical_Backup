export default {
  NotFoundText: () => cy.xpath(`//*[text()='Not Found']`),
  ReturnToHomeButton: () => cy.xpath(`//a[text()='Back home']`),
}