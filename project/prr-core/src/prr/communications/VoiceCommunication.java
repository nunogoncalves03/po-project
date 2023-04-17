package prr.communications;

import prr.terminals.Terminal;

public class VoiceCommunication extends InteractiveCommunication {

    public VoiceCommunication(int key, Terminal sourceTerminal,
                              Terminal destinationTerminal) {
                                
        super(key, sourceTerminal, destinationTerminal);
    }

    public double endInteractiveComm(long duration) {
        super.setDuration(duration);
        double cost = super.getSourceTerminal().calculateVoiceCommCost(this);
        super.endCommunication(cost);
        return cost;
    }

    @Override
    public String toString() {
        return "VOICE|" + super.toString() + String.format("%d|%d|%s",
                        super.getDuration(),
                        Math.round(super.getCost()),
                        super.getInProgress() ? "ONGOING" : "FINISHED");
    }
}
