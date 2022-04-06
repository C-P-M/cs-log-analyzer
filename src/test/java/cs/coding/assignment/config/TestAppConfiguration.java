package cs.coding.assignment.config;

import cs.coding.assignment.dao.LogEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

@TestConfiguration
@EnableAsync
public class TestAppConfiguration {

    @Bean
    @Primary
    public Executor testAsyncLogEventPoller() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("lineConsumerTEST-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    @Primary
    public BlockingQueue<LogEvent> testQueueForLogLines() {
        return new ArrayBlockingQueue<>(1000);
    }

    @Bean
    @Primary
    public Connection testHsqlDbConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:assignmentdb", "SA", "");
    }


}
