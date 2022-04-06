package cs.coding.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.assertj.core.api.Assertions.assertThat;

import cs.coding.assignment.services.FileProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"file.to.process=src/test/java/cs/coding/assignment/services/CorrespondingLines.txt"})
class LogAnalyzerTests {

	@Autowired
	FileProcessor fileProcessor;

	@Test
	void contextLoad_with_input_file(ApplicationContext applicationContext) {
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void event_published_properly() {
		String[] linesToParse = new String[] {
				"{\"id\":\"scsmbstgrb\", \"state\":\"STARTED\", \"timestamp\":1491377495213}",
				"{\"id\":\"scsmbstgrb\", \"state\":\"FINISHED\", \"timestamp\":1491377495218}"
		};

		assertEquals(1, fileProcessor.getLineProcessingStats()[3]);
	}

}
