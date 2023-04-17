package prr.clients;

public class PlatinumType extends ClientType {
    
    public PlatinumType(Client client) {
        super(client, new PlatinumBasePlan());
    }

    public void transitToNormal() {
        Client client = super.getClient();
        client.setType(new NormalType(client));
    }

    public void transitToGold() {
        Client client = super.getClient();
        client.setType(new GoldType(client));
    }

    public void transitToPlatinum() {
        // EMPTY
    }

    public void checkTransitionAfterPayment() {
        // EMPTY
    }

    public void checkTransitionAfterComm() {
        if (super.getClient().getBalance() < 0) {
            transitToNormal();
        } else if (super.getTextCommStreak() == 2) {
            transitToGold();
        }
    }
    
    @Override
    public String toString() {
        return "PLATINUM";
    }
}
