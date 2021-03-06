package com.navi.app.config;

import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;

@Configuration
public class LockConfig {
  @Value(("${subscriber.executor.lock.atmost.inMillis}"))
  private long subscriberExecutorLockAtmostInMillis;

  @Value(("${subscriber.executor.lock.atleast.inMillis}"))
  private long subscriberExecutorLockAtleastInMillis;

  @Bean
  public LockProvider lockProvider(DataSource dataSource) {
    return new JdbcTemplateLockProvider(
        JdbcTemplateLockProvider.Configuration.builder()
            .withJdbcTemplate(new JdbcTemplate(dataSource))
            .usingDbTime() // Works on Postgres, MySQL, MariaDb, MS SQL, Oracle, DB2, HSQL and H2
            .build()
    );
  }

  @Bean
  public DistributedLockExecutor getQueueCallbackExecutor(LockProvider lockProvider) {
    LockingTaskExecutor executor = new DefaultLockingTaskExecutor(lockProvider);
    Duration lockAtMostUntil = Duration.ofSeconds(subscriberExecutorLockAtmostInMillis);
    Duration lockAtLeast = Duration.ofMillis(subscriberExecutorLockAtleastInMillis);
    return (runnable, key) -> executor.executeWithLock(
        runnable,
        new LockConfiguration(Instant.now(), key, lockAtMostUntil, lockAtLeast));
  }
}
