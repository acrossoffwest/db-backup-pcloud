--liquibase formatted sql

--changeset aow:1
ALTER TABLE source ADD COLUMN cron VARCHAR(255);