import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientFILE {

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    public ClientFILE(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            input = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    receiveMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            System.out.println("Send message to the Server: ");

            new Thread(() -> {
                try {
                    sendMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }

    }
