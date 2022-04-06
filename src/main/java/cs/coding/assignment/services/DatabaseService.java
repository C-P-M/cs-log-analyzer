package cs.coding.assignment.services;

import com.google.gson.Gson;
import cs.coding.assignment.dao.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DatabaseService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Connection hsqlDbConnection;

    private final String queryCreateTable = "create table LogEvents (id  varchar(20) primary key, " +
            "duration int, alert boolean, type varchar(20), host varchar(20) );";

    private final String queryInsertLogEvent = "insert into LogEvents (id, duration, alert, type, host) values (?, ?, ?, ?, ?);";

    private final String queryClearLogEvent = "Delete from LogEvents;";

    private final Gson gson = new Gson();

    public void initializeDatabase() throws SQLException {

        DatabaseMetaData databaseMetaData = hsqlDbConnection.getMetaData();

        try(ResultSet tables = databaseMetaData.getTables(null, null, "LOGEVENTS", new String[] {"TABLE"});) {
            if (tables.next()) {
                clearEvents();
                return;
            }
        }

        logger.info("Creating LogEvent Table...");
        try (Statement hsqlStatement = hsqlDbConnection.createStatement();) {

            hsqlStatement.execute(queryCreateTable);
        }
    }

    public void clearEvents() throws SQLException {

        logger.info("Clearing LogEvent Table before new run");
        try (Statement hsqlStatement = hsqlDbConnection.createStatement();) {

            hsqlStatement.execute(queryClearLogEvent);
        }
    }

    public void getLogEvents() {
        logger.info("Log-Events persisted in database table...");
        try (Statement hsqlStatement = hsqlDbConnection.createStatement();
             ResultSet rs = hsqlStatement.executeQuery("SELECT * from LogEvents");) {
                int i = 1;
                while(rs.next()) {
                    logger.info("id - {}, duration - {}, alert - {}, type - {}, host - {}",
                            rs.getString("id"),
                            rs.getInt("duration"),
                            rs.getBoolean("alert"),
                            rs.getString("type"),
                            rs.getString("host"));
                    i++;
                }
        } catch (SQLException e) {
            logger.error("Exception with statement", e);
        }
    }

    public void insertLogEvents(LogEvent logEvent) {
        logger.info("Adding LogEvent in Table");
        try (PreparedStatement hsqlStatement = hsqlDbConnection.prepareStatement(queryInsertLogEvent);) {

            try {
                hsqlStatement.setString(1, logEvent.getId());
                hsqlStatement.setLong(2, logEvent.getDuration());
                hsqlStatement.setBoolean(3, logEvent.getDuration() > 4);
                hsqlStatement.setString(4, logEvent.getType());
                hsqlStatement.setString(5, logEvent.getHost());
                hsqlStatement.executeUpdate();
            } catch (SQLException e) {
                logger.error("Exception while inserting event {} into database", logEvent, e);
            }
        } catch (SQLException e) {
            logger.error("Exception with statement while inserting event {} into database", logEvent, e);
        }
    }
}