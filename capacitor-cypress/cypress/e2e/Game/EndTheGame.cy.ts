import page from '../../page'
import * as action from '../../action'

it('', () => {
  page.HomePage.Setting().click()
  page.HomePage.EndTheGameButton().click()
  action.setPhonePortraitViewport()
})

before(() => {
  action.setPhonePortraitViewport()
  cy.visit("/")
  page.HomePage.EnterGame().click()
  action.setPhoneLandscapeViewport()
  page.HomePage.Game().should("be.visible")
})