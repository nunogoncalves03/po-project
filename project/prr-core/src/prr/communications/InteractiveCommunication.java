package prr.communications;

import prr.terminals.Terminal;

public abstract class InteractiveCommunication extends Communication {
    private long _duration = 0;

    public InteractiveCommunication(int key, Terminal sourceTerminal,
                                    Terminal destinationTerminal) {
                                        
        super(key, true, sourceTerminal, destinationTerminal);
    }

    public long getDuration() {
        return _duration;
    }

    public void setDuration(long duration) {
        _duration = duration;
    }

    public Terminal getSourceTerminal() {
        return super.getSourceTerminal();
    }

    public abstract double endInteractiveComm(long duration);
}
