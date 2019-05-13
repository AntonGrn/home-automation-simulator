package mainPackage;

import java.io.IOException;

public class ClientThread extends Thread {

        private Client client;

        public ClientThread(Client client) {
            this.client = client;
        }

        @Override
        public void run() {

            //Before continuing the input loop, the thread assures that a login attempt
            //has been succeeded. If not; thread cancels and removes client from clientList
            try {
                String request = client.getInput().readUTF();
                ClientRequest loginRequest = new ClientRequest(client, request);
                client = Server.getInstance().login(loginRequest); //Assign the complete client information gathered by the login operation.
            } catch (IOException e) {
                System.out.println("Exception on login in clientThread " + Thread.currentThread() + "Client IP " + client.getSocket().getInetAddress());
            }

            if(!client.getAccountID().equals("null")) { //If login succeeded (account has been assigned with other than "null"; proceed:

                while (true) {
                    String messageFromClient;
                    try {
                        // Read from client
                        messageFromClient = client.getInput().readUTF();
                        ClientRequest clientRequest = new ClientRequest(client, messageFromClient);

                        if(messageFromClient.equals("16")) { //Logout request
                            closeResources();
                            break;
                        }

                        try {
                            //Add to Server request list
                            Server.getInstance().getServerRequest().put(clientRequest);
                            //System.out.println("Message from client has been added to serverRequests");
                        } catch (InterruptedException i) {
                            i.printStackTrace();
                        }

                    } catch (IOException e) {  //SocketException is subclass. Client has disconnected
                        closeResources();
                        break;
                    }
                }
            } else{
                System.out.println("Client did not log in successfully. Client: " + client.getSocket().getInetAddress());
                closeResources();
            }
        }

        private boolean loginAttempt() {
            return true;
        }

        private void closeResources() {
            try {
                //Remove client from activeClients.
                Server.getInstance().removeClient(client);
                //Close resources
                client.getSocket().close();
                client.getInput().close();
                client.getOutput().close();

            } catch (IOException e) {
                System.out.println("IOException on closing clientThread: " + Thread.currentThread());
            }

        }
    }
