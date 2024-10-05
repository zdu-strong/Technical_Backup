FROM rockylinux:9.3.20231119 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en glibc-langpack-en
ENV LANG="en_US.UTF-8"

# install nodejs
RUN dnf module install -y nodejs:20/minimal

# Install java
RUN dnf install -y java-17-openjdk-devel
ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk"

# run test
FROM first_docker AS second_docker
RUN dnf install -y git
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel nss libXScrnSaver alsa-lib
COPY . /all_code
WORKDIR /all_code/capacitor-cypress
RUN git add ..
RUN git reset --hard
RUN rm -rf ../capacitor/node_modules
RUN rm -rf ./node_modules
RUN npm test
WORKDIR /all_code/capacitor
WORKDIR /all_code
RUN rm -rf ./capacitor-cypress/node_modules
RUN rm -rf ./capacitor/node_modules

# copy all_code folder
FROM first_docker
COPY --from=second_docker /all_code/capacitor /capacitor
WORKDIR /capacitor

# start server
ENTRYPOINT ["/bin/bash"]
