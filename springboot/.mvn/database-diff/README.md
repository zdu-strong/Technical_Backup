# Getting Started

Projects created by start.spring.io contain Spring Boot, a framework that makes Spring ready to work inside your app, but without much code or configuration required. Spring Boot is the quickest and most popular way to start Spring projects. If you have any questions, please contact zdu.strong@gmail.com.

## Development environment setup
1. From https://adoptium.net install java v21, and choose Entire feature.<br/>
2. From https://www.jetbrains.com/idea install IntelliJ IDEA.<br/>
   Next, install lombok plugin(File - Settings - Plugins).<br/>
   Next, choose Bundled Maven(File - Settings - Build - Build Tools - Maven).<br/>
   Next, choose java 21(File - Project Structure - Project - SDK).<br/>

## Available Scripts

In the project directory, you can run:

### `./mvn clean package`

To generate executable jar package

### `./mvn versions:update-properties versions:update-parent`

Check that a new version of the dependency is available<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>
