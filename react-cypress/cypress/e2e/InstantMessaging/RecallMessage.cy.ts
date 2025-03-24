import page from '../../page'
import { v6 } from 'uuid'
import * as action from '../../action'

it('', () => {
  page.Chat.RecallMessageButton(message).click()
  page.Chat.Message(message).should("not.exist")
})

before(() => {
  cy.visit("/sign-up")
  action.signUp(email, password)
  page.Chat.MessageContentInput().type(message).type("{enter}")
  page.Chat.Message(message).should("exist")
})

const message = `Hello, World! ${v6()}`
const email = `${v6()}zdu.strong@gmail.com`
const password = 'Hello, World!'
