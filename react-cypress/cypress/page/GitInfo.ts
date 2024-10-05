export default {
  FrondEndCommitIdInfo: () => cy.xpath(`//div[@id='FrontEndCommitIdInfo']`),
  BackendEndCommitIdInfo: () => cy.xpath(`//div[@id='BackendCommitIdInfo']`),
  FrondEndUpdateDateInfo: () => cy.xpath(`//div[@id='FrondEndUpdateDateInfo']`),
  BackendEndUpdateDateInfo: () => cy.xpath(`//div[@id='BackendUpdateDateInfo']`),
}