import page from '../../page'

it('', () => {
  page.GitInfo.FrondEndCommitIdInfo().then((element) => element.text()).should("have.length", 40)
  page.GitInfo.BackendEndCommitIdInfo().then((element) => element.text()).should("have.length", 40)
  page.GitInfo.FrondEndUpdateDateInfo().then((element) => element.text()).should("have.length", 16)
  page.GitInfo.BackendEndUpdateDateInfo().then((element) => element.text()).should("have.length", 16)
})

before(() => {
  cy.visit("/git")
})