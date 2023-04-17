package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;
import prr.app.exceptions.UnknownTerminalKeyException;

/**
 * Command for sending a text communication.
 */
class DoSendTextCommunication extends TerminalCommand {

        DoSendTextCommunication(Network context, Terminal terminal) {
            super(Label.SEND_TEXT_COMMUNICATION, context, terminal,
                  receiver -> receiver.canStartCommunication());
            addStringField("key", Prompt.terminalKey());
            addStringField("message", Prompt.textMessage());
        }

        @Override
        protected final void execute() throws CommandException {
            try {
                _receiver.sendTextCommunication(
                                                _network, 
                                                stringField("key"), 
                                                stringField("message")
                                            );
            } catch (prr.exceptions.UnknownTerminalKeyException e) {
                throw new UnknownTerminalKeyException(e.getKey());
            } catch (prr.exceptions.DestinationTerminalIsOff e) {
                _display.popup(Message.destinationIsOff(e.getKey()));
            }
        }
} 
