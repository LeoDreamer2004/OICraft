FROM gcc:13.2
COPY . /usr/src/test
WORKDIR /usr/src/test
RUN chmod +x /usr/src/test/compile.sh
RUN chmod +x /usr/src/test/run.sh
RUN apt-get update
RUN apt-get install time
