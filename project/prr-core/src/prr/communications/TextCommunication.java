package prr.communications;

import prr.terminals.Terminal;

public class TextCommunication extends Communication {
    private String _message = "";

    public TextCommunication(int key, Terminal sourceTerminal,
                            Terminal destinationTerminal, String message) {
                                
        super(key, false, sourceTerminal, destinationTerminal);
        _message = message;
    }

    public String getMessage() {
        return _message;
    }

    @Override
    public String toString() {
        return "TEXT|" + super.toString() + String.format("%d|%d|%s",
                        _message.length(),
                        Math.round(super.getCost()),
                        super.getInProgress() ? "ONGOING" : "FINISHED");
    }
}
