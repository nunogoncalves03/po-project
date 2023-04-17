package prr.exceptions;

public class UnknownTerminalKeyException extends Exception {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202208091753L;

    private final String _key;

    public UnknownTerminalKeyException(String key) {
        _key = key;
    }

    public String getKey() {
        return _key;
    }
    
}
