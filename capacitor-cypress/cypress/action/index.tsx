export function setPhonePortraitViewport() {
  cy.viewport("iphone-xr", "portrait")
}

export function setPhoneLandscapeViewport() {
  cy.viewport("iphone-xr", "landscape")
}