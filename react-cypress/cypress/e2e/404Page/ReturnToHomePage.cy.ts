import page from '../../page'

it('', () => {
  page.NotFound.ReturnToHomeButton().click()
  page.Chat.signOutButton().should('exist')
})

before(() => {
  cy.visit("/404")
})