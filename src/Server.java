import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class Server {
    private ServerSocket server = null;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for clients ...");
            Thread sendToClientsThread = new Thread(() -> {
                try {
                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(System.in));
                    String serverMessage;
                    while (true) {
                        serverMessage = serverReader.readLine();
                        sendToAllClients("Server:" + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            sendToClientsThread.start();

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Client  connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler); // Add client to the list
                new Thread(clientHandler).start();
            }
        }



