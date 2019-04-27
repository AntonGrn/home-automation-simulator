package mainPackage;

import java.sql.*;
import java.util.ArrayList;

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
        String ip = "localhost";    //"134.209.198.123"; //or localhost
        String port = "3306";
        String database = "homeAutoLAAS";
        String user = "userLAAS";
        String password = "detvarsomtusan";

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
                String accessLevel = resultSet.getString("accessLevel");

                items[0] = accountID;
                items[1] = systemID;
                items[2] = name;
                items[3] = accessLevel;
                items[4] = password;

                System.out.println("Database");

            }
            if(results != 1) { //If there was no match, or for some reason, multiple matches
                throw new Exception("Login failed. Invalid input");
            }

        } catch (SQLException e) {
            throw new Exception("Error on SQL query");
        } catch (NullPointerException e) {
            throw new Exception("Nullpointer Exception");
        } finally {
            closeConnection();
        }
        return items;
    }

}

