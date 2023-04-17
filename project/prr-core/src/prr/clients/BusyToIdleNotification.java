package prr.clients;

public class BusyToIdleNotification extends Notification {
    
    public BusyToIdleNotification(String terminalKey) {
        super(terminalKey);
    }

    @Override
    public String toString() {
        return "B2I|" + super.getTerminalKey();
    }
}
