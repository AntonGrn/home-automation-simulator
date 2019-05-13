package mainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
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
                    } finally {
                        activeClients.remove(i);
                    }
                }
            }
            //LOG PURPOSE
            iterateActiveClients();
        }
    }

    // Server log purpose. Standard output -> Server log
    public void iterateActiveClients() {
        synchronized (activeClients) {
            String clients = "=========== " + " Active clients ======== " + Thread.currentThread().getName() + "\n";
            if (activeClients.size() == 0) {
                clients = clients.concat("Zero");
            } else {
                for (Client client : activeClients) {
                    clients = clients.concat(client.getAccountID() + " " + (client.isAdmin() ? "[Admin]" : "[Non-admin]") + " " + client.getSocket().getInetAddress() + "\n");
                }
            }
            System.out.println(clients + "\n==========================================");
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
                            accountID, Integer.parseInt(systemID), admin.equals("1"), loginRequest.getClient().getSocket(), loginRequest.getClient().getInput(), loginRequest.getClient().getOutput());

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

    //Process requests from logged in clients
    public void processClientRequests() {
        ClientRequest clientRequest;
        while (true) {
            try {
                //Waits here until there are any requests
                clientRequest = serverRequest.take();

                synchronized (lockObject2) {
                    //Process the request according to LAAS communication protocol
                    String commands[] = clientRequest.getRequest().split(":");

                    switch (commands[0]) {
                        case "1": //Login request. If user can reach this; user is already logged in.
                            outputToClients(true, false, "15:Already logged in", clientRequest.getClient().getSocket(), clientRequest.getClient().getSystemID());
                            break;
                        case "3": //Request to update a gadget's state
                            updateGadgetState(commands, clientRequest.getClient());
                            break;
                        case "5": //Individual request for all gadgets info
                            gadgetsRequest(clientRequest.getClient(), false, true);
                            break;
                        case "6": //Request to alter gadget's info
                            break;
                        case "7": //Request to add a gadget
                            updateAddGadget(commands, clientRequest.getClient());
                            break;
                        case "9": // Individual request for all users info
                            break;
                        case "10": // Request to alter user info
                            break;
                        case "11": // Request add a user
                            break;
                        case "13": //Log request
                           logRequest(clientRequest.getClient());
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGadgetState(String[] commands, Client fromClient) {
        //Note: In the database; all gadget states are integers; heat=value, others=boolean(1/0)
        int systemID = fromClient.getSystemID();
        int gadgetID = Integer.parseInt(commands[1]);
        int newState = Integer.parseInt(commands[2]);
        try{
            DB.getInstance().setGadgetState(systemID, gadgetID, newState);
            //output to clients
            gadgetsRequest(fromClient, true, false);
        } catch(Exception e) {
            outputToClients(true, false, "15:".concat(e.getMessage()), fromClient.getSocket(), fromClient.getSystemID());
        }
        //Add gadget usage log
        try {
            addLog(fromClient, gadgetID, newState);
        } catch (Exception e) {
            System.out.println("Adding logs error " + e.getMessage() );
        }
    }

    private void updateAddGadget(String[] commands, Client fromClient) {
        String type = commands[1];
        String name = commands[2];
        String room = commands[3];
        String consumption = commands[4];
        try {
            DB.getInstance().addGadget(fromClient.getSystemID(), type, name, room, consumption);
            gadgetsRequest(fromClient, false, false);
        } catch (Exception e) {
            outputToClients(true, false, "15:".concat(e.getMessage()), fromClient.getSocket(), fromClient.getSystemID());
        }
    }

    //The output operation
    private void outputToClients(boolean onlyToIndividual, boolean onlyToAdmins, String message, Socket connection, int systemID) {
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
