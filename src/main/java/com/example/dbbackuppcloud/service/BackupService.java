package com.example.dbbackuppcloud.service;

import com.example.dbbackuppcloud.entity.Source;
import com.example.dbbackuppcloud.repository.SourceRepository;
import com.pcloud.sdk.DataSource;
import com.pcloud.sdk.RemoteFile;
import com.smattme.MysqlExportService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Properties;

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
            doBackups();
        } else {
            startAuthorization();
        }
    }

    private void doBackups() {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var localDateTimeForNext = LocalDateTime.now().minus(10, ChronoUnit.SECONDS);
        Flux.fromIterable(sourceRepository.findAll())
                .filter(source -> Objects.nonNull(source.getCron()))
                .filter(source -> {
                    var expression = CronExpression.parse(source.getCron());
                    var next = expression.next(localDateTimeForNext);
                    log.info("Now: %s == Next: %s".formatted(now, next));
                    return now.equals(next);
                })
                .flatMap(source -> Mono.zip(Mono.just(source), backupAsync(source)))
                .flatMap(tuple2 -> uploadToCloudAsync(tuple2.getT2(), tuple2.getT1()))
                .flatMap(uploadedFile -> Mono.just("Uploaded file size: %s".formatted(uploadedFile.size())))
                .log()
                .blockLast();
    }

    private void startAuthorization() {
        if (!pCloudAPIAuth.isAuthorizationBegun()) {
            pCloudAPIAuth.startAuth();
        }
    }

    @SneakyThrows
    public Mono<RemoteFile> uploadToCloudAsync(File file, Source source) {
        return Mono.fromSupplier(() -> uploadToCloud(file, source));
    }

    @SneakyThrows
    public RemoteFile uploadToCloud(File file, Source source) {
        log.info("[%s] Uploading to cloud".formatted(source.getCode()));
        var apiClient = pCloudAPIClientFactory.getClient(pCloudAPIAuth.getAccessToken());
        var remoteFile = apiClient.createFile(
                3008030246L,
                generateZipFilename(source),
                DataSource.create(file)
        ).execute();
        log.info("[%s] File uploaded to cloud".formatted(source.getCode()));
        Files.delete(file.toPath());

        return remoteFile;
    }

    private String generateZipFilename(Source source) {
        return "%s_%s.zip".formatted(getCurrentTimestamp(), source.getCode());
    }

    private String getCurrentTimestamp() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_s");
        return ldt.format(dtf);
    }

    @SneakyThrows
    public Mono<File> backupAsync(Source db) {
        return Mono.fromSupplier(() -> backup(db));
    }

    @SneakyThrows
    public File backup(Source db) {
        log.info("[%s] Start backuping".formatted(db.getCode()));
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
        log.info("[%s] Backup files generated".formatted(db.getCode()));
        file.deleteOnExit();
        return file;
    }
}
