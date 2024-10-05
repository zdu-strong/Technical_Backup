export default {
  CurrentPower: () => cy.xpath(`//*[contains(text(),'current battery')]`),
  EnterGame: () => cy.xpath(`//button[contains(., 'Enter the game')]`),
  Game: () => cy.xpath(`//canvas`),
  Setting: () => cy.xpath(`//*[@data-icon='gear']/..`),
  EndTheGameButton: () => cy.xpath(`//button[contains(., 'End Game')]`),
}