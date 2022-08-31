package com.example.dbbackuppcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@Entity
@Table(name = "source")
@AllArgsConstructor
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

    public Source() {
        // TODO document why this constructor is empty
    }

    public String getJDBCConnectionString() {
        return "jdbc:%s://%s:%d/%s".formatted(type, host, port, name);
    }
}
