package mainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class ServerConnection {

    private static ServerConnection instance = null;

    private final String ip;
    private final int port;
    //We need the threads to be accessible in order to interrupt them if necessary.
    private Thread inputThread;
    private Thread outputThread;
    private Socket socket;
    private boolean threadsRunning;

    private ServerConnection() {
        ip = "134.209.198.123";
        port = 8081;
        threadsRunning = false;
    }

    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    public void connectToServer() throws Exception {
        try {
            if (threadsRunning) {
                closeResources();
            }

            // Establish the connection with server
            socket = new Socket(ip, port);

            // Obtaining input and output streams
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //Run input and output on new threads.
            outputThread = new ClientOutputThread(socket, output);
            inputThread = new ClientInputThread(socket, input);

            outputThread.start();
            inputThread.start();

            threadsRunning = true;

        } catch (ConnectException e) {
            throw new Exception("Unable to connect to Server");
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Unable to set up input/output resources");
        }
    }

    public void closeResources() {
        try {
            if (threadsRunning) {
                inputThread.interrupt();
                outputThread.interrupt();
                inputThread.join();
                outputThread.join();
                socket.close();
            }
            threadsRunning = false;
            AccountLoggedin.getInstance().setLoggedInAccount(null);
            System.out.println("Closed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
