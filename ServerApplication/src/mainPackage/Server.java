package mainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private BlockingQueue<ClientRequest> serverRequest;

    //Maybe add a queue for serverOutputs to clients, which method outputToClient could scan from a new thread

    private final List<Client> activeClients;

    private final Object lockObject1;
    private final Object lockObject2;

    private static Server instance = null;

    private Server() {
        serverRequest = new ArrayBlockingQueue<>(10);
        //If synchronizing ordinary list with synchronized blocks doesn't work; try Vector or CopyOnWriteArrayList or Set
        activeClients = Collections.synchronizedList(new ArrayList<>());
        lockObject1 = new Object();
        lockObject2 = new Object();
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public BlockingQueue<ClientRequest> getServerRequest() {
        return serverRequest;
    }

    public void run() {
        //Deploy methods on threads.
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    acceptClientConnections();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                processClientRequests();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Invoked by t1
    private void acceptClientConnections() throws IOException {
        synchronized (lockObject1) {

            // Thread pool for better control of the threads being launched.
            ExecutorService executor = Executors.newFixedThreadPool(10);

            // The server is listening on a port
            ServerSocket server = new ServerSocket(8081);

            System.out.println("Server running...");

            while (true) {

                Socket clientConnection = null;
                Client client = null;

                try {
                    // socket object to receive incoming client requests
                    clientConnection = server.accept();

                    // obtaining input and output streams
                    DataInputStream input = new DataInputStream(clientConnection.getInputStream());
                    DataOutputStream output = new DataOutputStream(clientConnection.getOutputStream());

                    client = new Client("null", 0, false, clientConnection, input, output);

                    // Assigning new thread for client
                    executor.submit(new ClientThread(client));

                } catch (Exception e) {
                    clientConnection.close();
                    e.printStackTrace();
                    System.out.println("Exception in acceptClients");
                }
            }
        }
    }

    //Invoked by acceptClientConnections() -> t1
    private void addClient(Client client) {
        synchronized (activeClients) {
            activeClients.add(client);
            //LOG PURPOSE
            iterateActiveClients();
        }
    }

    //Invoked by ClientThreads
    public void removeClient(Client clientToRemove) {
        synchronized (activeClients) {

            for (int i = 0; i < activeClients.size(); i++) {
                if (activeClients.get(i).getSocket().equals(clientToRemove.getSocket())) {
                    try {
                        activeClients.get(i).getSocket().close();
                        activeClients.get(i).getOutput().close();
                        activeClients.get(i).getInput().close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                        System.out.println("Remove client error: Unable to close all resources for client " + activeClients.get(i).getAccountID());
                    }
                    finally {
                        activeClients.remove(i);
                    }
                }
            }
            //LOG PURPOSE
            iterateActiveClients();
        }
    }

    //Invoked by acceptClientConnections() t1,
    public void iterateActiveClients() {
        synchronized (activeClients) {
            String clients = "THREAD " + Thread.currentThread().getName() + " Active clients:\n";
            if(activeClients.size() == 0) {
                clients = clients.concat("Zero");
            } else {
                for (Client client : activeClients) {
                    clients = clients.concat(client.getAccountID() + " " + (client.isAdmin() ? "[Admin]" : "[Non-admin]") + "  " + client.getSocket().getInetAddress()  + "\n");
                }
            }
            System.out.println(clients);
        }
    }

    //Invoked by ClientThread to assure account is logged in to proceed
    public Client login(ClientRequest loginRequest) {
        synchronized (lockObject2) { //Same lock as processClientRequest, to assure no simultanous operations on DB
            Client client = loginRequest.getClient();

            String[] commands = loginRequest.getRequest().split(":");

            if (commands[0].equals("1")) { //If it is a login request

                try {
                    //Try to log in with [1] accountID and [2] password
                    String[] result = DB.getInstance().login(commands[1], commands[2]); //login() throws Exception

                    String accountID = result[0];
                    String systemID = result[1];
                    String name = result[2];
                    String admin = result[3];
                    String password = result[4];

                    client = new Client(
                            accountID, Integer.parseInt(systemID), admin.equals("1") ? true : false, loginRequest.getClient().getSocket(), loginRequest.getClient().getInput(), loginRequest.getClient().getOutput());

                    //Add client to list of active clients
                    addClient(client);

                    String clientOutput = String.format("%s%s%s%s%s%s%s%s%s%s",
                            "2:ok:null:", accountID, ":", systemID, ":", name, ":", admin, ":", password);

                    outputToClients(true, false, (clientOutput), loginRequest.getClient().getSocket(), loginRequest.getClient().getSystemID());
                } catch (Exception e) {
                    String exceptionMessage = e.getMessage();
                    try {
                        client.getOutput().writeUTF("2:no:".concat(exceptionMessage));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return client;
        }
    }

    //Invoked by t2
    public void processClientRequests() {
        ClientRequest clientRequest;

        while (true) {
            try {
                //See if there is anything
                clientRequest = serverRequest.take();

                synchronized (lockObject2) {

                    //Process the request according to LAAS communication protocol
                    String commands[] = clientRequest.getRequest().split(":");

                    String clientOutput;

                    switch (commands[0]) {
                        case "1": //Login request
                            clientOutput = "15:Already logged in";
                            outputToClients(true, false, clientOutput, clientRequest.getClient().getSocket(), clientRequest.getClient().getSystemID());
                            break;
                        case "3": //Request to update a gadget's state
                            //Will form a 4:XX command
                            break;
                        case "5": //Individual request for all gadgets info
                            break;
                        case "6": //Request to alter gadget's info
                            break;
                        case "7": //Request to add a gadget
                            break;
                        case "9": // Individual request for all users info
                            break;
                        case "10": // Request to alter user info
                            break;
                        case "11": // Request add a user
                            break;
                        case "13": //Log request
                            try{
                                int systemId = clientRequest.getClient().getSystemID(); // DO THIS INSTEAD. More reliable. Can't be manipulated at client end. Won't need NumberFormatException
                                ArrayList<String[]> logs = DB.getInstance().getLogs(systemId);

                                //Form the output string according to LAAS protocol: 12:timestamp:log:next/null
                                clientOutput = "14:";
                                for(int i = 0 ; i < logs.size() ; i++) {

                                    // NOTE: timestamp contains colon (ex 18:34:15), which is the break mark for commands in LAAS protocol,
                                    // so we first need to exchange the colons in the timestamp with "&"
                                    logs.get(i)[0] = logs.get(i)[0].replace(":", "&");
                                    //Remove 'seconds' from timestamp        start                       end  (removing ':seconds')
                                    logs.get(i)[0] = logs.get(i)[0].substring(0, logs.get(i)[0].length() - 3);

                                    //                                    timestamp           log message
                                    clientOutput = clientOutput.concat(logs.get(i)[0] + ":" + logs.get(i)[1]);
                                    //if there are more logs to read, or not
                                    if(i == logs.size() - 1) {
                                        clientOutput = clientOutput.concat(":null");
                                    }else {
                                        clientOutput = clientOutput.concat(":next:");
                                    }
                                }
                                outputToClients(true, false, clientOutput, clientRequest.getClient().getSocket(), clientRequest.getClient().getSystemID());
                            } catch (Exception e){
                                outputToClients(true, false, "13:".concat(e.getMessage()), clientRequest.getClient().getSocket(), clientRequest.getClient().getSystemID());
                            }
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // Produces output commands 4 and 8 of LAAS communication protocol
    private void gadgetsRequest(Client fromClient, boolean onlyGadgetStates, boolean onlyToIndividual) {
        String clientOutput = onlyGadgetStates ? "4:" : "8:";
        try {
            ArrayList<String[]> gadgetList = DB.getInstance().getGadgets(fromClient.getSystemID(), onlyGadgetStates);
            // If there were no sql results
            if (gadgetList.size() < 1) {
                clientOutput = clientOutput.concat("null");
            } else {
                clientOutput = clientOutput.concat("notnull:");
                // Send gadget state info ("4:XXX")
                if(onlyGadgetStates) {
                    for (int i = 0; i < gadgetList.size(); i++) {
                        String gadgetID = gadgetList.get(i)[1];
                        String state = gadgetList.get(i)[2];

                        clientOutput = String.format("%s%s%s%s",
                                clientOutput, gadgetID, ":", state);

                        if (i == gadgetList.size() - 1) {
                            clientOutput = clientOutput.concat(":null");
                        } else {
                            clientOutput = clientOutput.concat(":next:");
                        }
                    }
                    // Sends full gadgets info ("8:XXX")
                }else {
                    for (int i = 0; i < gadgetList.size(); i++) {
                        String type = gadgetList.get(i)[0];
                        String gadgetID = gadgetList.get(i)[1];
                        String name = gadgetList.get(i)[2];
                        String room = gadgetList.get(i)[3];
                        String state = gadgetList.get(i)[4];
                        String consumption = gadgetList.get(i)[5];

                        clientOutput = String.format("%s%s%s%s%s%s%s%s%s%s%s%s",
                                clientOutput, type, ":", gadgetID, ":", name, ":", room, ":", state, ":", consumption);

                        if (i == gadgetList.size() - 1) {
                            clientOutput = clientOutput.concat(":null");
                        } else {
                            clientOutput = clientOutput.concat(":next:");
                        }
                    }
                }
            }
            outputToClients(onlyToIndividual, false, clientOutput, fromClient.getSocket(), fromClient.getSystemID());
        } catch (Exception e) {
            outputToClients(true, false, "15:".concat(e.getMessage()), fromClient.getSocket(), fromClient.getSystemID());
        }
    }

    private void addLog(Client fromClient, int gadgetID, int newState) throws Exception {
        String items[] = new String[4];
        try {
            //Get the data needed to form a user friendly log message
            items = DB.getInstance().getLogCreationData(fromClient.getAccountID(), gadgetID);
            String accountName = items[0];
            String gadgetName = items[1];
            String gadgetType = items[2];
            String gadgetRoom = items[3];

            //Form a user friendly log message
            String logMessage = accountName;
            switch (gadgetType) {
                case "Heat":
                    logMessage = String.format("%s%s%s%s%s", logMessage, " turned heat to ", newState, " C in ", gadgetRoom);
                    break;
                case "Door":
                    logMessage = String.format("%s%s%s", logMessage, (newState == 1 ? " locked ":" unlocked "), gadgetName);
                    break;
                default:
                    logMessage = String.format("%s%s%s%s%s%s", logMessage, " turned ", (newState == 1 ? "on ":"off "), gadgetName, " in ", gadgetRoom);
                    break;
            }
            DB.getInstance().addLog(fromClient.getSystemID(), logMessage);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //Produces command 14
    private void logRequest(Client fromClient) {
        try {
            ArrayList<String[]> logs = DB.getInstance().getLogs(fromClient.getSystemID());

            //Form the output string according to LAAS protocol: 12:timestamp:log:next/null
            String clientOutput = "14:";
            for (int i = 0; i < logs.size(); i++) {

                // NOTE: timestamp contains colon (ex 18:34:15), which is the break mark for commands in LAAS protocol,
                // so we first exchange the colons in the timestamp with "&"
                logs.get(i)[0] = logs.get(i)[0].replace(":", "&");
                //Remove 'seconds' from timestamp        start                       end  (removing ':seconds')
                logs.get(i)[0] = logs.get(i)[0].substring(0, logs.get(i)[0].length() - 3);

                //                                                       timestamp           log message
                clientOutput = String.format("%s%s%s%s", clientOutput, logs.get(i)[0], ":", logs.get(i)[1]);
                //if there are more logs to read, or not
                if (i == logs.size() - 1) {
                    clientOutput = clientOutput.concat(":null");
                } else {
                    clientOutput = clientOutput.concat(":next:");
                }
            }
            outputToClients(true, false, clientOutput, fromClient.getSocket(), fromClient.getSystemID());
        } catch (Exception e) {
            outputToClients(true, false, "15:".concat(e.getMessage()), fromClient.getSocket(), fromClient.getSystemID());
        }
    }

    //Invoked by processClientRequests() -> t2
    public void outputToClients(boolean onlyToIndividual, boolean onlyToAdmins, String message, Socket connection, int systemID) {
        synchronized (activeClients) {
            //System.out.println("LIST SIZE = " + activeClients.size());
            try {
                if (onlyToIndividual) {
                    for (Client client : activeClients) {
                        if (client.getSocket().equals(connection)) {
                            if ((onlyToAdmins & client.isAdmin()) || !onlyToAdmins) {
                                try {
                                    client.getOutput().writeUTF(message);
                                } catch (IOException e) {
                                    //Won't be used. Inactive clients are removed before this
                                    System.out.println("Unable to write to: " + client.getAccountID());
                                }
                            }
                        }
                    }
                } else {
                    for (Client client : activeClients) {
                        if (client.getSystemID() == systemID) {
                            if ((onlyToAdmins & client.isAdmin()) || !onlyToAdmins) {
                                client.getOutput().writeUTF(message);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
