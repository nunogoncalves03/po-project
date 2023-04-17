package prr.terminals;

import java.io.Serializable;
import prr.exceptions.TerminalStateAlreadyOn;
import prr.exceptions.TerminalStateAlreadySilent;
import prr.exceptions.TerminalStateAlreadyOff;
import prr.exceptions.DestinationTerminalIsOff;
import prr.exceptions.DestinationTerminalIsBusy;
import prr.exceptions.DestinationTerminalIsSilent;

public abstract class TerminalState implements Serializable {
    private Terminal _terminal;

    public TerminalState(Terminal terminal) {
        _terminal = terminal;
    }

    public Terminal getTerminal() {
        return _terminal;
    }

    public abstract void transitToIdle() throws TerminalStateAlreadyOn;
    public abstract void transitToSilent() throws TerminalStateAlreadySilent;
    public abstract void transitToBusy();
    public abstract void transitToOff() throws TerminalStateAlreadyOff;
    public abstract void transitToPrevious();
    public abstract boolean canStartCommunication();
    public abstract boolean canSendTextCommunication();
    public abstract boolean canReceiveTextCommunication()
                            throws DestinationTerminalIsOff;
    public abstract boolean canReceiveInteractiveCommunication()
                            throws DestinationTerminalIsOff,
                                   DestinationTerminalIsBusy,
                                   DestinationTerminalIsSilent;
}
