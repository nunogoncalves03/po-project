package prr.clients;

public class SilentToIdleNotification extends Notification {
    
    public SilentToIdleNotification(String terminalKey) {
        super(terminalKey);
    }

    @Override
    public String toString() {
        return "S2I|" + super.getTerminalKey();
    }
}
