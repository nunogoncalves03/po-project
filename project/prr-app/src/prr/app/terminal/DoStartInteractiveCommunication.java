package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;
import prr.app.exceptions.UnknownTerminalKeyException;

/**
 * Command for starting communication.
 */
class DoStartInteractiveCommunication extends TerminalCommand {

	DoStartInteractiveCommunication(Network context, Terminal terminal) {
		super(Label.START_INTERACTIVE_COMMUNICATION, context, terminal,
			  receiver -> receiver.canStartCommunication());
		addStringField("key", Prompt.terminalKey());
		addOptionField("type", Prompt.commType(), "VOICE", "VIDEO");
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			_receiver.startInteractiveCommunication(
				_network,
				stringField("key"),
				stringField("type")
			);
		} catch (prr.exceptions.UnknownTerminalKeyException e) {
			throw new UnknownTerminalKeyException(e.getKey());
		} catch (prr.exceptions.CommunicationUnsupportedAtOrigin e) {
			_display.popup(Message.unsupportedAtOrigin(e.getKey(), e.getType()));
		} catch (prr.exceptions.CommunicationUnsupportedAtDestination e) {
			_display.popup(Message.unsupportedAtDestination(e.getKey(), e.getType()));
		} catch (prr.exceptions.DestinationTerminalIsOff e) {
			_display.popup(Message.destinationIsOff(e.getKey()));
		} catch (prr.exceptions.DestinationTerminalIsBusy e) {
			_display.popup(Message.destinationIsBusy(e.getKey()));
		} catch (prr.exceptions.DestinationTerminalIsSilent e) {
			_display.popup(Message.destinationIsSilent(e.getKey()));
		}
	}
}
