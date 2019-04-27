package mainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class ServerConnection {

    private final String ip;
    private final int port;

    public ServerConnection() {
        ip = "134.209.198.123";
        port = 8081;
    }

    public void connectToServer() throws Exception{
        try {
            // Establish the connection with server
            Socket socket = new Socket(ip, port);

            // Obtaining input and output streams
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //Run input and output on new threads.
            //If needs to be handled later; better to manage the threads via thread pools (ExecutorService)
            Thread outputThread = new ClientOutputThread(socket, output);
            Thread inputThread = new ClientInputThread(socket, input);

            outputThread.start();
            inputThread.start();

            /*Main.getMainWindowController().outOne = new ClientOutputThread(socket, output);
            Main.getMainWindowController().inOne = new ClientInputThread(socket, input);
            Main.getMainWindowController().outOne.start();
            Main.getMainWindowController().inOne.start();*/

        } catch (ConnectException e) {
            throw new Exception("Unable to connect to Server");
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Unable to set up input/output resources");
        }
    }
    
}
