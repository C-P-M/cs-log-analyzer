package cs.coding.assignment.config;

import cs.coding.assignment.dao.LogEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AppConfiguration {

    @Bean
    public BlockingQueue<LogEvent> queueForLogLines() {
        return new ArrayBlockingQueue<>(1000);
    }

    @Bean
    public Executor asyncLogEventPoller() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("lineConsumer-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    public Connection hsqlDbConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:hsqldb/assignmentdb", "SA", "");
    }
}
