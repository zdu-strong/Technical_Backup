import { v1 } from 'uuid'
import page from '../../page'

it('', () => {
  page.SignIn.username().clear().type(email)
  page.SignIn.showPasswordButton().click()
  page.SignIn.password().clear().type(password)
  page.SignIn.signInButton().click()
  page.SignIn.incorrectPasswordOrUsernameDialog().should("exist")
})

before(() => {
  cy.visit("/sign_in")
})

const email = `${v1()}zdu.strong@gmail.com`
const password = 'Hello, World!'