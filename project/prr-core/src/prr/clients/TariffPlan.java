package prr.clients;

import java.io.Serializable;

import prr.communications.InteractiveCommunication;
import prr.communications.TextCommunication;

public interface TariffPlan extends Serializable {
    public double calculateTextCommCost(TextCommunication comm);
    public double calculateVoiceCommCost(InteractiveCommunication comm);
    public double calculateVideoCommCost(InteractiveCommunication comm);
}
