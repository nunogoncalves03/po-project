package prr.communications;

import java.io.Serializable;
import prr.terminals.Terminal;

public abstract class Communication implements Serializable {
    private int _key;
    private boolean _inProgress;
    private double _cost = 0;
    private boolean _paid = false;
    private Terminal _sourceTerminal;
    private Terminal _destinationTerminal;

    public Communication(int key, boolean inProgress, Terminal sourceTerminal,
                         Terminal destinationTerminal) {

        _key = key;
        _inProgress = inProgress;
        _sourceTerminal = sourceTerminal; 
        _destinationTerminal = destinationTerminal;
    }

    public int getKey() {
        return _key;
    }

    public double getCost() {
        return _cost;
    }

    public boolean getInProgress() {
        return _inProgress;
    }

    public void setInProgress(boolean inProgress) {
        _inProgress = inProgress;
    }

    public void setCost(double cost) {
        _cost = cost;
    }

    public boolean getPaid() {
        return _paid;
    }

    public Terminal getSourceTerminal() {
        return _sourceTerminal;
    }

    public Terminal getDestinationTerminal() {
        return _destinationTerminal;
    }

    public boolean isFriend() {
        return _sourceTerminal.isFriend(_destinationTerminal);
    }

    public double pay() {
        _paid = true;
        return _cost;
    }

    public void endCommunication(double cost) {
        _cost = cost;
        _inProgress = false;
        _sourceTerminal.endOnGoingCommunication();
        _destinationTerminal.endOnGoingCommunication();
    }

    @Override
    public String toString() {
        return String.format("%d|%s|%s|", _key, _sourceTerminal.getKey(),
                                          _destinationTerminal.getKey());
    }
}
