package prr.clients;

import java.io.Serializable;

import prr.communications.InteractiveCommunication;
import prr.communications.TextCommunication;

public abstract class ClientType implements Serializable {
    private Client _client;
    private TariffPlan _tariffPlan;
    private int _textCommunicationStreak = 0;
    private int _videoCommunicationStreak = 0;

    public ClientType(Client client, TariffPlan tariffPlan) {
        _client = client;
        _tariffPlan = tariffPlan;
    }

    public Client getClient() {
        return _client;
    }

    public int getTextCommStreak() {
        return _textCommunicationStreak;
    }

    public int getVideoCommStreak() {
        return _videoCommunicationStreak;
    }

    public double calculateTextCommCost(TextCommunication comm) {
        _textCommunicationStreak++;
        _videoCommunicationStreak = 0;
        return _tariffPlan.calculateTextCommCost(comm);
    }

    public double calculateVoiceCommCost(InteractiveCommunication comm) {
        _textCommunicationStreak = 0;
        _videoCommunicationStreak = 0;
        return _tariffPlan.calculateVoiceCommCost(comm);
    }

    public double calculateVideoCommCost(InteractiveCommunication comm) {
        _textCommunicationStreak = 0;
        _videoCommunicationStreak++;
        return _tariffPlan.calculateVideoCommCost(comm);
    }

    public abstract void transitToNormal();
    public abstract void transitToGold();
    public abstract void transitToPlatinum();
    public abstract void checkTransitionAfterPayment();
    public abstract void checkTransitionAfterComm();
}
