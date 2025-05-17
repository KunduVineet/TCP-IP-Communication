# **Device Communication System**

---

## **Overview**

This Java-based client-server application enables real-time data communication with multiple devices in a manufacturing plant over **TCP/IP**. It supports various communication protocols for reading and writing data, handles simultaneous device connections, and allows for future protocol extensions. The system includes a **server**, **client simulator**, **protocol implementations**, and **unit tests**, fully meeting the assignment requirements.

### **Assignment Requirements:**

* Supports TCP/IP communication (IP address and port number) for reading and writing values.
* Communicates with multiple devices simultaneously.
* Supports future additions of communication protocols.
* Provides a stub to test TCP/IP communication.
* Includes program/code-driven unit test cases.

This project fulfills all requirements through a **modular design**, **robust error handling**, and **comprehensive testing**.

---

## **Functionalities**

### **1. TCP/IP Communication (Requirement a)**

* Utilizes TCP/IP sockets for communication between the **server** (`DeviceServer`) and **clients** (`DeviceSimulator`).
* The server listens on a configurable port (default: `8080`) at a specified IP address (default: `localhost`).
* Clients connect using the host and port, sending `READ` and `WRITE` commands and receiving responses.

### **2. Simultaneous Device Communication (Requirement b)**

* The server handles multiple device connections concurrently by spawning a `DeviceHandler` thread for each client.
* The `DeviceSimulator` can simulate multiple devices (default: 3) connecting simultaneously, each sending read and write commands.
* **Thread safety** is ensured through synchronized methods and proper socket handling.

### **3. Future Protocol Support (Requirement c)**

* Uses an `IProtocol` interface to define protocol behavior, enabling easy addition of new protocols.
* Includes a `SimpleProtocol` implementation that processes read/write commands (e.g., returns `READ:- Sensor1`).
* A `ProtocolFactory` supports **dynamic protocol selection** via `config.properties`, accommodating future protocols like **Modbus** or **OPC UA**.

### **4. TCP/IP Communication Stub (Requirement d)**

* The `DeviceSimulator` serves as a **client stub** to test TCP/IP communication.
* It connects to the server, sends `READ` and `WRITE` commands, and prints responses, simulating real device behavior.
* Supports configurable **host**, **port**, and **number of devices** for versatile testing.

### **5. Unit Tests (Requirement e)**

* Includes **JUnit 5** unit tests in `DeviceServerTest` to verify server functionality.

**Tests cover:**

* Single device communication (sending read and write commands).
* Multiple device communication (concurrent connections and responses).

Tests use **dynamic port allocation** to avoid conflicts and ensure robust setup/teardown.

---

## **Prerequisites**

* **Java:** JDK 17 or later (tested with JDK 22).
* **Maven:** 3.8 or later for building and running tests.
* **IDE:** Optional (e.g., IntelliJ IDEA).
* **Git:** For cloning the repository.

---

## **Project Structure**

```
device-communication/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── client/
│   │   │   │   └── DeviceSimulator.java      # Client stub to simulate devices
│   │   │   ├── protocol/
│   │   │   │   └── IProtocol.java            # Protocol interface
│   │   │   │   └── SimpleProtocol.java       # Basic protocol implementation
│   │   │   │   └── ModbusProtocol.java       # Example protocol
│   │   │   │   └── ProtocolFactory.java      # Factory for protocol selection
│   │   │   ├── server/
│   │   │   │   └── DeviceServer.java         # TCP/IP server
│   │   │   │   └── DeviceHandler.java        # Handles client connections
│   ├── resources/
│   │   └── config.properties                 # Configuration file
├── test/
│   └── java/
│       └── server/
│           └── DeviceServerTest.java        # Unit tests
├── pom.xml                                  # Maven build file
├── README.md                                # Project documentation
```

---

## **Key Components**

