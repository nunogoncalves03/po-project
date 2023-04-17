package prr.exceptions;

public class DestinationTerminalIsOff extends Exception {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202208091753L;

    private final String _key;

    public DestinationTerminalIsOff(String key) {
        _key = key;
    }

    public String getKey() {
        return _key;
    }
}
