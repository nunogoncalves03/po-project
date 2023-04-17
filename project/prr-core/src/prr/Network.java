package prr;

import java.io.Serializable;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import prr.clients.Client;
import prr.clients.Notification;
import prr.communications.Communication;
import prr.communications.InteractiveCommunication;
import prr.communications.TextCommunication;
import prr.communications.VoiceCommunication;
import prr.communications.VideoCommunication;
import prr.terminals.Terminal;
import prr.terminals.BasicTerminal;
import prr.terminals.FancyTerminal;
import prr.exceptions.UnrecognizedEntryException;
import prr.exceptions.DuplicateClientKeyException;
import prr.exceptions.UnknownClientKeyException;
import prr.exceptions.UnknownTerminalKeyException;
import prr.exceptions.InvalidTerminalKeyException;
import prr.exceptions.DuplicateTerminalKeyException;
import prr.exceptions.ClientNotificationsAlreadyEnabled;
import prr.exceptions.ClientNotificationsAlreadyDisabled;


/**
 * Class Network implements a network.
 */
public class Network implements Serializable {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 202208091753L;

	/**
	 * Map of all the clients in the Network.
	 */
	private Map<String, Client> _clients = 
					 new TreeMap<String, Client>(String.CASE_INSENSITIVE_ORDER);
	
	/**
	 * Map of all the terminals in the Network.
	 */
	private Map<String, Terminal> _terminals = new TreeMap<String, Terminal>();
	
	/**
	 * Tells if changes have been made to the Network.
	 */
	private boolean _changed = false;

	/**
	 * Map of all the communications in the Network.
	 */	
	private List<Communication> _communications = new LinkedList<Communication>();

	/**
	 * Counter of communications in the Network.
	 */	
	private int _communicationsCounter = 0;

