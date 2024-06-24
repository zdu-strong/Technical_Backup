import page from '../page'

export function signUp(email: string, password: string) {
  page.SignUp.nickname().type('John Hancock')
  page.SignUp.nextStepButton().click()
  page.SignUp.password().type(password)
  page.SignUp.nextStepButton().click()
  page.SignUp.addEmailOrPhoneNumber().click()
  page.SignUp.email().type(email)
  page.SignUp.sendVerificationCodeButton().click()
  page.SignUp.verificationCodeInput().click()
  page.SignUp.verificationCodeInput().invoke("val").should("have.length.at.least", 6)
  page.SignUp.nextStepButton().click()
  page.SignUp.signUpButton().click()
  cy.location('pathname', { timeout: 300000 }).should('equal', "/")
}