* **DeviceServer:** Listens for client connections and spawns `DeviceHandler` threads.
* **DeviceHandler:** Processes client messages (`READ` or `WRITE`) using the selected protocol.
* **DeviceSimulator:** Simulates multiple devices, sending read/write commands.
* **IProtocol:** Defines the protocol contract (`read` and `write` methods).
* **SimpleProtocol:** Prepends `READ:-` or `WRITE:-` to input data.
* **ModbusProtocol:** A stub for a Modbus-like protocol, showing extensibility.
* **ProtocolFactory:** Loads protocols based on `config.properties`.
* **DeviceServerTest:** JUnit tests for single and multiple device communication.

---

## **Setup and Running Instructions**

### **1. Clone the Repository**

```bash
git clone <repository-url>
cd device-communication
```

### **2. Build the Project**

Compile and package using Maven:

```bash
mvn clean install
```

### **3. Configure the Application**

Edit `src/main/resources/config.properties`:

```properties
server.host=localhost
serversocket.port=8080
protocol=simple
client.devices=3
```

* `server.host`: Server hostname (default: `localhost`).
* `serversocket.port`: Server port (default: `8080`).
* `protocol`: Protocol to use (`simple` or `modbus`).
* `client.devices`: Number of simulated devices (default: `3`).

### **4. Run the Server**

```bash
java -cp target/classes server.DeviceServer
```

**Expected Output:**

```
Server started on port: 8080
New Device Connected: 127.0.0.1
New Device Connected: 127.0.0.1
New Device Connected: 127.0.0.1
```

### **5. Run the Client**

```bash
java -cp target/classes client.DeviceSimulator
```

**Expected Output (for protocol=simple, devices=3):**

```
Connected to server at localhost:8080
Device 0: READ:- Sensor0
Device 0: WRITE:- Sensor0=100
Device 1: READ:- Sensor1
Device 1: WRITE:- Sensor1=100
Device 2: READ:- Sensor2
Device 2: WRITE:- Sensor2=100
```

### **6. Run Unit Tests**

```bash
mvn test
```

**Expected Output:**

```
[INFO] Running server.DeviceServerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## **Testing Details**

The `DeviceServerTest` class includes:

* `testSingleDeviceCommunication`: Verifies a single device can connect and communicate.
* `testMultipleDevices`: Verifies concurrent communication with three devices.

**Tests use:**

* Dynamic port allocation.
* `CountDownLatch` for thread synchronization.
* Robust setup/teardown to manage server lifecycle.

---

## **Extending the System**

To add a new protocol (e.g., OPC UA):

### **1. Create the Protocol Class**

```java
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
```

### **2. Register in ProtocolFactory**

```java
PROTOCOLS.put("opcua", new OPCUAProtocol());
```

### **3. Update config.properties**

```properties
protocol=opcua
```

### **4. Rebuild and Run**

```bash
mvn clean install
java -cp target/classes server.DeviceServer
java -cp target/classes client.DeviceSimulator
```

---

## **Notes for Interviewers**

This project demonstrates:

* **TCP/IP Expertise:** Socket programming with error handling.
* **Concurrency:** Multi-threaded server with synchronized handling.
* **Extensibility:** Modular design using interfaces and factories.
* **Testing:** Comprehensive JUnit 5 tests.
* **Code Quality:** Clean structure, Javadoc, modular components.
* **Configuration Flexibility:** Easy setup via properties file.

**Bonus Features:**

* Dynamic protocol selection.
* Robust error handling.
* Thread-safe client ops.
* Dynamic test port allocation.

---

## **Known Limitations and Potential Improvements**

* **Text-Based Protocols:** Binary protocols (e.g., Modbus RTU) would need `DataInputStream/DataOutputStream`.
* **Logging:** Replace `System.out` with SLF4J or Logback.
* **Security:** No encryption/authentication (for internal use only).
* **Persistence:** Message logging/auditing not included but feasible.

---

## **Conclusion**

This project provides a complete, extensible, and well-tested solution for device communication in a manufacturing setup. It demonstrates strong Java skills, including **networking**, **threading**, **protocol abstraction**, and **automated testing**.

> For any queries or improvements, contact the repository owner or open an issue.


