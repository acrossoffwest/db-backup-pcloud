FROM openjdk:17-jdk-alpine

ENV DB_NAME ""
ENV DB_USERNAME ""
ENV DB_PASSWORD ""
ENV DB_HOST ""
ENV DB_PORT ""

MAINTAINER Iurii Karpov <acrossoffwest@gmail.com>

RUN mkdir /app
WORKDIR /app
COPY ./ /app

RUN ./mvnw -DskipTests package

ARG JAR_FILE=target/*.jar
RUN cp ${JAR_FILE} entry.jar

CMD DB_NAME=${DB_NAME} DB_USERNAME=${DB_USERNAME} DB_PASSWORD=${DB_PASSWORD} DB_HOST=${DB_HOST} DB_PORT=${DB_PORT} java -jar /app/entry.jar