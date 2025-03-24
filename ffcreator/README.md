# Getting Started

This project was bootstrapped with [NestJS](https://nestjs.com). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org install nodejs v68.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Generate a video with the default given environment variables as arguments<br/>

### `npm test`

Run all unit tests.<br/>
See the section about [running tests](https://jestjs.io) for more information.<br/>

###  `npm run test:open`

Launches the test runner in the interactive watch mode.<br/>
See the section about [running tests](https://jestjs.io) for more information.<br/>

### `npm run production:install`

Pre-install dependencies for production environments<br/>

###  `npm run production:run`

Generate video with given given environment variables as arguments<br/>

FFCREATOR_CACHE_DIRECTORY:  The folder where the cache files are stored when generating the video. For example: /c/cache<Br/><br/>
FFCREATOR_OUTPUT_VIDEO_FILE_PATH:  The path to the generated video file, it must be in mp4 format. For example: /c/video.mp4<br/>

Pre-step, please run<br/>

    npm run production:install 

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
execa <br/>
linq <br/>
eslint<br/>
typescript<br/>

# Demonstration of the animation effect of the video

Transition:
1. 12 transitions [https://tnfe.github.io/FFCreator/#/guide/animate](https://tnfe.github.io/FFCreator/#/guide/animate)
2. 67 transitions [https://gl-transitions.com/gallery](https://gl-transitions.com/gallery)

Animate:
1. https://createjs.com/demos/tweenjs/tween_sparktable

Effect:
1. https://animate.style

## Learn More

1. ffcreator (https://tnfe.github.io/FFCreator)<br/>
