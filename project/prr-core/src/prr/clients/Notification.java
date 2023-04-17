package prr.clients;

import java.io.Serializable;

public class Notification implements Serializable {
    String _terminalKey;

    public Notification(String terminalKey) {
        _terminalKey = terminalKey;
    }

    public String getTerminalKey() {
        return _terminalKey;
    }
}
