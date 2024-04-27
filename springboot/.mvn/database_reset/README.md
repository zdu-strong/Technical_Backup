# Getting Started

Projects created by start.spring.io contain Spring Boot, a framework that makes Spring ready to work inside your app, but without much code or configuration required. Spring Boot is the quickest and most popular way to start Spring projects. If you have any questions, please contact zdu.strong@gmail.com.

## Development environment setup
1. From https://adoptium.net install java v21, and choose Entire feature.<br/>
2. From https://code.visualstudio.com install Visual Studio Code. Next, install extension "Extension Pack for Java Auto Config".<br/>

## Available Scripts

In the project directory, you can run:

### `./mvn clean package`

To generate executable jar package

### `./mvn versions:display-dependency-updates`

Check that a new version of the dependency is available<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>

    <dependency>
        <groupId>com.fasterxml.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-xml</artifactId>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-yaml</artifactId>
    </dependency>

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>

    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5</artifactId>
    </dependency>