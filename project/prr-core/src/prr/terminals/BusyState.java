package prr.terminals;

import prr.exceptions.DestinationTerminalIsOff;
import prr.exceptions.DestinationTerminalIsBusy;
import prr.exceptions.DestinationTerminalIsSilent;

public class BusyState extends TerminalState {
    private TerminalState _previousState;

    public BusyState(Terminal terminal, TerminalState previousState) {
        super(terminal);
        _previousState = previousState;
    }

    @Override
    public void transitToIdle() {
        // EMPTY
    };

    @Override
    public void transitToSilent() {
        // EMPTY
    };

    @Override
    public void transitToBusy() {
        // EMPTY
    };

    @Override
    public void transitToOff() {
        // EMPTY
    };

    @Override
    public void transitToPrevious() {
        Terminal terminal = super.getTerminal();
        terminal.setState(_previousState);
    }

    @Override
    public boolean canStartCommunication() {
        return false;
    }

    @Override
    public boolean canSendTextCommunication() {
        return false;
    }

    @Override
    public boolean canReceiveTextCommunication() throws DestinationTerminalIsOff {
        return true;
    }

    @Override
    public boolean canReceiveInteractiveCommunication()
                   throws DestinationTerminalIsOff, DestinationTerminalIsBusy,
                          DestinationTerminalIsSilent {
                            
        throw new DestinationTerminalIsBusy(super.getTerminal().getKey());
    }

    @Override
    public String toString() {
        return "BUSY";
    }
    
}
