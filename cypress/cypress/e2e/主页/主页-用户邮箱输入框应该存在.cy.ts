import page from '../../page'

it('', () => {
    page.HomePage.用户邮箱输入框().should("exist")
})

before(() => {
    cy.visit("/")
})