FROM rockylinux:9.3.20231119 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

# install nodejs
RUN dnf module install -y nodejs:20

# install serve
RUN npm install --location=global serve

# install wait-on
RUN npm install --location=global wait-on

# install concurrently
RUN npm install --location=global concurrently

# install typescript
RUN npm install --location=global typescript

# install ts-node
RUN npm install --location=global ts-node

# Install java
RUN dnf install -y java-21-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java-21-openjdk

# compile server code
FROM first_docker as second_docker
RUN dnf install -y git
COPY . /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
RUN ./mvn clean package
RUN mv target/*.jar target/server.jar

# compile client code
FROM first_docker as three_docker
RUN dnf install -y git
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel nss libXScrnSaver alsa-lib
COPY . /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
WORKDIR /all_code/react-cypress
RUN rm -rf ../react/node_modules
RUN rm -rf ./node_modules
RUN npm test
WORKDIR /all_code/react
RUN npm run build

FROM first_docker
# copy jar
COPY --from=second_docker /all_code/springboot/target/server.jar /server.jar
# copy build folder
COPY --from=three_docker /all_code/react/build /build

# set env
ENV SERVER_SHUTDOWN GRACEFUL
ENV SPRING_DATASOURCE_URL 'jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME org.h2.Driver
ENV SPRING_JPA_HIBERNATE_DDL_AUTO update
ENV SPRING_JPA_DATABASE_PLATFORM com.springboot.project.common.database.CustomH2Dialect
ENV SPRING_LIQUIBASE_ENABLED false
ENV PROPERTIES_STORAGE_ROOT_PATH 'defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a'

# 8080 is server port, Since the server port is configured as 8080 in the client, the server port must be 8080 during runtime
EXPOSE 8080

# 443 is client port
EXPOSE 443

# start server
ENTRYPOINT ["concurrently", "wait-on http://localhost:8080 && ts-node --skipProject build/support_react_app_environment_variables.ts && serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=443 build", "java -jar server.jar"]
