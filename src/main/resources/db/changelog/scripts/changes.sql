--liquibase formatted sql

--changeset aow:1
CREATE TABLE `source`(
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    host VARCHAR(500) NOT NULL,
    port INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(70) NOT NULL,
    username VARCHAR(150) NOT NULL,
    password VARCHAR(500) NOT NULL,
    jdbc_driver_name VARCHAR(120) NOT NULL,
    code VARCHAR(120) NOT NULL UNIQUE
);