Device Communication System

Overview
This Java-based client-server application enables real-time data communication with multiple devices in a manufacturing plant over TCP/IP. It supports various communication protocols for reading and writing data, handles simultaneous device connections, and allows for future protocol extensions. The system includes a server, client simulator, protocol implementations, and unit tests, fully meeting the assignment requirements.
The assignment required a program that:

Supports TCP/IP communication (IP address and port number) for reading and writing values.
Communicates with multiple devices simultaneously.
Supports future additions of communication protocols.
Provides a stub to test TCP/IP communication.
Includes program/code-driven unit test cases.

This project fulfills all requirements through a modular design, robust error handling, and comprehensive testing.

Functionalities
1. TCP/IP Communication (Requirement a)

Utilizes TCP/IP sockets for communication between the server (DeviceServer) and clients (DeviceSimulator).
The server listens on a configurable port (default: 8080) at a specified IP address (default: localhost).
Clients connect using the host and port, sending READ and WRITE commands and receiving responses.

2. Simultaneous Device Communication (Requirement b)

The server handles multiple device connections concurrently by spawning a DeviceHandler thread for each client.
The DeviceSimulator can simulate multiple devices (default: 3) connecting simultaneously, each sending read and write commands.
Thread safety is ensured through synchronized methods in DeviceSimulator and proper socket handling.

3. Future Protocol Support (Requirement c)

Uses an IProtocol interface to define protocol behavior, enabling easy addition of new protocols.
Includes a SimpleProtocol implementation that processes read and write commands (e.g., returns READ:- Sensor1).
A ProtocolFactory supports dynamic protocol selection via config.properties, accommodating future protocols like Modbus or OPC UA.

4. TCP/IP Communication Stub (Requirement d)

The DeviceSimulator serves as a client stub to test TCP/IP communication.
It connects to the server, sends READ and WRITE commands, and prints responses, simulating real device behavior.
Supports configurable host, port, and number of devices for versatile testing.

5. Unit Tests (Requirement e)

Includes JUnit 5 unit tests in DeviceServerTest to verify server functionality.
Tests cover:
Single device communication (sending read and write commands).
Multiple device communication (concurrent connections and responses).


Tests use dynamic port allocation to avoid conflicts and ensure robust setup/teardown.


Prerequisites

Java: JDK 17 or later (tested with JDK 22).
Maven: 3.8 or later for building and running tests.
IDE: Optional (e.g., IntelliJ IDEA for easier execution).
Git: For cloning the repository.


Project Structure
device-communication/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── client/
│   │   │   │   └── DeviceSimulator.java    # Client stub to simulate devices
│   │   │   ├── protocol/
│   │   │   │   └── IProtocol.java          # Protocol interface
│   │   │   │   └── SimpleProtocol.java     # Basic protocol implementation
│   │   │   │   └── ModbusProtocol.java     # Example protocol for extensibility
│   │   │   │   └── ProtocolFactory.java    # Factory for protocol selection
│   │   │   ├── server/
│   │   │   │   └── DeviceServer.java       # TCP/IP server
│   │   │   │   └── DeviceHandler.java      # Handles client connections
│   │   ├── resources/
│   │   │   └── config.properties           # Configuration file
│   ├── test/
│   │   ├── java/
│   │   │   ├── server/
│   │   │   │   └── DeviceServerTest.java   # Unit tests
├── pom.xml                                     # Maven build file
├── README.md                                   # This file

Key Components:

DeviceServer: Listens for client connections and spawns DeviceHandler threads.
DeviceHandler: Processes client messages (READ or WRITE) using the selected protocol.
DeviceSimulator: Simulates multiple devices, sending read/write commands.
IProtocol: Defines the protocol contract (read and write methods).
SimpleProtocol: Prepends READ:- or WRITE:- to input data.
ModbusProtocol: A stub for a Modbus-like protocol, showing extensibility.
ProtocolFactory: Loads protocols based on config.properties.
DeviceServerTest: JUnit tests for single and multiple device communication.


