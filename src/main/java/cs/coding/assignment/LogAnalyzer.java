package cs.coding.assignment;

import cs.coding.assignment.services.DatabaseService;
import cs.coding.assignment.services.FileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.concurrent.Executor;

@SpringBootApplication
public class LogAnalyzer implements ApplicationRunner {

	 final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		SpringApplication.run(LogAnalyzer.class, args);
	}

	@Autowired
	FileProcessor fileProcessor;

	@Autowired
	DatabaseService databaseService;

	@Autowired
	Executor asyncLogEventPoller;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		// Initializing the database - Create table (if not exist), else clean table for the run
		databaseService.initializeDatabase();

		// Read file line by line, generate logEvents & publish to Queue for persistence.
		fileProcessor.readFileLineByLine();

		// Check for completion, if tasks are not queued, fair chances that it is last batch of Threads executing
		do {

			logger.info("STATUS - Consumer {}..", ((ThreadPoolTaskExecutor)asyncLogEventPoller).getThreadPoolExecutor());
			logger.info("STATUS - File processing {}. {} lines read, {} lines parsed, {} lines error-ed while parsing, {} events published for persistence",
					fileProcessor.isProcessingCompleted() ? "Completed." : "In Progress..",
					fileProcessor.getLineProcessingStats()[0],
					fileProcessor.getLineProcessingStats()[1],
					fileProcessor.getLineProcessingStats()[2],
					fileProcessor.getLineProcessingStats()[3]
			);

		} while (!fileProcessor.isProcessingCompleted());

		logger.info("Shutting down the Consumer process, await 5 seconds for rest of the container");
		((ThreadPoolTaskExecutor) asyncLogEventPoller).setAwaitTerminationSeconds(5);
		((ThreadPoolTaskExecutor) asyncLogEventPoller).shutdown();

		databaseService.getLogEvents();
	}
}
