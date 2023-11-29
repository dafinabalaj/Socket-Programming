public class Client {

  import java.io.*;
import java.net.*;

public class Client {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");
            input = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());

            // Create a thread to read messages from the server
            Thread readFromServer = new Thread(() -> {
                try {
                    DataInputStream serverInput = new DataInputStream(socket.getInputStream());
                    String serverMessage;
                    while (true) {
                        serverMessage = serverInput.readUTF();
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
           
}
