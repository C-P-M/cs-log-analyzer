package cs.coding.assignment.services;

import com.google.gson.Gson;
import cs.coding.assignment.config.AppConfiguration;
import cs.coding.assignment.dao.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileProcessor {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${file.to.process}")
    String absoluteFilePath;

    @Autowired
    BlockingQueue<LogEvent> queueForLogLines;

    @Autowired
    DataConsumer dataConsumer;

    private boolean processingCompleted;

    int linesRead, linesParsed, eventsPublished, linesWithParsingError = 0;

    private final Map<String, LogEvent> events = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();

    public void readFileLineByLine() {

        logger.info("Reading file - {}", absoluteFilePath);

        String lineToProcess = "";
        try (FileInputStream inputStream = new FileInputStream(absoluteFilePath);
            Scanner scanner = new Scanner(inputStream))
        {
            while(scanner.hasNextLine()) {
                lineToProcess = scanner.nextLine();
                linesRead++;
                processLine(lineToProcess);
            }
        } catch (IOException e) {
            logger.error("Line {} could not be processed.", lineToProcess);
        }

        processingCompleted = true;
    }

    public void processLine(String nextLine) {
        try {
            LogEvent currLogEvent = gson.fromJson(nextLine, LogEvent.class);

            if(currLogEvent.getId() == null) {
                linesWithParsingError++;
                return;
            }

            LogEvent existingEvent = events.remove(currLogEvent.getId());

            if (existingEvent != null) {
                if (currLogEvent.getState().equals("STARTED")) {
                    currLogEvent.setDuration(existingEvent.getTimestamp() - currLogEvent.getTimestamp());
                } else {
                    currLogEvent.setDuration(currLogEvent.getTimestamp() - existingEvent.getTimestamp());
                }
                linesParsed++;

                queueForLogLines.put(currLogEvent);
                eventsPublished++;

                //Start a consumer thread as well
                dataConsumer.consumeLogLines();

                logger.info("Event id - [{}] has been published for persistence", currLogEvent.getId());
            } else {
                logger.info("Corresponding Event for id [{}] is not yet available, parking it until then..", currLogEvent.getId());
                linesParsed++;
                events.put(currLogEvent.getId(), currLogEvent);
            }
        } catch (InterruptedException e) {
            linesWithParsingError++;
            logger.error("Interrupt ignoring", e);
        }
    }

    public boolean isProcessingCompleted() {
        return processingCompleted;
    }

    public int[] getLineProcessingStats() {
        return new int[] {linesRead, linesParsed, linesWithParsingError, eventsPublished};
    }
}