package prr.clients;

public class OffToIdleNotification extends Notification {
    
    public OffToIdleNotification(String terminalKey) {
        super(terminalKey);
    }

    @Override
    public String toString() {
        return "O2I|" + super.getTerminalKey();
    }
}
