package server;

import Client.DeviceSimulator;
import Server.DeviceServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Protocol.IProtocol;
import Protocol.SimpleProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class DeviceServerTest {
    private DeviceServer server;
    private int port;
    private Thread serverThread;

    @BeforeEach
    void setUp() throws IOException {
        // Find a free port dynamically
        try (ServerSocket tempSocket = new ServerSocket(0)) {
            port = tempSocket.getLocalPort();
        }
        IProtocol protocol = new SimpleProtocol();
        server = new DeviceServer(port, protocol);
        serverThread = new Thread(() -> server.start());
        serverThread.start();

        // Wait for server to start by attempting a connection
        int retries = 10;
        while (retries-- > 0) {
            try {
                new DeviceSimulator("localhost", port).connect();
                break;
            } catch (IOException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        if (retries <= 0) {
            fail("Server failed to start within timeout");
        }
    }

    @AfterEach
    void tearDown() {
        server.stop();
        try {
            serverThread.join(1000);
            if (serverThread.isAlive()) {
                serverThread.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSingleDeviceCommunication() throws IOException {
        DeviceSimulator device = new DeviceSimulator("localhost", port);
        try {
            device.connect();
            String readResponse = device.sendRead("TestSensor");
            assertEquals("READ_RESPONSE: TESTSENSOR", readResponse);
            String writeResponse = device.sendWrite("TestSensor=100");
            assertEquals("WRITE_ACK: TestSensor=100", writeResponse);
        } finally {
            device.disconnect();
        }
    }

    @Test
    void testMultipleDevices() throws InterruptedException {
        int deviceCount = 3;
        List<Thread> devices = new ArrayList<>();
        List<String> readResponses = new ArrayList<>();
        List<String> writeResponses = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(deviceCount);

        for (int i = 0; i < deviceCount; i++) {
            final int id = i;
            Thread deviceThread = new Thread(() -> {
                DeviceSimulator device = new DeviceSimulator("localhost", port);
                try {
                    device.connect();
                    String readResponse = device.sendRead("Sensor" + id);
                    String writeResponse = device.sendWrite("Sensor" + id + "=100");
                    synchronized (readResponses) {
                        while (readResponses.size() <= id) {
                            readResponses.add(null);
                        }
                        readResponses.set(id, readResponse);
                    }
                    synchronized (writeResponses) {
                        while (writeResponses.size() <= id) {
                            writeResponses.add(null);
                        }
                        writeResponses.set(id, writeResponse);
                    }
                } catch (IOException e) {
                    fail("Device " + id + " failed: " + e.getMessage());
                } finally {
                    try {
                        device.disconnect();
                    } catch (IOException e) {
                        // Ignore
                    }
                    latch.countDown();
                }
            });
            devices.add(deviceThread);
            deviceThread.start();
        }

        // Wait for all devices to complete
        latch.await(5, java.util.concurrent.TimeUnit.SECONDS);

        for (Thread device : devices) {
            device.join(1000);
        }

        // Verify responses
        // Verify responses
        for (int i = 0; i < deviceCount; i++) {
            assertNotNull(readResponses.get(i), "Read response for device " + i + " is null");
            assertNotNull(writeResponses.get(i), "Write response for device " + i + " is null");
            assertEquals("READ_RESPONSE: SENSOR" + i, readResponses.get(i));
            assertEquals("WRITE_ACK: Sensor" + i + "=100", writeResponses.get(i));
        }
    }
}