package cs.coding.assignment.services;

import cs.coding.assignment.config.TestAppConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {"file.to.process=src/test/java/cs/coding/assignment/services/singleLine.txt"})
@Import(TestAppConfiguration.class)
class FileProcessorTest {

    @Autowired
    FileProcessor fileProcessor;

    @Autowired
    DataConsumer dataConsumer;

    @Test
    void not_able_to_parse_line() {
        String lineNotToBeParsed = "{\"TEST\": \"line\"}";
        fileProcessor.processLine(lineNotToBeParsed);

        assertEquals(1, fileProcessor.getLineProcessingStats()[2]);
    }

    @Test
    void parse_single_line_properly() {

        assertEquals(1, fileProcessor.getLineProcessingStats()[1]);
    }
}