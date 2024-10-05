FROM rockylinux:9.3.20231119 AS first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en glibc-langpack-en
ENV LANG="en_US.UTF-8"

FROM first_docker AS second_docker

# install git
RUN dnf install -y git

# install cargo
RUN dnf install -y cargo

# cargo build
COPY . /all_code
WORKDIR /all_code/rust
RUN cargo build --release

# cargo run
FROM first_docker
COPY --from=second_docker /all_code/rust/target/release/my /my
RUN /my

# start server
ENTRYPOINT ["/bin/bash"]
