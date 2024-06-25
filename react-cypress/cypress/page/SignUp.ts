export default {
  nickname: () => cy.xpath("//label[.='nickname']/../div/input"),
  nextStepButton: () => cy.xpath("//button[.='Next']"),
  password: () => cy.xpath(`//textarea[@rows='6']`),
  addEmailOrPhoneNumber: () => cy.xpath(`//button[@aria-label='add']`),
  email: () => cy.xpath(`//label[.='Email']/../div/input`),
  sendVerificationCodeButton: () => cy.xpath(`//button[.='Send']`),
  verificationCodeInput: () => cy.xpath(`//label[.='Verification code']/../div/input`),
  signUpButton: () => cy.xpath(`//button[.='Sign Up']`)
}