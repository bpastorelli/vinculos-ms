FROM ubuntu:latest
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y tzdata

FROM openjdk:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
MAINTAINER Bruno Pastorelli
COPY target/vinculos-ms.jar vinculos-ms.jar
ENTRYPOINT ["java","-jar","/vinculos-ms.jar"]
EXPOSE 9095