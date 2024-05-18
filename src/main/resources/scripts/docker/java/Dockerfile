FROM openjdk:23-ea-17-jdk-bookworm
COPY . /usr/src/test
WORKDIR /usr/src/test
RUN apt-get update
RUN apt-get install time
RUN chmod +x /usr/src/test/compile.sh
RUN chmod +x /usr/src/test/run.sh