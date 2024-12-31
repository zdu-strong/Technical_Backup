import page from '../../page'

it('', () => {
  page.SuperAdminUserManage.userDetailButton(username).click()
  page.SuperAdminUserManage.closeUserDetailDialogButton().click()
})

before(() => {
  cy.visit("/sign_in")
  page.SignIn.username().clear().type(email)
  page.SignIn.showPasswordButton().click()
  page.SignIn.password().clear().type(password)
  page.SignIn.signInButton().click()
  cy.location('pathname', { timeout: 180000 }).should('equal', '/')
  cy.visit("/super_admin/user/manage")
})

const username = "SuperAdmin"
const email = `zdu.strong@gmail.com`
const password = 'zdu.strong@gmail.com'