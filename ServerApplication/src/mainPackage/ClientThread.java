package mainPackage;

import java.io.IOException;

public class ClientThread extends Thread {

        private Client client;

        public ClientThread(Client client) {
            this.client = client;
        }

        @Override
        public void run() {

            System.out.println(" NEW CLIENT THREAD: " + Thread.currentThread());

            //Before continuing the input loop, the thread assures that a login attempt
            //has been succeeded. If not; thread cancels and removes client from clientList
            try {
                String request = client.getInput().readUTF();
                ClientRequest loginRequest = new ClientRequest(client, request);
                client = Server.getInstance().login(loginRequest); //Assign the complete client information gathered by the login operation.
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Exception on login");
            }

            if(!client.getAccountID().equals("null")) { //If login succeeded (account has been assigned with other than "null"; proceed:

                while (true) {
                    String messageFromClient;
                    try {
                        // Read from client
                        messageFromClient = client.getInput().readUTF();
                        ClientRequest clientRequest = new ClientRequest(client, messageFromClient);

                        //Here: If command is log out; closeResources() + break;

                        try {
                            //Add to Server request list
                            Server.getInstance().getServerRequest().put(clientRequest);
                            System.out.println("Message from client has been added to serverRequests");
                        } catch (InterruptedException i) {
                            i.printStackTrace();
                        }

                    } catch (IOException e) {  //SocketException is subclass
                        System.out.println("Client has disconnected");
                        closeResources();
                        break;
                    }
                }
            } else{
                System.out.println("Client did not log in successfully");
                closeResources();
            }
        }

        private void closeResources() {
            try {
                System.out.println("Closing this connection...");
                client.getSocket().close();
                client.getInput().close();
                client.getOutput().close();
                //+Remove client from activeClients.
                Server.getInstance().removeClient(client);

                //PRINT JUST TO SHOW
                Server.getInstance().iterateActiveClients();

                System.out.println("Connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
