import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class Server {
    private ServerSocket server = null;
    private int clientCounter = 0;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for clients ...");

            while (true) {
                Socket clientSocket = server.accept();
                clientCounter++;
                System.out.println("Client " + clientCounter + " connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket, clientCounter);
                clients.add(clientHandler); // Add client to the list
                new Thread(clientHandler).start();


                // Create a thread to send messages to the connected client
                Thread sendToClientThread = new Thread(() -> {
                    try {
                        BufferedReader serverReader = new BufferedReader(new InputStreamReader(System.in));
                        String serverMessage;
                        while (true) {
                            serverMessage = serverReader.readLine();
                            sendToAllClients(serverMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
}
