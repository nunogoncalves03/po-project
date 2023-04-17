package prr.clients;

public class OffToSilentNotification extends Notification {
    
    public OffToSilentNotification(String terminalKey) {
        super(terminalKey);
    }

    @Override
    public String toString() {
        return "O2S|" + super.getTerminalKey();
    }
}
