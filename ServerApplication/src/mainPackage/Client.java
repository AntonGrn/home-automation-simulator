package mainPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {

    private final String accountID;
    private int systemID;
    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    public Client(String accountID, int systemID, Socket socket, DataInputStream input, DataOutputStream output) {
        this.accountID = accountID;
        this.systemID = systemID;
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    public DataInputStream getInput() {
        return input;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getAccountID() {
        return accountID;
    }

    public int getSystemID() {
        return systemID;
    }

    public void setSystemID(int systemID) {
        this.systemID = systemID;
    }
}
