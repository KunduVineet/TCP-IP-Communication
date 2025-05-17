package Server;

import Protocol.IProtocol;
import Protocol.ProtocolFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Server for handling multiple device connections over TCP/IP.
 */
public class DeviceServer {
    private final int port;
    private final IProtocol protocol;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public DeviceServer(int port, IProtocol protocol) {
        this.port = port;
        this.protocol = protocol;
        this.running = false;
    }

    public void start() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New Device Connected: " + clientSocket.getInetAddress());
                    new Thread(new DeviceHandler(clientSocket, protocol)).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Server error: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        try (var input = DeviceServer.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("config.properties not found");
            }
            config.load(input);
        }

        int port = Integer.parseInt(config.getProperty("serversocket.port", "8080"));
        String protocolName = config.getProperty("protocol", "simple");
        IProtocol protocol = ProtocolFactory.getProtocol(protocolName);
        DeviceServer server = new DeviceServer(port, protocol);
        server.start();
    }
}