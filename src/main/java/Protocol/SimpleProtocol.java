package Protocol;


/**
 * A simple protocol implementation that simulates basic read/write operations.
 */
public class SimpleProtocol implements IProtocol {
    @Override
    public String read(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "ERROR: Invalid read input";
        }
        return "READ_RESPONSE: " + input.toUpperCase();
    }

    @Override
    public String write(String data) {
        if (data == null || data.trim().isEmpty()) {
            return "ERROR: Invalid write data";
        }
        return "WRITE_ACK: " + data;
    }
}