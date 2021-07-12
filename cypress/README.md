# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org/en/ install nodejs v16.<br/>
3. From https://adoptium.net install java v11, and choose Entire feature.

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Runs the app in the development mode.<br/>
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.<br/>

The page will reload if you make edits.<br/>
You will also see any lint errors in the console.<br/>

For now, it has a warning, you can adjust the browser's Custom levels to hide it.

### `npm test`

Run all unit tests.<br/>
See the section about [running tests](https://www.cypress.io) for more information.<br/>

## Install new dependencies

    npm install react --save-dev

After installing new dependencies, please make sure that the project runs normally.<br/>
After installing new dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

## Upgrade dependency

You can use this command to check if a new version is available:<br/>
npm install -g npm-check && npm-check --update<br/>

After upgrading the dependencies, please make sure that the project runs normally.<br/>
After upgrading the dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>
execa (Current project not support ES module)<br/>
get-port (Current project not support ES module)<br/>

## Learn More

1. Cypress (https://www.cypress.io)<br/>
