package prr.clients;

public class NormalType extends ClientType {
    
    public NormalType(Client client) {
        super(client, new NormalBasePlan());
    }

    public void transitToNormal() {
        // EMPTY
    }

    public void transitToGold() {
        Client client = super.getClient();
        client.setType(new GoldType(client));
    }

    public void transitToPlatinum() {
        // EMPTY
    }

    public void checkTransitionAfterPayment() {
        if (super.getClient().getBalance() > 500) {
            transitToGold();
        }
    }

    public void checkTransitionAfterComm() {
        // EMPTY
    }


    @Override
    public String toString() {
        return "NORMAL";
    }
}
