package prr.clients;

import prr.communications.TextCommunication;
import prr.communications.InteractiveCommunication;

public class PlatinumBasePlan implements BaseTariffPlan {

    @Override
    public double calculateTextCommCost(TextCommunication comm) {
        int characters = comm.getMessage().length();

        if (characters < 50) {
            return 0;
        } else if (50 <= characters && characters < 100) {
            return 4;
        } else {
            return 4;
        }
    }

    @Override
    public double calculateVoiceCommCost(InteractiveCommunication comm) {
        return 10 * comm.getDuration() * (comm.isFriend() ? 0.5 : 1);
    }

    @Override
    public double calculateVideoCommCost(InteractiveCommunication comm) {
        return 10 * comm.getDuration() * (comm.isFriend() ? 0.5 : 1);
    }
}
