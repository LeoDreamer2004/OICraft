FROM python:3.10
COPY . /usr/src/test
WORKDIR /usr/src/test
RUN apt-get update
RUN apt-get install time
RUN chmod +x /usr/src/test/compile.sh
RUN chmod +x /usr/src/test/run.sh
RUN apt-get install bc