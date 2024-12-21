FROM rockylinux/rockylinux:9.5.20241118 AS first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en glibc-langpack-en
ENV LANG="en_US.UTF-8"

# run test
FROM first_docker AS second_docker
RUN dnf module install -y nodejs:18/minimal
RUN dnf install -y git
RUN dnf install -y gcc-c++ cairo-devel pango-devel libjpeg-turbo-devel giflib
RUN dnf install -y mesa-dri-drivers Xvfb libXi-devel libXinerama-devel libX11-devel
COPY . /all_code
WORKDIR /all_code/ffcreator
RUN rm -rf ./node_modules
RUN git add ..
RUN git reset --hard
RUN xvfb-run npm test
RUN rm -rf ./node_modules

# copy ffcreator folder
FROM first_docker
COPY --from=second_docker /all_code/ffcreator /ffcreator
WORKDIR /ffcreator

# start server
ENTRYPOINT ["/bin/bash"]
