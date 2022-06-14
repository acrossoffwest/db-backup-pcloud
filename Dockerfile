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

RUN ./mvnw -DskipTests package

ARG JAR_FILE=target/*.jar
RUN cp ${JAR_FILE} entry.jar

RUN ENVS=$(printenv | sed 's/\(^[^=]*\)=\(.*\)/export \1="\2"/')

CMD $ENVS java -jar /app/entry.jar