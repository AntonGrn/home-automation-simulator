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

public class Server {

    private BlockingQueue<ClientRequest> serverRequest;

    //Maybe add a queue for serverOutputs to clients, which method outputToClient could scan from a new thread???

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
    public void acceptClientConnections() throws IOException {
        synchronized (lockObject1) {

            // The server is listening on a port
            ServerSocket server = new ServerSocket(8081);

            System.out.println("Server running...");

            while (true) {

                Socket clientConnection = null;
                Thread clientThread = null;
                Client client = null;

                try {
                    // socket object to receive incoming client requests
                    clientConnection = server.accept();

                    System.out.println("New client connected : " + clientConnection);

                    // obtaining input and output streams
                    DataInputStream input = new DataInputStream(clientConnection.getInputStream());
                    DataOutputStream output = new DataOutputStream(clientConnection.getOutputStream());

                    System.out.println("Assigning new thread for client");

                    client = new Client("null", 0, clientConnection, input, output);

                    // Create a new thread object
                    clientThread = new ClientThread(client);

                    // Invoking the start() method
                    clientThread.start();

                } catch (Exception e) {
                    clientConnection.close();
                    e.printStackTrace();
                    System.out.println("Exception in acceptClients");
                }
            }
        }
    }

    //Invoked by acceptClientConnections() -> t1
    public void addClient(Client client) {
        synchronized (activeClients) {
            activeClients.add(client);
            System.out.println("Client added by thread " + Thread.currentThread().getName());
        }
    }

    //Invoked by ClientThreads
    public void removeClient(Client clientToRemove) {
        synchronized (activeClients) {
            System.out.println("THREAD " + Thread.currentThread().getName() + " REMOVING CLIENT: " + clientToRemove.getAccountID());
            System.out.println("LIST SIZE: " + activeClients.size());

            Iterator<Client> iterator = activeClients.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getSocket().equals(clientToRemove.getSocket())) {
                    iterator.remove();
                    System.out.println("Account found and removed");
                    break;
                }
            }
        }
    }

    //Invoked by acceptClientConnections() t1,
    public void iterateActiveClients() {
        synchronized (activeClients) {
            System.out.println("THREAD " + Thread.currentThread().getName() + " ACTIVE CLIENTS METHOD:");

            for (Client client : activeClients) {
                System.out.println(client.getAccountID());
            }
        }
    }

    //Invoked by ClientThread to assure account is logged in to proceed
    public Client login(ClientRequest loginRequest) {
        synchronized (lockObject2) { //Same lock as processClientRequest, to assure no simultanous operations on DB
            Client client = loginRequest.getClient();

            String[] commands = loginRequest.getRequest().split(":");

            if (commands[0].equals("1")) { //If it is a login request
                System.out.println("Login attempt");

                try {
                    //Try to log in with [1] accountID and [2] password
                    String[] result = DB.getInstance().login(commands[1], commands[2]); //login() throws Exception

                    System.out.println("HAS been at DB");

                    String accountID = result[0];
                    String systemID = result[1];
                    String name = result[2];
                    String accessLevel = result[3];
                    String password = result[4];

                    client = new Client(
                            accountID, Integer.parseInt(systemID), loginRequest.getClient().getSocket(), loginRequest.getClient().getInput(), loginRequest.getClient().getOutput());

                    //Add client to list of active clients
                    addClient(client);

                    String clientOutput = String.format("%s%s%s%s%s%s%s%s%s%s",
                            "2:ok:null:", accountID, ":", systemID, ":", name, ":", accessLevel, ":", password);

                    outputToClients(true, (clientOutput), loginRequest.getClient().getSocket(), loginRequest.getClient().getSystemID());
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
        System.out.println("process clients requests");

        ClientRequest clientRequest;
        while (true) {

            iterateActiveClients();
            clientRequest = null;
            try {
                //See if there is anything
                clientRequest = serverRequest.take(); //BEFORE: Server.getInstance().getServerRequest().take();

                synchronized (lockObject2) {

                    System.out.println("CLIENT REQUEST: " + clientRequest.getRequest());

                    //Process the request according to LAAS communication protocol
                    String commands[] = clientRequest.getRequest().split(":");

                    System.out.println("THREAD " + Thread.currentThread().getName() + " PROCESSING REQUEST");

                    switch (commands[0]) {
                        case "1": //Login request

                            String clientOutput = "13:Already logged in";
                            outputToClients(true, (clientOutput), clientRequest.getClient().getSocket(), clientRequest.getClient().getSystemID());

                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Invoked by processClientRequests() -> t2
    public void outputToClients(boolean individual, String message, Socket connection, int systemID) {
        synchronized (activeClients) {
            System.out.println("LIST SIZE = " + activeClients.size());
            try {
                if (individual) {
                    for (Client client : activeClients) {
                        if (client.getSocket().equals(connection)) {
                            try {
                                client.getOutput().writeUTF(message);
                            } catch (IOException e) {
                                //Won't be used. Inactive clients are removed before this
                                System.out.println("Unable to write to: " + client.getAccountID());
                            }
                        }
                    }
                } else {
                    for (Client client : activeClients) {
                        if (client.getSystemID() == systemID) {
                            client.getOutput().writeUTF(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
