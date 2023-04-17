package prr.clients;

import prr.communications.TextCommunication;
import prr.communications.InteractiveCommunication;

public class GoldBasePlan implements BaseTariffPlan {
    
    @Override
    public double calculateTextCommCost(TextCommunication comm) {
        int characters = comm.getMessage().length();

        if (characters < 50) {
            return 10;
        } else if (50 <= characters && characters < 100) {
            return 10;
        } else {
            return 2 * characters;
        }
    }

    @Override
    public double calculateVoiceCommCost(InteractiveCommunication comm) {
        return 10 * comm.getDuration() * (comm.isFriend() ? 0.5 : 1);
    }

    @Override
    public double calculateVideoCommCost(InteractiveCommunication comm) {
        return 20 * comm.getDuration() * (comm.isFriend() ? 0.5 : 1);
    }
}
