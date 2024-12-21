# Getting Started

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org install nodejs v22.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Runs the app in the development mode.

The app will reload if you make edits.<br/>
You will also see any lint errors in the console.

### `npm run pack`

only generates the package directory without really packaging it. This is useful for testing purposes.

### `npm run make`

to package in a distributable format (e.g. dmg, windows installer, deb package).

## Install new dependencies

    npm install react --save-dev

After installing new dependencies, please make sure that the project runs normally.<br/>
After installing new dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

## Upgrade dependency

You can use this command to check if a new version is available:<br/>

    npx -y -p npm-check npm-check --update

After upgrading the dependencies, please make sure that the project runs normally.<br/>
After upgrading the dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>
linq (Current project not support ES module)<br/>
@types/react<br/>
@types/react-dom<br/>
eslint<br/>
execa (Current project not support ES module)<br/>
get-port (Current project not support ES module)<br/>
node-polyfill-webpack-plugin<br/>
react<br/>
react-dom<br/>
react-intl<br/>
react-router-dom<br/>
typescript (Dependency incompatibility)<br/>

## Notes - Things to note

1. The dependencies used in the Electron main process need to be installed in dependencies, and other dependencies are installed in devDependencies. This can keep the application size to a minimum.

## Learn More

1. React UI framework (https://react.dev)<br/>
2. mobx-react-use-autorun (https://www.npmjs.com/package/mobx-react-use-autorun)
3. typestyle (https://www.npmjs.com/package/typestyle)<br/>
4. Material UI Components (https://material-ui.com)<br/>
5. Icons (https://fontawesome.com/search?o=r&m=free)<br/>
6. Electron (https://www.electronjs.org)<br/>
7. axios (https://axios-http.com)
8. typedjson (https://www.npmjs.com/package/typedjson)
9. linq (https://www.npmjs.com/package/linq)<br/>
10. UUID (https://www.npmjs.com/package/uuid)<br/>
11. tailwindcss(https://tailwindcss.com)<br/>
12. Animate.css(https://animate.style)<br/>
13. react-git-info (https://www.npmjs.com/package/react-git-info)
14. Game Engine (https://www.babylonjs.com)
15. mathjs (https://www.npmjs.com/package/mathjs)
16. electron-builder (https://www.electron.build)
