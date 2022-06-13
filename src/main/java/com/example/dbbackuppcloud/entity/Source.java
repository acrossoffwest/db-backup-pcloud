package com.example.dbbackuppcloud.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "source")
public class Source {
    @Id
    private Long id;

    private String jdbcDriverName = "com.mysql.jdbc.Driver";
    private String host;
    private int port;
    private String name;
    private String type = "mariadb";
    private String username;
    private String password;
    private String code;
    private String cron;

    public String getJDBCConnectionString() {
        return "jdbc:%s://%s:%d/%s".formatted(type, host, port, name);
    }
}
