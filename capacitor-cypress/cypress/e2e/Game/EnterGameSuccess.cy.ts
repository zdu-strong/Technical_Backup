import page from '../../page'
import * as action from '../../action'

it('', () => {
  page.HomePage.EnterGame().click()
  action.setPhoneLandscapeViewport()
  page.HomePage.Game().should("be.visible")
})

before(() => {
  action.setPhonePortraitViewport()
  cy.visit("/")
})