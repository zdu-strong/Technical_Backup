import page from '../../page'
import * as action from '../../action'

it('', () => {
  page.NotFound.ReturnToHomeButton().click()
  page.HomePage.CurrentPower().should("exist")
})

before(() => {
  action.setPhonePortraitViewport()
  cy.visit("/404")
})