import { v1 } from 'uuid'
import page from '../../page'

it('', () => {
  page.SignUp.nickname().type('John Hancock')
  page.SignUp.nextStepButton().click()
  page.SignUp.password().type('Hello, World!')
  page.SignUp.nextStepButton().click()
  page.SignUp.addEmailOrPhoneNumber().click()
  page.SignUp.email().type(email)
  page.SignUp.sendVerificationCodeButton().click()
  page.SignUp.verificationCodeInput().click()
  page.SignUp.verificationCodeInput().invoke("val").should("have.length.at.least", 6)
  page.SignUp.nextStepButton().click()
  page.SignUp.signUpButton().click()
  cy.location('pathname', { timeout: 180000 }).should('equal', "/")
})

before(() => {
  cy.visit("/sign-up")
})

const email = `${v1()}zdu.strong@gmail.com`