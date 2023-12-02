import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    public Client(String address, int port) {
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

        private void receiveMessages() throws IOException {
            String message;
            while (true) {
                message = input.readUTF();
                if (message.equals("Over")) {
                    break;
                }
                System.out.println("Message from server: " + message);
            }
        }

        private void sendMessages() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (!(line = reader.readLine()).equals("Over")) {
                out.writeUTF(line);
            }
            out.writeUTF("Over");
        }



        public static void main(String args[]) {
            Client client = new Client("Server IP", 5000);
        }
    }

