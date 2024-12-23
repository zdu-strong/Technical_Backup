FROM rockylinux/rockylinux:9.5.20241118 AS first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en glibc-langpack-en
ENV LANG="en_US.UTF-8"

# install nodejs
RUN dnf module install -y nodejs:22

# install serve
RUN npm install --location=global serve

# run test
FROM first_docker AS second_docker
RUN dnf install -y git
RUN dnf install -y java-21-openjdk-devel
ENV JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
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

# compile code
WORKDIR /all_code/react
RUN npm run build

# copy build folder
FROM first_docker
COPY --from=second_docker /all_code/react/build /build

EXPOSE 443

# start server
ENTRYPOINT node build/support_react_app_environment_variables.js && serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=443 build