FROM java:7

COPY . /usr/src
WORKDIR /usr/src
EXPOSE 3000
RUN LEIN_ROOT=true bin/lein ring uberjar
ENV PORT 3000
CMD java -jar target/dashportal-standalone.jar
