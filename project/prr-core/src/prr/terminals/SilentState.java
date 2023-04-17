package prr.terminals;

import prr.clients.SilentToIdleNotification;
import prr.exceptions.TerminalStateAlreadySilent;
import prr.exceptions.DestinationTerminalIsOff;
import prr.exceptions.DestinationTerminalIsBusy;
import prr.exceptions.DestinationTerminalIsSilent;

public class SilentState extends TerminalState {
    
    public SilentState(Terminal terminal) {
        super(terminal);
    }

    @Override
    public void transitToIdle() {
        Terminal terminal = super.getTerminal();
        terminal.setState(new IdleState(terminal));
        terminal.deliverNotifications(new SilentToIdleNotification(terminal.getKey()));
    };

    @Override
    public void transitToSilent() throws TerminalStateAlreadySilent {
        throw new TerminalStateAlreadySilent();
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
    public boolean canReceiveTextCommunication() {
        return true;
    }

    @Override
    public boolean canReceiveInteractiveCommunication()
                   throws DestinationTerminalIsOff, DestinationTerminalIsBusy,
                          DestinationTerminalIsSilent {

        throw new DestinationTerminalIsSilent(super.getTerminal().getKey());
    }

    @Override
    public String toString() {
        return "SILENCE";
    }
    
}
