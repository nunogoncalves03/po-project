package prr.terminals;

import prr.clients.Client;

public class BasicTerminal extends Terminal {

    public BasicTerminal(String key, Client client, String status) {
        super(key, client, status);
    }

    public boolean supportInteractiveCommunication(String type) {
        return type.equals("VOICE") ? true : false;
    }

    @Override
    public String toString() {
        return "BASIC|" + super.toString();
    }
    
}
