FROM rockylinux:8.6.20227707 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# dnf源使用阿里云镜像
RUN sed -e 's|^mirrorlist=|#mirrorlist=|g' \
    -e 's|^#baseurl=http://dl.rockylinux.org/$contentdir|baseurl=https://mirrors.aliyun.com/rockylinux|g' \
    -i.bak \
    /etc/yum.repos.d/Rocky-*.repo
RUN dnf makecache


# support utf-8
RUN dnf install -y langpacks-en
RUN dnf install -y glibc-common
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

# Install java
RUN dnf install -y java-11-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk
ENV PATH $PATH:$JAVA_HOME/bin

# compile code
FROM first_docker as second_docker
RUN dnf install -y git
COPY ./ /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
RUN ./mvn clean package --define database.mysql.jdbc.url=host.docker.internal:3306
RUN mv target/*.jar target/server.jar

# copy jar
FROM first_docker
COPY --from=second_docker /all_code/springboot/target/server.jar /server.jar
ENV SERVER_SHUTDOWN GRACEFUL
ENV SPRING_DATASOURCE_URL jdbc:h2:mem:wiki_test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&createDatabaseIfNotExist=true;MODE=MYSQL;DB_CLOSE_DELAY=-1
ENV SPRING_DATASOURCE_USERNAME sa
ENV SPRING_DATASOURCE_PASSWORD sa
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME org.h2.Driver
ENV SPRING_JPA_HIBERNATE_DDL_AUTO update
ENV SPRING_JPA_DATABASE_PLATFORM com.springboot.project.common.mysql.CustomH2Dialect
ENV SPRING_LIQUIBASE_ENABLED false

# 8080 is server port, Since the server port is configured as 8080 in the client, the server port must be 8080 during runtime
EXPOSE 8080

# start server
ENTRYPOINT ["java", "-jar", "server.jar"]
