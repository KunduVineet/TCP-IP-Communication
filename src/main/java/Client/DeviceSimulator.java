package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

/**
 * Simulates a device communicating with the server.
 */
public class DeviceSimulator {
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public DeviceSimulator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server at " + host + ":" + port);
        } catch (IOException e) {
            throw new IOException("Failed to connect to server at " + host + ":" + port, e);
        }
    }

    public synchronized String sendRead(String data) throws IOException {
        validateConnection();
        out.println("READ " + data);
        String response = in.readLine();
        if (response == null) {
            throw new IOException("Server closed connection");
        }
        return response;
    }

    public synchronized String sendWrite(String data) throws IOException {
        validateConnection();
        out.println("WRITE " + data);
        String response = in.readLine();
        if (response == null) {
            throw new IOException("Server closed connection");
        }
        return response;
    }

    private void validateConnection() {
        if (out == null || in == null) {
            throw new IllegalStateException("Connection not established. Call connect() first.");
        }
    }

    public void disconnect() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties config = new Properties();
        try (var input = DeviceSimulator.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("config.properties not found");
            }
            config.load(input);
        }

        String host = config.getProperty("server.host", "localhost");
        int port = Integer.parseInt(config.getProperty("serversocket.port", "8080"));
        int deviceCount = Integer.parseInt(config.getProperty("client.devices", "3"));

        Thread[] devices = new Thread[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            final int deviceId = i;
            devices[i] = new Thread(() -> {
                DeviceSimulator device = new DeviceSimulator(host, port);
                try {
                    device.connect();
                    String readResponse = device.sendRead("Sensor" + deviceId);
                    System.out.println("Device " + deviceId + ": " + readResponse);
                    String writeResponse = device.sendWrite("Sensor" + deviceId + "=100");
                    System.out.println("Device " + deviceId + ": " + writeResponse);
                } catch (IOException e) {
                    System.err.println("Device " + deviceId + " error: " + e.getMessage());
                } finally {
                    try {
                        device.disconnect();
                    } catch (IOException e) {
                        System.err.println("Device " + deviceId + " disconnect error: " + e.getMessage());
                    }
                }
            });
            devices[i].start();
        }

        for (Thread device : devices) {
            device.join();
        }
    }
}