	/**
	 * Read text input file and create corresponding domain entities.
	 * 
	 * @param filename     name of the text input file
	 * @throws IOException if there is an IO error while processing
	 					   the text file
	 * @throws UnrecognizedEntryException    if some entry is not correct
	 * @throws DuplicateClientKeyException   if, when registering a
	 *										 client, the given key is
	 *										 already being used
	 * @throws UnknownClientKeyException     if, when registering a
	 *										 terminal, there is no
	 *										 client with the given key
	 * @throws InvalidTerminalKeyException   if, when registering a
	 *										 terminal, the given key 
	 *										 doesn't have the correct
	 *                                       format
	 * @throws DuplicateTerminalKeyException if, when registering a
	 *										 terminal, the given key
	 *										 is already being used
	 * @throws UnknownTerminalKeyException   if, when registering
	 *										 friends, there is no
	 *                                       terminal with one of the
	 *										 given keys
	 */
	void importFile(String filename) 
				throws IOException, UnrecognizedEntryException,
				DuplicateClientKeyException, UnknownClientKeyException,
				InvalidTerminalKeyException, DuplicateTerminalKeyException,
			   	UnknownTerminalKeyException {
		
		try (BufferedReader reader = new BufferedReader(
										 new FileReader(filename) )) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split("\\|");
				registerEntry(fields);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Register an entity with the given array of fields.
	 * 
	 * @param fields array of strings with information to register an entity
	 * @throws UnrecognizedEntryException    if some entry is not correct
	 * @throws DuplicateClientKeyException   if, when registering a 
	 *										 client, the given key is
	 *										 already being used
	 * @throws UnknownClientKeyException     if, when registering a
	 *										 terminal, there is no
	 *										 client with the given key
	 * @throws InvalidTerminalKeyException   if, when registering a
	 *										 terminal, the given key
	 *										 doesn't have the correct
	 *										 format
	 * @throws DuplicateTerminalKeyException if, when registering a
	 *										 terminal, the given key
	 *										 is already being used
	 * @throws UnknownTerminalKeyException   if, when registering
	 *										 friends, there is no
	 *										 terminal with one of the
	 *										 given keys
	 */
	public void registerEntry(String... fields)
			throws UnrecognizedEntryException, DuplicateClientKeyException,
			UnknownClientKeyException, InvalidTerminalKeyException,
			DuplicateTerminalKeyException, UnknownTerminalKeyException {
		
		switch (fields[0]) {
			case "CLIENT" -> registerClient(fields[1], fields[2], fields[3]);
			case "BASIC" -> registerTerminal(fields[0], fields[1],
											 fields[2], fields[3]);
			case "FANCY" -> registerTerminal(fields[0], fields[1], 
											 fields[2], fields[3]);
			case "FRIENDS" -> registerFriends(fields[1], fields[2].split(","));
			default -> throw
					   new UnrecognizedEntryException(String.join("|", fields));
		}
	}

	/**
	 * Register a client with the given attributes.
	 * 
	 * @param key   the client's key
	 * @param name  the client's name
	 * @param taxId the client's taxId
	 * @throws DuplicateClientKeyException if the given key is already
	 *									   being used
	 */
	public void registerClient(String key, String name, String taxId)
			throws DuplicateClientKeyException {
		
		if (_clients.containsKey(key)) {
			throw new DuplicateClientKeyException(key);
		}

		_clients.put(key, new Client(key, name, Integer.parseInt(taxId)));
		setChanged(true);
	}

	/**
	 * Register a terminal with the given attributes.
	 * 
	 * @param type      the terminal's type
	 * @param key       the terminal's key
	 * @param clientKey the terminal's client key
	 * @param state     the terminal's state
	 * @throws UnknownClientKeyException     if there is no client
	 *										 with the given key
	 * @throws InvalidTerminalKeyException   if the given key doesn't
	 *										 have the correct format
	 * @throws DuplicateTerminalKeyException if the given key is
	 *										 already being used
	 */
	public void registerTerminal(String type, String key,
	String clientKey, String state)
			throws InvalidTerminalKeyException,
			DuplicateTerminalKeyException, UnknownClientKeyException {
		
		Client client = getClient(clientKey);

		if (!isTerminalKeyValid(key)) {
			throw new InvalidTerminalKeyException(key);
		} else if (_terminals.containsKey(key)) {
			throw new DuplicateTerminalKeyException(key);
		}

		Terminal terminal;
		if (type.equals("BASIC")) {
			terminal = new BasicTerminal(key, client, state);
		} else {
			terminal = new FancyTerminal(key, client, state);
		}

		client.addTerminal(terminal);
		_terminals.put(key, terminal);
		setChanged(true);
	}

	/**
	 * Register friends to a terminal.
	 * 
	 * @param key     key of the terminal to which are being added friends
	 * @param friends array of the keys of the terminals to be added as friends
	 * @throws UnknownTerminalKeyException if there is no terminal
	 *									   with one of the given keys
	 */
	public void registerFriends(String key, String[] friends)
			throws UnknownTerminalKeyException {

		Terminal terminal = getTerminal(key);

		for (String friend : friends) {
			terminal.addFriend(this, friend);
		}
		setChanged(true);
	}

	/**
	 * Register a text communication.
	 * 
	 * @param source	  the terminal that sends the text communication
	 * @param destination the terminal that receives the text communication
	 * @param message	  the message sent
	 * @return			  the created communication
	 */
	public TextCommunication registerTextCommunication(Terminal source,
								Terminal destination, String message) {
									
		TextCommunication comm = new TextCommunication(++_communicationsCounter,
										source, destination, message);
		_communications.add(comm);
		setChanged(true);
		return comm;
	}

	/**
	 * Register an interactive communication.
	 * 
	 * @param source	  the terminal that starts the interactive communication
	 * @param destination the destination terminal
	 * @param type		  the type of interactive communication
	 * @return			  the created communication
	 */
	public InteractiveCommunication registerInteractiveCommunication
				(Terminal source, Terminal destination, String type) {

		InteractiveCommunication comm;

		if (type.equals("VOICE")) {
			comm = new VoiceCommunication(++_communicationsCounter,
										  source, destination);
		} else {
			comm = new VideoCommunication(++_communicationsCounter,
										  source, destination);
		}

		_communications.add(comm);
		setChanged(true);
		return comm;
	}

	/**
	 * Check if a string corresponds to a valid terminal key.
	 * 
	 * @param key the terminal key to be validated
	 * @return true if the key is valid, false otherwise
	 */
	public boolean isTerminalKeyValid(String key) {
		final int TERMINAL_KEY_LENGTH = 6;

		if (key.length() != TERMINAL_KEY_LENGTH) {
			return false;
		}

		try {
			Integer.parseInt(key);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	 * Get the sum of the payments of all clients.
	 * 
	 * @return the sum of the payments of all clients
	 */
	public double getPayments() {
		double payments = 0;
		for (Client client : _clients.values()) {
			payments += client.getPayments();
		}
		return payments;
	}

	/**
	 * Get the sum of the debts of all clients.
	 *
	 * @return the sum of the debts of all clients
	 */
	public double getDebts() {
		double debts = 0;
		for (Client client : _clients.values()) {
			debts += client.getDebts();
		}
		return debts;
	}

	/**
	 * Check if there have been made changes to the Network.
	 * 
	 * @return true if there have been made changes to the Network,
	 *         false otherwise
	 */
	public boolean getChanged() {
		return _changed;
	}

	/**
	 * Set the value of the attribute _changed.
	 * 
	 * @param changed the boolean value to set the attribute _changed
	 */
	public void setChanged(boolean changed) {
		_changed = changed;
	}

	/**
	 * Get the client with the given attributes.
	 * 
	 * @param key the key of the client to be got
	 * @return the client
	 * @throws UnknownClientKeyException if there is no client with
	 *									 the given key
	 */
	public Client getClient(String key) throws UnknownClientKeyException {
		Client client = _clients.get(key);

		if (client == null) {
			throw new UnknownClientKeyException(key);
		}

		return client;
	}

	/**
	 * Get a collection of all the clients in the Network.
	 * 
	 * @return collection of all the clients in the Network
	 */
	public Collection<Client> getAllClients() {
		return _clients.values();
	}

	/**
	 * Clear all the notifications of a client and return an array with them.
	 *
	 * @param clientKey the terminal's client key
	 * @return array of the notifications of the client
	 * @throws UnknownClientKeyException if there is no client with
	 *									 the given key
	 */
	public Collection<Notification> getClientNotifications(String clientKey)
			throws UnknownClientKeyException {
		return getClient(clientKey).clearNotifications();
	}

	/**
	 * Enable notifications of failed contacts from the client.
	 * 
	 * @param clientKey the key of the client whose notifications will
	 * 					be enabled
	 * @throws UnknownClientKeyException if there is no client with
	 * 									 the given key
	 * @throws ClientNotificationsAlreadyEnabled if the notifications
	 * 											 are already enabled
	 */
	public void enableClientNotifications(String clientKey) throws
			UnknownClientKeyException, ClientNotificationsAlreadyEnabled {
		Client client = getClient(clientKey);

		if (client.canReceiveNotifications()) {
			throw new ClientNotificationsAlreadyEnabled();
		} else {
			client.setReceiveNotifications(true);
		}
	}

	/**
	 * Disable notifications of failed contacts from the client.
	 * 
	 * @param clientKey the key of the client whose notifications will
	 * 					be disabled
	 * @throws UnknownClientKeyException if there is no client with
	 * 									 the given key
	 * @throws ClientNotificationsAlreadyDisabled if the notifications
	 * 											  are already disabled
	 */
	public void disableClientNotifications(String clientKey) throws
			UnknownClientKeyException, ClientNotificationsAlreadyDisabled {
		Client client = getClient(clientKey);

		if (!client.canReceiveNotifications()) {
			throw new ClientNotificationsAlreadyDisabled();
		} else {
			client.setReceiveNotifications(false);
		}
	}

	/**
	 * Get the payments of a client.
	 * 
	 * @param clientKey the client's key
	 * @return the value of the client's payments
	 * @throws UnknownClientKeyException  if there is no client with
	 * 									  the given key
	 */
	public double getClientPayments(String clientKey)
			throws UnknownClientKeyException {

		return getClient(clientKey).getPayments();
	}

	/**
	 * Get the debts of a client.
	 * 
	 * @param clientKey the client's key
	 * @return the value of the client's debts
	 * @throws UnknownClientKeyException if there is no client with
	 * 									 the given key
	 */
	public double getClientDebts(String clientKey)
			throws UnknownClientKeyException {
		
		return getClient(clientKey).getDebts();
	}

	/**
	 * Get the terminal with the given attributes.
	 * 
	 * @param key the key of the terminal to be got
	 * @return the terminal
	 * @throws UnknownTerminalKeyException if there is no terminal
	 *									   with the given key
	 */
	public Terminal getTerminal(String key)
			throws UnknownTerminalKeyException {
		
		Terminal terminal = _terminals.get(key);

		if (terminal == null) {
			throw new UnknownTerminalKeyException(key);
		}

		return terminal;
	}

	/**
	 * Get a collection of all the terminals in the Network.
	 * 
	 * @return collection of all the terminals in the Network
	 */
	public Collection<Terminal> getAllTerminals() {
		return _terminals.values();
	}

	/**
	 * Get a collection of all the communications in the Network.
	 * 
	 * @return the collection of all the communications in the Network
	 */
	public Collection<Communication> getAllCommunications() {
		return _communications;
	}

	/**
	 * Get a collection of the communications from a client.
	 * 
	 * @param key the client's key
	 * @return the collection of the communications from the client
	 * @throws UnknownClientKeyException if there is no client with
	 * 									 the given key
	 */
	public Collection<Communication> getCommunicationsFromClient(String key)
									 throws UnknownClientKeyException {

		Client client = getClient(key);
		List<Communication> communications = new LinkedList<Communication>();
		
		client.getTerminals().forEach((terminal) -> communications.addAll
								  (terminal.getStartedCommunications()));
		
		Collections.sort(communications,
						 (comm1, comm2) -> comm1.getKey() - comm2.getKey());
		
		return communications;
	}

	/**
	 * Get a collection of the communications to a client.
	 * 
	 * @param key the client's key
	 * @return the collection of the communications to the client
	 * @throws UnknownClientKeyException if there is no client with
	 * 									 the given key
	 */
	public Collection<Communication> getCommunicationsToClient(String key)
									 throws UnknownClientKeyException {

		Client client = getClient(key);
		List<Communication> communications = new LinkedList<Communication>();

		client.getTerminals().forEach((terminal) -> communications.addAll
								 (terminal.getReceivedCommunications()));
		
		Collections.sort(communications,
						 (comm1, comm2) -> comm1.getKey() - comm2.getKey());
		
		return communications;
	}
}
