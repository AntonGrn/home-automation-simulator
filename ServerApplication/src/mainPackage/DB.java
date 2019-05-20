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
                System.out.println("Error on closing DB ResultSet");
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error on closing DB Statement");
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error on closing DB connection");
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
            if (results != 1) { //If there was no match, or for some reason, multiple matches
                throw new Exception("Login failed. Connection is good");
            }
        } catch (SQLException e) {
            throw new Exception("Error on SQL query. Code 1");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
        return items;
    }

    public void setGadgetState(int systemID, int gadgetID, int newState) throws Exception {
        connect();
        int result;
        try {
            result = statement.executeUpdate("UPDATE Gadget SET state= " + newState + " WHERE systemID = " + systemID + " AND gadgetID = " + gadgetID + ";");
            if (result != 1) {
                throw new Exception("Gadget update failed");
            }
        } catch (SQLException e) {
            throw new Exception("Error on SQL query. Code 2");
        } finally {
            closeConnection();
        }
    }

    public ArrayList<String[]> getGadgets(int systemID, boolean onlyGadgetStates) throws Exception {
        connect();
        ArrayList<String[]> gadgetList = new ArrayList<>();
        int results = 0;
        try {
            resultSet = statement.executeQuery("SELECT gadgetID, type, name, room, state, consumption FROM Gadget WHERE systemID = " + systemID + ";");
            while (resultSet.next()) {
                results++;
                String gadgetID = resultSet.getString("gadgetID");
                String type = resultSet.getString("type");
                String name = resultSet.getString("name");
                String room = resultSet.getString("room");
                String state = resultSet.getString("state");
                String consumption = resultSet.getString("consumption");

                if (onlyGadgetStates) {
                    String[] items = {type, gadgetID, state};
                    gadgetList.add(items);
                } else {
                    String[] items = {type, gadgetID, name, room, state, consumption};
                    gadgetList.add(items);
                }
            }
            /*if (results < 1) {
                throw new Exception("No gadgets stored in Server");
            }*/
        } catch (SQLException e) {
            throw new Exception("Error on SQL query. Code 3");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
        return gadgetList;
    }

    public void addGadget(int systemID, String type, String name, String room, String consumption) throws Exception {
        connect();
        int result;
        try {
            result = statement.executeUpdate("INSERT INTO Gadget (systemID, type, name, room, state, consumption) VALUES (" + systemID + ", '" + type + "', '" + name + "', '" + room + "', 0, " + consumption + ");");
            if (result != 1) {
                throw new Exception("Server unable to add gadget. Code 4a");
            }
        } catch (SQLException e) {
            throw new Exception("Server unable to add gadget. Code 4b");
        } finally {
            closeConnection();
        }
    }

    public void deleteGadget(int systemID, int gadgetID) throws Exception {
        connect();
        try {
            statement.executeUpdate("DELETE FROM Gadget WHERE systemID = " + systemID + " AND gadgetID = " + gadgetID + ";");
        } catch (SQLException e) {
            throw new Exception("Server unable to delete gadget.");
        } finally {
            closeConnection();
        }
    }

    public void editGadgetsInfo(int systemID, String type, int gadgetID, String name, String room, int consumption) throws Exception {
        connect();
        int result;
        try {
            result = statement.executeUpdate("UPDATE Gadget SET type = '" + type + "', name = '" + name + "', room = '" + room +  "', consumption = " + consumption + " WHERE systemID =  " + systemID + " AND gadgetID = " + gadgetID + ";");
            if (result != 1) {
                throw new Exception("Server unable to update gadget.");
            }
        } catch (SQLException e) {
            throw new Exception("Server unable to update gadget.");
        } finally {
            closeConnection();
        }
    }

    public ArrayList<String[]> getLogs(int systemID) throws Exception {
        connect();
        ArrayList<String[]> logs = new ArrayList<>();
        int results = 0;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Log WHERE systemID = " + systemID + ";");
            while (resultSet.next()) {
                results++;
                String timestamp = resultSet.getString("timestamp");
                String log = resultSet.getString("log");

                String[] items = {timestamp, log};
                logs.add(items);
            }

            if (results < 1) {
                throw new Exception("No logs stored");
            }

        } catch (SQLException e) {
            throw new Exception("Error on SQL query. Code 5");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
        return logs;
    }

    public String[] getLogCreationData(String accountID, int gadgetID) throws Exception {
        connect();
        String[] items = new String[4];
        try {
            resultSet = statement.executeQuery(
                    "SELECT Account.name AS `accountName`, Gadget.name AS `gadgetName`, Gadget.type, Gadget.room FROM Account, Gadget WHERE Account.accountID = '" + accountID + "' AND Gadget.gadgetID = " + gadgetID + ";");
            /*if (resultSet.getRow() != 1) { //Prints 0 (zero)
               throw new Exception("Unable to form log message");*/
            //} else {
            while (resultSet.next()) {
                String accountName = resultSet.getString("accountName");
                String gadgetName = resultSet.getString("gadgetName");
                String gadgetType = resultSet.getString("type");
                String gadgetRoom = resultSet.getString("room");
                items[0] = accountName;
                items[1] = gadgetName;
                items[2] = gadgetType;
                items[3] = gadgetRoom;
            }
            // }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error on SQL query: getLogCreationData");
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new Exception("NullPointer Exception: getLogCreationData");
        } finally {
            closeConnection();
        }
        return items;
    }


    public void addLog(int systemID, String logMessage) throws Exception {
        connect();
        int systemLogs = 0;
        try {
            //Count the logs of the system
            resultSet = statement.executeQuery("SELECT COUNT(*) as count FROM Log WHERE systemID = " + systemID + ";");
            while (resultSet.next()) {
                systemLogs = resultSet.getInt("count");
            }
            //If logs per system exceeds 9; delete oldest
            if (systemLogs >= 10) {
                statement.executeUpdate("DELETE FROM Log WHERE systemID = " + systemID + " ORDER By Log.logID LIMIT 1;");
            }
            //Add new log. Note: timestamp is added by default by MySQL
            statement.executeUpdate("INSERT INTO Log (systemID, log) VALUES (" + systemID + ", '" + logMessage + "');");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error on SQL query. Code 6");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
    }

    public ArrayList<String[]> getAccounts(int systemID) throws Exception {
        connect();
        ArrayList<String[]> accountList = new ArrayList<>();
        int results = 0;
        try {
            resultSet = statement.executeQuery("SELECT accountID, name, password, admin FROM Account WHERE systemID = " + systemID + ";");
            while (resultSet.next()) {
                results++;
                String accountID = resultSet.getString("accountID");
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                String admin = resultSet.getString("admin");

                //Don't return password of other admins
                if (admin.equals("1")) {
                    password = "accessDenied";
                }
                String[] items = {accountID, name, password, admin};
                accountList.add(items);
            }
            if (results < 1) {
                throw new Exception("Unable to return accounts from server");
            }
        } catch (SQLException e) {
            throw new Exception("Error on SQL query. Code 3");
        } catch (NullPointerException e) {
            throw new Exception("NullPointer Exception");
        } finally {
            closeConnection();
        }
        return accountList;
    }

    public void addAccount(String accountID, int systemID, String name, String admin, String password) throws Exception {
        connect();
        int sameAccountID = 0;
        int result;
        try {
            //Assure no other has the requested accountID (same mail)
            resultSet = statement.executeQuery("SELECT COUNT(*) as count FROM Account WHERE accountID = '" + accountID + "';");
            while (resultSet.next()) {
                sameAccountID = resultSet.getInt("count");
            }
            //If the requested accountID (mail) is already taken
            if (sameAccountID >= 10) {
                throw new Exception("The requested email already exists");
            }
            result = statement.executeUpdate("INSERT INTO Account (accountID, systemID, name, password, admin) VALUES ('" + accountID + "', " + systemID + ", '" + name + "', '" + password + "', '" + admin + "');");
            if (result != 1) {
                throw new Exception("Server unable to add user.");
            }
        } catch (SQLException e) {
            throw new Exception("Server unable to add user.");
        } finally {
            closeConnection();
        }
    }

    public void deleteAccount(int systemID, String accountID) throws Exception {
        connect();
        try {
            statement.executeUpdate("DELETE FROM Account WHERE systemID = " + systemID + " AND accountID = '" + accountID + "';");
        } catch (SQLException e) {
            throw new Exception("Server unable to delete account.");
        } finally {
            closeConnection();
        }
    }

    public void editAccountsInfo(int systemID, String accountID, String name, String admin, String password) throws Exception {
        connect();
        int result;
        try {
            result = statement.executeUpdate("UPDATE Account SET name = '" + name + "', admin = " + admin + ", password = '" + password + "' WHERE systemID = " + systemID + " AND accountID = '" + accountID + "';");
            if (result != 1) {
                throw new Exception("Server unable to update account info.");
            }
        } catch (SQLException e) {
            throw new Exception("Server unable to update account info.");
        } finally {
            closeConnection();
        }
    }

}

