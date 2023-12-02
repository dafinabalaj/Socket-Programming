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

        @Override
        public void run() {
            try {
                int clientPort = clientSocket.getPort();

                System.out.println("Client connected from IP: " + clientIP + ", Port: " + clientPort);

                String line = "";
                while (!line.equals("Over")) {
                    line = in.readUTF();
                    System.out.println("Message from client " + clientIP + " (Port: " + clientPort + "): " + line);

                    if (line.equals("ReadFile")) {
                        sendFileContent();
                    } else if (line.startsWith("CreateFile ") && clientIP.equals(allowedClientIP)) {
                        createFileOnServer(line.substring("CreateFile ".length()));
                    } else if (clientIP.equals(allowedClientIP)) {
                        try (FileWriter writer = new FileWriter(getLogFilePath(), true);
                             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                            bufferedWriter.write("Client " + clientIP + " (Port: " + clientPort + "): " + line);
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Closing connection with client " + clientIP);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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

        private void sendFileContent() {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(getLogFilePath()))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    out.writeUTF(line);
                }
                out.writeUTF("Over");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void createFileOnServer(String fileName) {
            try {
                File newFile = new File(logFileDir, fileName);
                if (newFile.createNewFile()) {
                    System.out.println("File created on the server: " + newFile.getAbsolutePath());
                } else {
                    System.out.println("File creation failed. The file already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getLogFilePath() {
            return logFileDir.getAbsolutePath() + File.separator + "file.txt";
        }
    }
}