import page from '../../page'
import * as action from '../../action'

it('', () => {
  page.NotFound.NotFoundText().should("exist")
})

before(() => {
  action.setPhonePortraitViewport()
  cy.visit("/404")
})