FROM rockylinux:9.3.20231119 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

FROM first_docker as second_docker

# install cargo
RUN dnf install -y cargo

# cargo build
COPY . /all_code
WORKDIR /all_code
RUN cargo build --release --quiet

# cargo run
FROM first_docker
COPY --from=second_docker /all_code/target/release/my /my
RUN /my

# start server
ENTRYPOINT ["/bin/bash"]
