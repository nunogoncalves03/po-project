package prr.clients;

public class GoldType extends ClientType {

    public GoldType(Client client) {
        super(client, new GoldBasePlan());
    }

    public void transitToNormal() {
        Client client = super.getClient();
        client.setType(new NormalType(client));
    }

    public void transitToGold() {
        // EMPTY
    }

    public void transitToPlatinum() {
        Client client = super.getClient();
        client.setType(new PlatinumType(client));
    }

    public void checkTransitionAfterPayment() {
        // EMPTY
    }

    public void checkTransitionAfterComm() {
        if (super.getClient().getBalance() < 0) {
            transitToNormal();
        } else if (super.getVideoCommStreak() == 5) {
            transitToPlatinum();
        }
    }

    @Override
    public String toString() {
        return "GOLD";
    }
}
