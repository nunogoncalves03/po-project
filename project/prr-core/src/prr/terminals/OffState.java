package prr.terminals;

import prr.clients.OffToIdleNotification;
import prr.clients.OffToSilentNotification;
import prr.exceptions.TerminalStateAlreadyOff;
import prr.exceptions.DestinationTerminalIsOff;
import prr.exceptions.DestinationTerminalIsBusy;
import prr.exceptions.DestinationTerminalIsSilent;

public class OffState extends TerminalState {
    
    public OffState(Terminal terminal) {
        super(terminal);
    }

    @Override
    public void transitToIdle() {
        Terminal terminal = super.getTerminal();
        terminal.setState(new IdleState(terminal));
        terminal.deliverNotifications(new OffToIdleNotification(terminal.getKey()));
    };

    @Override
    public void transitToSilent() {
        Terminal terminal = super.getTerminal();
        terminal.setState(new SilentState(terminal));
        terminal.deliverNotifications(new OffToSilentNotification(terminal.getKey()));
    };

    @Override
    public void transitToBusy() {
        // EMPTY
    };

    @Override
    public void transitToOff() throws TerminalStateAlreadyOff {
        throw new TerminalStateAlreadyOff();
    };

    @Override
    public void transitToPrevious() {
        // EMPTY
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
        throw new DestinationTerminalIsOff(super.getTerminal().getKey());
    }

    @Override
    public boolean canReceiveInteractiveCommunication() throws DestinationTerminalIsOff,
    DestinationTerminalIsBusy, DestinationTerminalIsSilent {
        throw new DestinationTerminalIsOff(super.getTerminal().getKey());
    }

    @Override
    public String toString() {
        return "OFF";
    }
    
}