Setup and Running Instructions
1. Clone the Repository
git clone <repository-url>
cd device-communication

2. Build the ProjectCompile and package using Maven:
mvn clean install

3. Configure the ApplicationEdit src/main/resources/config.properties to customize settings:
server.host=localhost
serversocket.port=8080
protocol=simple
client.devices=3


server.host: Server hostname (default: localhost).
serversocket.port: Server port (default: 8080).
protocol: Protocol to use (simple or modbus).
client.devices: Number of simulated devices (default: 3).

4. Run the ServerStart the server in a terminal or IDE:
java -cp target/classes server.DeviceServer

Expected Output:
Server started on port: 8080
New Device Connected: 127.0.0.1
New Device Connected: 127.0.0.1
New Device Connected: 127.0.0.1

5. Run the ClientIn a separate terminal or IDE, run the client simulator:
java -cp target/classes client.DeviceSimulator

Expected Output (with protocol=simple, client.devices=3):
Connected to server at localhost:8080
Connected to server at localhost:8080
Connected to server at localhost:8080
Device 0: READ:- Sensor0
Device 0: WRITE:- Sensor0=100
Device 1: READ:- Sensor1
Device 1: WRITE:- Sensor1=100
Device 2: READ:- Sensor2
Device 2: WRITE:- Sensor2=100

6. Run Unit TestsExecute unit tests to verify functionality:
mvn test

Expected Output:
[INFO] Running server.DeviceServerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS


Testing Details
The DeviceServerTest class includes:

testSingleDeviceCommunication: Verifies a single device can connect, send read/write commands, and receive responses (e.g., READ:- TestSensor, WRITE:- TestSensor=100).
testMultipleDevices: Tests concurrent communication with three devices, ensuring correct responses.

Tests use:

Dynamic port allocation to avoid conflicts.
A CountDownLatch for thread synchronization.
Robust setup/teardown to manage server lifecycle.


Extending the System
To add a new protocol (e.g., OPC UA):

Create a class implementing IProtocol:
package protocol;

public class OPCUAProtocol implements IProtocol {
    @Override
    public String read(String input) {
        return "OPCUA_READ: " + input;
    }

    @Override
    public String write(String data) {
        return "OPCUA_WRITE: " + data;
    }
}


Register in ProtocolFactory:
PROTOCOLS.put("opcua", new OPCUAProtocol());


Update config.properties:
protocol=opcua


Rebuild and run:
mvn clean install
java -cp target/classes server.DeviceServer
java -cp target/classes client.DeviceSimulator




Notes for Interviewers
This project demonstrates:

TCP/IP Expertise: Robust socket programming with error handling and resource management.
Concurrency: Multi-threaded server handling simultaneous connections.
Extensibility: Modular design with IProtocol and ProtocolFactory for new protocols.
Testing: Comprehensive JUnit 5 tests for core functionality.
Code Quality: Clean, documented code with Javadoc and clear structure.
Configuration: Flexible settings via config.properties.

The project exceeds requirements by including:

Dynamic protocol selection.
Robust error handling (e.g., input validation, socket closure).
Thread-safe client operations.
Dynamic port allocation in tests.


Known Limitations and Potential Improvements

Text-Based Protocols: Supports text-based communication. Binary protocols (e.g., real Modbus) would require DataInputStream/DataOutputStream.
Logging: Uses System.out. A logging framework (e.g., SLF4J) could improve debugging.
Security: No authentication or encryption, suitable for controlled environments.
Message Persistence: Messages are not stored, which could be added for auditing.

These are outside the assignment scope but can be implemented if required.

Conclusion
This project provides a complete, well-tested solution for device communication in a manufacturing plant. It is easy to set up, run, and extend, showcasing strong Java programming, network communication, and software design skills.
For questions or clarification, please contact the repository owner or raise an issue.
