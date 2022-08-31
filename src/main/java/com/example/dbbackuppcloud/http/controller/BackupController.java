package com.example.dbbackuppcloud.http.controller;

import com.example.dbbackuppcloud.dto.SourceDTO;
import com.example.dbbackuppcloud.entity.Source;
import com.example.dbbackuppcloud.service.BackupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class BackupController {
    private BackupService backupService;

    @PostMapping("/backup")
    public Mono<String> parseToken(@RequestBody SourceDTO sourceDTO) {
        var source = Source.builder()
                .host(sourceDTO.getHost())
                .port(sourceDTO.getPort())
                .name(sourceDTO.getName())
                .code(sourceDTO.getCode())
                .username(sourceDTO.getUsername())
                .password(sourceDTO.getPassword())
                .type(sourceDTO.getType())
                .jdbcDriverName(sourceDTO.getJdbcDriverName())
                .build();

        return backupService
                .makeBackupsFlux(List.of(source), true)
                .then(Mono.fromCallable(() -> "OK"));
    }
}
