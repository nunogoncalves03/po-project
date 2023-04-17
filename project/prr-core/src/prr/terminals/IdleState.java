package prr.terminals;

import prr.exceptions.TerminalStateAlreadyOn;
import prr.exceptions.DestinationTerminalIsOff;
import prr.exceptions.DestinationTerminalIsBusy;
import prr.exceptions.DestinationTerminalIsSilent;

public class IdleState extends TerminalState {
    
    public IdleState(Terminal terminal) {
        super(terminal);
    }

    @Override
    public void transitToIdle() throws TerminalStateAlreadyOn {
        throw new TerminalStateAlreadyOn();
    };

    @Override
    public void transitToSilent() {
        Terminal terminal = super.getTerminal();
        terminal.setState(new SilentState(terminal));
    };

    @Override
    public void transitToBusy() {
        Terminal terminal = super.getTerminal();
        terminal.setState(new BusyState(terminal, this));
    };

    @Override
    public void transitToOff() {
        Terminal terminal = super.getTerminal();
        terminal.setState(new OffState(terminal));
    };

    @Override
    public void transitToPrevious() {
        // EMPTY
    }

    @Override
    public boolean canStartCommunication() {
        return true;
    }

    @Override
    public boolean canSendTextCommunication() {
        return true;
    }

    @Override
    public boolean canReceiveTextCommunication() throws DestinationTerminalIsOff {
        return true;
    }

    @Override
    public boolean canReceiveInteractiveCommunication()
                   throws DestinationTerminalIsOff, DestinationTerminalIsBusy,
                          DestinationTerminalIsSilent {
                            
        return true;
    }

    @Override
    public String toString() {
        return "IDLE";
    }
    
}
