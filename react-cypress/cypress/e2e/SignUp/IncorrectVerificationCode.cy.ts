import { v6 } from 'uuid'
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
  page.SignUp.verificationCodeInput().clear().type("1234567")
  page.SignUp.nextStepButton().click()
  page.SignUp.signUpButton().click()
  page.SignUp.incorrectVerificationCodeDialog().should("exist")
})

before(() => {
  cy.visit("/sign-up")
})

const email = `${v6()}zdu.strong@gmail.com`