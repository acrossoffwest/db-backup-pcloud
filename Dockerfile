FROM openjdk:17-jdk-alpine

ENV APP_NAME ""
ENV APP_PORT ""

ENV DB_NAME ""
ENV DB_USERNAME ""
ENV DB_PASSWORD ""
ENV DB_HOST ""
ENV DB_PORT ""

ENV PCLOUD_OAUTH_CLIENT_ID ""
ENV PCLOUD_OAUTH_CALLBACK_URL ""

MAINTAINER Iurii Karpov <acrossoffwest@gmail.com>

RUN mkdir /app
WORKDIR /app
COPY ./ /app 

RUN mkdir /backuper
WORKDIR /backuper

RUN apk update && apk upgrade && apk add --no-cache bash git openssh

RUN git clone https://github.com/acrossoffwest/mysql-backup4j.git ./
RUN git checkout write-batch-inserts-into-file
COPY ./.mvn /backuper/.mvn
RUN /app/mvnw -N io.takari:maven:wrapper
RUN /app/mvnw clean install -DskipTests -Dgpg.skip=true

WORKDIR /app

RUN ./mvnw -DskipTests package

ARG JAR_FILE=target/*.jar
RUN cp ${JAR_FILE} entry.jar

RUN ENVS=$(printenv | sed 's/\(^[^=]*\)=\(.*\)/export \1="\2"/')

CMD $ENVS java -Xmx10024m -Xms256m -jar /app/entry.jar