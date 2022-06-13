package com.example.dbbackuppcloud.service;

import com.example.dbbackuppcloud.entity.Source;
import com.example.dbbackuppcloud.repository.SourceRepository;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.DataSource;
import com.pcloud.sdk.RemoteFile;
import com.smattme.MysqlExportService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class BackupService {
    private PCloudAPIAuth pCloudAPIAuth;
    private PCloudAPIClientFactory pCloudAPIClientFactory;
    private SourceRepository sourceRepository;

    @SneakyThrows
    @Scheduled(cron = "0 * * * * *")
    public void run() {
        if (pCloudAPIAuth.isAuthorized()) {
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Flux.fromIterable(sourceRepository.findAll())
                    .filter(source -> Objects.nonNull(source.getCron()))
                    .filter(source -> {
                        var expression = CronExpression.parse(source.getCron());
                        var next = expression.next(LocalDateTime.now().minus(10, ChronoUnit.SECONDS));
                        log.info("Now: %s == Next: %s".formatted(now, next));
                        return now.equals(next);
                    })
                    .flatMap(this::backupAsync)
                    .flatMap(this::uploadToCloudAsync)
                    .flatMap(uploadedFile -> Mono.just("Uploaded file size: %s".formatted(uploadedFile.size())))
                    .log()
                    .blockLast();
        } else {
            if (!pCloudAPIAuth.isAuthorizationBegun()) {
                pCloudAPIAuth.startAuth();
            }
        }
    }

    @SneakyThrows
    public Mono<RemoteFile> uploadToCloudAsync(File file) {
        return Mono.fromSupplier(() -> uploadToCloud(file));
    }

    @SneakyThrows
    public RemoteFile uploadToCloud(File file) {
        var apiClient = pCloudAPIClientFactory.getClient(pCloudAPIAuth.getAccessToken());
        var remoteFile = apiClient.createFile(
                3008030246L,
                file.getName(),
                DataSource.create(file)
        ).execute();

        file.delete();

        return remoteFile;
    }

    @SneakyThrows
    public Mono<File> backupAsync(Source db) {
        return Mono.fromSupplier(() -> backup(db));
    }

    @SneakyThrows
    public File backup(Source db) {
        Properties properties = new Properties();
        properties.setProperty(MysqlExportService.DB_NAME, db.getName());
        properties.setProperty(MysqlExportService.DB_USERNAME, db.getUsername());
        properties.setProperty(MysqlExportService.DB_PASSWORD, db.getPassword());
        properties.setProperty(MysqlExportService.JDBC_DRIVER_NAME, db.getJdbcDriverName());
        properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, db.getJDBCConnectionString());

        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_SQL_FILE, "false");
        properties.setProperty(MysqlExportService.TEMP_DIR, new File("temp").getPath());

        MysqlExportService mysqlExportService = new MysqlExportService(properties);
        mysqlExportService.clearTempFiles();
        mysqlExportService.export();
        File file = mysqlExportService.getGeneratedZipFile();
        file.deleteOnExit();
        return file;
    }
}
