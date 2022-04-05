## Getting Started

- Check out the project into IntelliJ
- uses java 11 
- LogAnalyzer is main class to run
- Provide environment property / VM option "file.to.process" to a valid location of file containing LogEvents  
  - #### -Dfile.to.process=/valid/path/to/file/logfile.txt

OR

- Check out the project into IntelliJ
- in project directory execute
  - #### "mvn clean package"
- in target folder a jar file will be created 
  - #### "log-analyzer-0.0.1-SNAPSHOT.jar"
- in project directory execute command 
  - #### java -Dfile.to.process=</path/to/log-events/file> -jar target/log-analyzer-0.0.1-SNAPSHOT.jar


##Could have done better:
 - Closing out Thread-Pool-Executor can be handled in better way i.e. depending on processing & consumption rate
 - A bit more test coverage
 - Database service with @Repository implementation, rather than direct queries / Spring data JPA
 - Another approach can be to split file and process each portion in separate threads 
    