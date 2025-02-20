FROM rockylinux/rockylinux:9.5.20241118 AS first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en glibc-langpack-en
ENV LANG="en_US.UTF-8"

# Install java
RUN dnf install -y java-21-openjdk-devel

# compile code
FROM first_docker AS second_docker
RUN dnf install -y git
COPY . /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
RUN ./mvn clean package --define is.development.mock.mode=false
RUN mv target/*.jar target/server.jar

# copy jar
FROM first_docker
COPY --from=second_docker /all_code/springboot/target/server.jar /server.jar

ENV SERVER_SHUTDOWN="GRACEFUL"

# 8080 is server port, Since the server port is configured as 8080 in the client, the server port must be 8080 during runtime
EXPOSE 8080

# start server
ENTRYPOINT ["java", "-jar", "server.jar"]
