import { v1 } from 'uuid'
import page from '../../page'
import * as action from '../../action'

it('', () => {
  page.SignIn.username().clear().type(email)
  page.SignIn.showPasswordButton().click()
  page.SignIn.password().clear().type(password)
  page.SignIn.signInButton().click()
  cy.location('pathname', { timeout: 180000 }).should('equal', '/')
})

before(() => {
  cy.visit("/sign_up")
  action.signUp(email, password)
  page.Chat.signOutButton().click()
  cy.location('pathname').should('equal', '/sign_in')
})

const email = `${v1()}zdu.strong@gmail.com`
const password = 'Hello, World!'