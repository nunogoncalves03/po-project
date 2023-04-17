package prr.app.lookups;

import java.util.stream.Collectors;
import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show clients with negative balance.
 */
class DoShowClientsWithDebts extends Command<Network> {

  DoShowClientsWithDebts(Network receiver) {
    super(Label.SHOW_CLIENTS_WITH_DEBTS, receiver);
  }

  @Override
  protected final void execute() throws CommandException {
    _display.addAll(_receiver.getAllClients().stream()
        .filter(client -> client.hasDebts())
        .sorted(
          (client1, client2) ->
            Long.compare(
              Math.round(client2.getDebts()),
              Math.round(client1.getDebts())
            )
        )
        .collect(Collectors.toList())
    );
    _display.display();
  }
}
