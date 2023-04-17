package prr.exceptions;

public class CommunicationUnsupportedAtOrigin extends Exception {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202208091753L;

    private final String _key;
    private final String _type;

    public CommunicationUnsupportedAtOrigin(String key, String type) {
        _key = key;
        _type = type;
    }

    public String getKey() {
        return _key;
    }

    public String getType() {
        return _type;
    }
}
