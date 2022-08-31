package com.example.dbbackuppcloud.dto;

import lombok.Data;

@Data
public class SourceDTO {
    private Long id;
    private String jdbcDriverName = "com.mysql.jdbc.Driver";
    private String host;
    private int port;
    private String name;
    private String type = "mysql";
    private String username;
    private String password;
    private String code;
    private String cron;
}
