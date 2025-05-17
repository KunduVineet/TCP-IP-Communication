package Server;

import Protocol.IProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DeviceHandler implements Runnable {
    private final Socket clientSocket;
    private final IProtocol protocol;

    public DeviceHandler(Socket clientSocket, IProtocol protocol) {
        this.clientSocket = clientSocket;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("READ")) {
                    String response = protocol.read(inputLine.substring(5));
                    out.println(response);
                } else if (inputLine.startsWith("WRITE")) {
                    String response = protocol.write(inputLine.substring(6));
                    out.println(response); // Send response for WRITE
                } else {
                    out.println("ERROR: Invalid Command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}