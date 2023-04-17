package prr.clients;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

import prr.communications.InteractiveCommunication;
import prr.communications.TextCommunication;
import prr.terminals.Terminal;

public class Client implements Serializable {
    private String _key;
    private String _name;
    private int _taxId;
    private ClientType _type = new NormalType(this);
    private boolean _receiveNotifications = true;
    private Map<String, Terminal> _terminals = new HashMap<String, Terminal>();
    private double _payments = 0;
    private double _debts = 0;
    private List<Notification> _notifications = new LinkedList<Notification>();
    private NotificationDeliveryMethod deliveryMethod = new DefaultDeliveryMethod();

    private class DefaultDeliveryMethod implements NotificationDeliveryMethod {
        public void deliver(Notification notification) {
            _notifications.add(notification);
        }
    }

    public Client(String key, String name, int taxId) {
        _key = key;
        _name = name;
        _taxId = taxId;
    }

    public String getKey() {
        return _key;
    }

    public void setType(ClientType type) {
        _type = type;
    }

    public void addTerminal(Terminal terminal) {
        _terminals.put(terminal.getKey(), terminal);
    }

    public List<Notification> clearNotifications() {
        List<Notification> notifications = _notifications;
        _notifications = new LinkedList<Notification>();
        return notifications;
    }

    public boolean canReceiveNotifications() {
        return _receiveNotifications;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        _receiveNotifications = receiveNotifications;
    }

    public double getPayments() {
        return _payments;
    }

    public double getBalance() {
        return _payments - _debts;
    }

    public double getDebts() {
        return _debts;
    }

    public boolean hasDebts() {
        return _debts > 0;
    }

    public Collection<Terminal> getTerminals() {
        return _terminals.values();
    }

    public void performPayment(double payment) {
        _payments += payment;
        _debts -= payment;
        _type.checkTransitionAfterPayment();
    }

    public double calculateTextCommCost(TextCommunication comm) {
        double cost = _type.calculateTextCommCost(comm);
        _debts += cost;
        _type.checkTransitionAfterComm();
        return cost;
    }

    public double calculateVoiceCommCost(InteractiveCommunication comm) {
        double cost = _type.calculateVoiceCommCost(comm);
        _debts += cost;
        _type.checkTransitionAfterComm();
        return cost;
    }

    public double calculateVideoCommCost(InteractiveCommunication comm) {
        double cost = _type.calculateVideoCommCost(comm);
        _debts += cost;
        _type.checkTransitionAfterComm();
        return cost;
    }

    public void deliverNotification(Notification notification) {
        deliveryMethod.deliver(notification);
    }

    @Override
    public String toString() {
        return String.format("CLIENT|%s|%s|%d|%s|%s|%d|%d|%d",
                             _key, _name, _taxId, _type.toString(),
                             _receiveNotifications ? "YES" : "NO",
                             _terminals.size(), Math.round(_payments),
                             Math.round(_debts));
    }

}