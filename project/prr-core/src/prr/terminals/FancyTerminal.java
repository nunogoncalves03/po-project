package prr.terminals;

import prr.clients.Client;

public class FancyTerminal extends Terminal {

    public FancyTerminal(String key, Client client, String status) {
        super(key, client, status);
    }

    public boolean supportInteractiveCommunication(String type) {
        return true;
    }

    @Override
    public String toString() {
        return "FANCY|" + super.toString();
    }
    
}
