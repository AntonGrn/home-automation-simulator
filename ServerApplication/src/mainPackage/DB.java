package mainPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {

    private Statement statement;
    private Connection connection;
    private ResultSet resultSet;

    private static DB instance = null;

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    private DB() {
    }

    private void connect() {
        String ip = "localhost";
        String port = "XXXX";
        String database = "homeAutoLAAS";
        String user = "XXXXX";
        String password = "XXXXX";

        connection = null;
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?useSSL=false&user=" + user + "&password=" + password + "&serverTimezone=UTC";
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            closeConnection();
        }
    }

    private void closeConnection() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("Error on closing ResultSet");
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error on closing Statement");
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error on closing connection");
            }
        }
    }

    public String[] login(String accountID, String password) throws Exception {
        //catch or declare. We chose to declare so we can pass exception messages back to client application
        connect();
        String[] items = new String[5];
        int results = 0;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Account WHERE accountID = '" + accountID + "' AND password = '" + password + "';");
            //Note: If Query gives no result, the while(next) below won't run.
            while (resultSet.next()) {
                results++;
                //We already have accountID and password from the login() parameters.
                String systemID = resultSet.getString("systemID"); //Seems to work to take it as String, while it is an integer in MySQL
                String name = resultSet.getString("name");
                String admin = resultSet.getString("admin");

                items[0] = accountID;
                items[1] = systemID;
                items[2] = name;
                items[3] = admin;
                items[4] = password;

            }
            if(results != 1) { //If there was no match, or for some reason, multiple matches
                throw new Exception("Login failed. Connection is good");
            }

        } catch (SQLException e) {
            throw new Exception("Error on SQL query");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
        return items;
    }

    public ArrayList<String[]> getLogs(int systemID) throws Exception {
        connect();
        ArrayList<String[]> logs = new ArrayList<>();
        int results = 0;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Log WHERE systemID = '" + String.valueOf(systemID) + "';");
            while (resultSet.next()) {
                results++;
                String timestamp = resultSet.getString("timestamp");
                String log = resultSet.getString("log");

                String[] items = {timestamp, log};
                logs.add(items);
            }

            if(results < 1) {
                throw new Exception("No logs stored");
            }

        } catch (SQLException e) {
            throw new Exception("Error on SQL query");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
        return logs;
    }

    public void addLog(int systemID, String logMessage) throws Exception {
        connect();
        try {
            //Count the logs of the system
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM Log WHERE systemID = "+ String.valueOf(systemID) + ";");
            int systemLogs = resultSet.getInt(1);
            //Only store 10 logs for each system. If more; delete oldest
            if (systemLogs >= 10) {
                statement.executeUpdate("DELETE * FROM Log WHERE systemID = " +String.valueOf(systemID)+ " AND logID = (SELECT MIN(logID) FROM Log WHERE systemID = " +String.valueOf(systemID)+ ")");
            }
            //Add new log. Note: timestamp is added by default by MySQL
            statement.executeUpdate("INSERT INTO Log (`systemID`, `log`) VALUES ("+String.valueOf(systemID)+", '"+ logMessage +"');");

        } catch (SQLException e) {
            throw new Exception("Error on SQL query");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
    }
}

