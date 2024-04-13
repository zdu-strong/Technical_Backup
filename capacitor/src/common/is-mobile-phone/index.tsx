import isMobile from 'is-mobile'

export const isMobilePhone = isMobile({
  tablet: true,
  featureDetect: true,
});