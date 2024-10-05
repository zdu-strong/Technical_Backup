import page from '../../page'

it('', () => {
  page.NotFound.NotFoundText().should("exist")
})

before(() => {
  cy.visit("/any_page")
})