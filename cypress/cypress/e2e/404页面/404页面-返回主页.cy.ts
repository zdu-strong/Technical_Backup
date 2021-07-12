import page from '../../page'

it('', () => {
    page.NotFound.返回主页按钮().click()
    page.HomePage.用户邮箱输入框().should("exist")
})

before(() => {
    cy.visit("/404")
})