package Protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating protocol instances based on configuration.
 */
public class ProtocolFactory {
    private static final Map<String, IProtocol> PROTOCOLS = new HashMap<>();

    static {
        PROTOCOLS.put("simple", new SimpleProtocol());
        PROTOCOLS.put("modbus", new ModbusProtocol());
    }

    /**
     * Gets a protocol instance by name.
     * @param protocolName the protocol name (e.g., "simple", "modbus")
     * @return the protocol instance
     * @throws IllegalArgumentException if protocol is unknown
     */
    public static IProtocol getProtocol(String protocolName) {
        IProtocol protocol = PROTOCOLS.get(protocolName.toLowerCase());
        if (protocol == null) {
            throw new IllegalArgumentException("Unknown protocol: " + protocolName);
        }
        return protocol;
    }
}