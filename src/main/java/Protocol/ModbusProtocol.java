package Protocol;

/**
 * A stub implementation of a Modbus-like protocol for demonstration.
 */
public class ModbusProtocol implements IProtocol {
    @Override
    public String read(String input) {
        if (input == null || !input.matches("REG\\d+")) {
            return "ERROR: Invalid register format (e.g., REG123)";
        }
        return "MODBUS_READ: " + input + "=0x" + Integer.toHexString(input.hashCode() % 65536);
    }

    @Override
    public String write(String data) {
        if (data == null || !data.matches("REG\\d+=\\d+")) {
            return "ERROR: Invalid write format (e.g., REG123=456)";
        }
        return "MODBUS_WRITE: " + data + " OK";
    }
}