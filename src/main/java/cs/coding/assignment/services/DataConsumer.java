package cs.coding.assignment.services;

import cs.coding.assignment.dao.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class DataConsumer {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DatabaseService databaseService;

    @Autowired
    BlockingQueue<LogEvent> queueForLogLines;

    @Autowired
    Executor asyncLogEventPoller;

    @Async
    public void consumeLogLines() {
        try {
            LogEvent logEvent = queueForLogLines.poll(1000, TimeUnit.MILLISECONDS);

            if(logEvent != null) {
                persistLogEvent(logEvent);
                logger.debug("Log Event persisted, pool status {}", ((ThreadPoolTaskExecutor) asyncLogEventPoller).getThreadPoolExecutor());
            } else {
                logger.debug("No Log Events available, exiting this Thread, pool status {}", ((ThreadPoolTaskExecutor) asyncLogEventPoller).getThreadPoolExecutor());
            }

        } catch (InterruptedException e) {
            logger.error("Queue Interrupted");
        }
    }

    public void persistLogEvent(LogEvent logEvent) {
        databaseService.insertLogEvents(logEvent);
    }
}
