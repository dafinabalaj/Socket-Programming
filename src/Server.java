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

        catch (IOException e) {
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
        MultiThreadedServer multiThreadedServer = new MultiThreadedServer(5000);
    }

    private void sendToAllClients(String message) {
        for (ClientHandler client : clients) {
            client.sendMessageToClient(message);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataInputStream in;
        private DataOutputStream out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                this.out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessageToClient(String message) {
            try {
                out.writeUTF(message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
}

