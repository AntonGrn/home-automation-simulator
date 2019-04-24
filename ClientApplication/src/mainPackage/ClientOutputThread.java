package mainPackage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientOutputThread extends Thread{
    private final Socket socket;
    private final DataOutputStream output;

    public ClientOutputThread(Socket socket, DataOutputStream output) {
        this.socket = socket;
        this.output = output;
    }

    @Override
    public void run() {
        String messageToServer;

        while (true) {
            try {
                //Thread will wait here, until something to take
                messageToServer = Main.getMainWindowController().requestsToServer.take();
                //Send request to server
                output.writeUTF(messageToServer);
                output.flush();
            } catch (InterruptedException e) {
                e.printStackTrace();
                closeResources();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                closeResources();
                break;
            }
        }
    }

    private void closeResources() {
        try {
            socket.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
