package prr.terminals;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import prr.Network;
import prr.clients.Client;
import prr.clients.Notification;
import prr.communications.Communication;
import prr.communications.InteractiveCommunication;
import prr.communications.TextCommunication;
import prr.exceptions.CommunicationUnsupportedAtDestination;
import prr.exceptions.CommunicationUnsupportedAtOrigin;
import prr.exceptions.DestinationTerminalIsBusy;
import prr.exceptions.DestinationTerminalIsOff;
import prr.exceptions.DestinationTerminalIsSilent;
import prr.exceptions.InvalidCommunicationKey;
import prr.exceptions.NoOngoingCommunication;
import prr.exceptions.TerminalStateAlreadyOff;
import prr.exceptions.TerminalStateAlreadyOn;
import prr.exceptions.TerminalStateAlreadySilent;
import prr.exceptions.UnknownTerminalKeyException;

/**
 * Abstract terminal.
 */
public abstract class Terminal implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202208091753L;

    private String _key;
    private Client _client;
    private TerminalState _state;
    private double _payments = 0;
    private double _debts = 0;
    private Map<String, Terminal> _friends = new TreeMap<String, Terminal>();
    private Map<Integer, Communication> _communications = new HashMap<Integer, Communication>();
    private InteractiveCommunication _onGoingCommunication = null;
    private Set<Client> _contactAttempts = new HashSet<Client>();

    public Terminal(String key, Client client, String state) {
        _key = key;
        _client = client;
        selectState(state);
    }

    public Client getClient() {
        return _client;
    }

    public String getKey() {
        return _key;
    }

    TerminalState getState() {
        return _state;
    }

    public double getPayments() {
        return _payments;
    }

    public double getDebts() {
        return _debts;
    }

    public void setState(TerminalState state) {
        _state = state;
    }

    public Collection<Communication> getStartedCommunications() {
        return _communications
                .values()
                .stream()
                .filter(comm -> this.equals(comm.getSourceTerminal()))
                .collect(Collectors.toList());
    }

    public Collection<Communication> getReceivedCommunications() {
        return _communications
                .values()
                .stream()
                .filter(comm -> this.equals(comm.getDestinationTerminal()))
                .collect(Collectors.toList());
    }

    public boolean isUnused() {
        return _communications.size() == 0;
    }

    public boolean isFriend(Terminal terminal) {
        return _friends.get(terminal.getKey()) != null;
    }

    public boolean hasPositiveBalance() {
        return _payments > _debts;
    }

    public void selectState(String state) {
        switch (state) {
            case "ON" -> _state = new IdleState(this);
            case "OFF" -> _state = new OffState(this);
            case "SILENCE" -> _state = new SilentState(this);
            case "BUSY" -> _state = new BusyState(this, null);
        }
    }

    /**
     * Checks if this terminal can end the current interactive communication.
     *
     * @return true if this terminal is busy (i.e., it has an active
     *         interactive communication) and it was the originator of
     *         this communication.
     **/
    public boolean canEndCurrentCommunication() {
        return ((_onGoingCommunication == null ? false : true) &&
                this.equals(_onGoingCommunication.getSourceTerminal()));
    }

    /**
     * Checks if this terminal can start a new communication.
     *
     * @return true if this terminal is neither off neither busy,
     *         false otherwise.
     **/
    public boolean canStartCommunication() {
        return _state.canStartCommunication();
    }

    public void turnOn() throws TerminalStateAlreadyOn {
        _state.transitToIdle();
    }

    public void turnOff() throws TerminalStateAlreadyOff {
        _state.transitToOff();
    }

    public void silence() throws TerminalStateAlreadySilent {
        _state.transitToSilent();
    }

    public void addFriend(Network network, String friendKey)
            throws UnknownTerminalKeyException {
        Terminal friend = network.getTerminal(friendKey);

        if (_key.equals(friendKey) || _friends.get(friendKey) != null) {
            return;
        }
        _friends.put(friendKey, friend);
        network.setChanged(true);
    }

    public void removeFriend(Network network, String friendKey)
            throws UnknownTerminalKeyException {
        
        Terminal friend = network.getTerminal(friendKey);

        if (_friends.get(friendKey) == null) {
            return;
        }
        _friends.remove(friendKey);
        network.setChanged(true);
    }

    public boolean canReceiveTextCommunication() throws DestinationTerminalIsOff {
        return _state.canReceiveTextCommunication();
    }

    public boolean canReceiveInteractiveCommunication()
            throws DestinationTerminalIsOff, DestinationTerminalIsBusy, DestinationTerminalIsSilent {
        return _state.canReceiveInteractiveCommunication();
    }

    public void receiveTextCommunication(Communication communication) {
        _communications.put(communication.getKey(), communication);
    }

    public void receiveInteractiveCommunication(
            InteractiveCommunication communication) {
        _communications.put(communication.getKey(), communication);
        _onGoingCommunication = communication;
        _state.transitToBusy();
    }

    public InteractiveCommunication getOnGoingCommunication() throws NoOngoingCommunication {
        if (_onGoingCommunication == null) {
            throw new NoOngoingCommunication();
        }

        return _onGoingCommunication;
    }

    public void endOnGoingCommunication() {
        _onGoingCommunication = null;
        transitToPreviousState();
    }

    public void transitToPreviousState() {
        _state.transitToPrevious();
    }

    public double calculateTextCommCost(TextCommunication comm) {
        return _client.calculateTextCommCost(comm);
    }

    public double calculateVoiceCommCost(InteractiveCommunication comm) {
        return _client.calculateVoiceCommCost(comm);
    }

    public double calculateVideoCommCost(InteractiveCommunication comm) {
        return _client.calculateVideoCommCost(comm);
    }

    public void payCommunication(Network network, int key)
            throws InvalidCommunicationKey {
        Communication communication = _communications.get(key);

        if (communication == null ||
                !this.equals(communication.getSourceTerminal()) ||
                communication.getInProgress() ||
                communication.getPaid()) {
            throw new InvalidCommunicationKey();
        }

        double cost = communication.pay();
        _client.performPayment(cost);
        _payments += cost;
        _debts -= cost;
        network.setChanged(true);
    }

    public void sendTextCommunication(Network network, String destinationKey,
            String message) throws UnknownTerminalKeyException, DestinationTerminalIsOff {
        Terminal destination = network.getTerminal(destinationKey);

        try {
            destination.canReceiveTextCommunication();
        } catch (DestinationTerminalIsOff e) {
            if (_client.canReceiveNotifications()) {
                destination.registerContactAttempt(_client);
                network.setChanged(true);
            }
            throw e;
        }

        TextCommunication communication = network.registerTextCommunication(this, destination, message);

        _communications.put(communication.getKey(), communication);
        destination.receiveTextCommunication(communication);
        double cost = calculateTextCommCost(communication);
        _debts += cost;
        communication.endCommunication(cost);
        network.setChanged(true);
    }

    public void startInteractiveCommunication(
            Network network,
            String destinationKey,
            String type)
            throws UnknownTerminalKeyException, CommunicationUnsupportedAtOrigin, CommunicationUnsupportedAtDestination,
            DestinationTerminalIsOff, DestinationTerminalIsBusy, DestinationTerminalIsSilent {
        Terminal destination = network.getTerminal(destinationKey);

        if (!supportInteractiveCommunication(type)) {
            throw new CommunicationUnsupportedAtOrigin(_key, type);
        }

        if (!destination.supportInteractiveCommunication(type)) {
            throw new CommunicationUnsupportedAtDestination(destinationKey, type);
        }

        if (this.equals(destination)) {
            throw new DestinationTerminalIsBusy(_key);
        }

        try {
            destination.canReceiveInteractiveCommunication();
        } catch (DestinationTerminalIsOff | DestinationTerminalIsBusy | DestinationTerminalIsSilent e) {
            if (_client.canReceiveNotifications()) {
                destination.registerContactAttempt(_client);
                network.setChanged(true);
            }
            throw e;
        }

        InteractiveCommunication communication = network.registerInteractiveCommunication(this, destination, type);
        _communications.put(communication.getKey(), communication);
        _onGoingCommunication = communication;
        _state.transitToBusy();
        destination.receiveInteractiveCommunication(communication);
        network.setChanged(true);
    }

    public double endInteractiveCommunication(Network network, int duration) {
        double cost = _onGoingCommunication.endInteractiveComm(duration);
        _debts += cost;
        network.setChanged(true);
        return cost;
    }

    public void registerContactAttempt(Client client) {
        _contactAttempts.add(client);
    }

    public void deliverNotifications(Notification notification) {
        _contactAttempts.forEach((client) -> client.deliverNotification(notification));
        _contactAttempts.clear();
    }

    public abstract boolean supportInteractiveCommunication(String type);

    @Override
    public String toString() {
        return (String.format(
                "%s|%s|%s|%d|%d",
                _key,
                _client.getKey(),
                _state.toString(),
                Math.round(_payments),
                Math.round(_debts)) +
                (_friends.size() > 0 ? "|" + String.join(",", _friends.keySet()) : ""));
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Terminal) {
            Terminal terminal = (Terminal) object;
            return _key.equals(terminal.getKey());
        }
        return false;
    }
}
