package com.example.dbbackuppcloud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;

@SpringBootTest
class DbBackupPcloudApplicationTests {

    @Test
    void contextLoads() {
        var c = CronExpression.parse("");
        var s = c.next(Instant.now());
    }

}
