import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket server = null;
    private List<ClientHandler> clients = new ArrayList<>();
    private String allowedClientIP = "Admin IP";
    private File logFileDir = new File("Folder Path");

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for clients ...");

            while (true) {
                Socket clientSocket = server.accept();

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataInputStream in;
        private DataOutputStream out;
        private String clientIP;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                this.out = new DataOutputStream(socket.getOutputStream());
                this.clientIP = socket.getInetAddress().getHostAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

