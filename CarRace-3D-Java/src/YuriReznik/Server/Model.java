package YuriReznik.Server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import YuriReznik.Message.Bid;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Person;
import YuriReznik.Message.Race;
import YuriReznik.Server.eventdriven.AddRaceHandler;
import YuriReznik.Server.eventdriven.CreateNewAccountHandler;
import YuriReznik.Server.eventdriven.EnvelopHandler;
import YuriReznik.Server.eventdriven.FinishRaceHandler;
import YuriReznik.Server.eventdriven.GetCarsHandler;
import YuriReznik.Server.eventdriven.GetRacesAndBidsHandler;
import YuriReznik.Server.eventdriven.LoginHandler;
import YuriReznik.Server.eventdriven.MakeBidHandler;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.exceptions.NoSuchActionException;
import YuriReznik.Server.persistancy.BidDao;
import YuriReznik.Server.persistancy.PersonDao;
import YuriReznik.Server.persistancy.RaceDao;

public class Model {

	private ServerLog log;
	private PersonDao personDao = PersonDao.INSTANCE;
	private BidDao bidDao = BidDao.INSTANCE;
	private RaceDao raceDao = RaceDao.INSTANCE;
	private long clientId;
	private long personId = -1;
	private Map<MessageType, List<EnvelopHandler>> messageTypeToEnvelopeHandler = new HashMap<>();
	private Map<Long, Model> personIdToModelLookup;
	private ObjectOutputStream objectOutputStream;
	public static final long MAX_CARS_IN_RACE = 5;
	public static final long MIN_CARS_IN_RACE = 3;

	public Model(int clientId, ServerLog log, Map<Long, Model> personIdToModelLookup) {
		this.clientId = clientId;
		this.log = log;
		this.personIdToModelLookup = personIdToModelLookup;
		bindHandlers();
	}

	/**
	 * Bind predefined handlers to messageType
	 */
	private void bindHandlers() {
		addEnvelopHandler(MessageType.LOGIN, new LoginHandler(this));
		addEnvelopHandler(MessageType.CREATE_NEW_ACCOUNT, new CreateNewAccountHandler(this));
		addEnvelopHandler(MessageType.GET_RACES_AND_BIDS, new GetRacesAndBidsHandler(this));
		addEnvelopHandler(MessageType.MAKE_BID, new MakeBidHandler(this));
		addEnvelopHandler(MessageType.FINISH_RACE, new FinishRaceHandler(this));
		addEnvelopHandler(MessageType.GET_CARS, new GetCarsHandler(this));
		addEnvelopHandler(MessageType.ADD_RACE, new AddRaceHandler(this));
	}


	/**
	 * Bind handler to messageType
	 * @param messageType messageType to handle
	 * @param envelopHandler handler of message type
	 */
	public void addEnvelopHandler(MessageType messageType, EnvelopHandler envelopHandler) {
		List<EnvelopHandler> envelopHandlers = messageTypeToEnvelopeHandler.computeIfAbsent(messageType, k -> new ArrayList<>());

		if (envelopHandler != null) {
			envelopHandlers.add(envelopHandler);
		}

	}

	/**
	 * Handles client action by MessageType
	 * @param envelope transfer object to handle
	 * @throws NoSuchActionException if no such messageType configured
	 * @throws ActionFailedException if one of the handlers failed
	 */
	public void handle(Envelope envelope) throws NoSuchActionException, ActionFailedException {
		MessageType messageType = envelope.getMessageType();
		log.printMsg(String.format("INFOR | Start to handle message: %s, clientId: %d, personId: %d", messageType, clientId, personId));
		List<EnvelopHandler> envelopHandlers = messageTypeToEnvelopeHandler.get(messageType);
		if (envelopHandlers == null) {
			throw new NoSuchActionException("No such action supported by server");
		}

		for (EnvelopHandler envelopHandler : envelopHandlers) {
			envelopHandler.handle(envelope);
		}
		log.printMsg(String.format("INFOR | Done handling message: %s, clientId: %d, personId: %d", messageType, clientId, personId));
	}

	/**
	 * Set YuriReznik.Client ObjectOutputStream
	 * @param objectOutputStream
	 */
	public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
		this.objectOutputStream = objectOutputStream;
	}

	public void printRaceStatistics() {
		log.printMsg("INFOR | ============================================");
		log.printMsg("INFOR | ======= Printing Live Race Statistics ====== ");
		for (Race race : raceDao.getAllRaces()) {
			List<Bid> bidsByRaceId = bidDao.getBidsByRaceId(race.getId());
			double totalMoneyByRaceId = bidDao.getTotalMoneyByRaceId(race.getId());
			log.printMsg(String.format("INFOR | Race #%d status: %s, total bids: %d, total money: %f",
					race.getId(),
					race.isStarted() ? "Finished" : "Started",
					bidsByRaceId.size(), totalMoneyByRaceId));

			for (Bid bid : bidsByRaceId) {
				log.printMsg(String.format("INFOR | Person #%d on Car: %s", bid.getPersonId(), bid.getCarId()));
			}
		}
		log.printMsg("INFOR | ==== Done Printing Live Race Statistics ==== ");
		log.printMsg("INFOR | ============================================");
	}

	/**
	 * Get YuriReznik.Client ObjectInputStream
	 * @return
	 */
	public ObjectOutputStream getObjectOutputStream() {
		return objectOutputStream;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public ServerLog getLog() {
		return log;
	}

	public void setLog(ServerLog log) {
		this.log = log;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public Person getSystemUser() {
		return personDao.getSystemUser();
	}

	public Map<Long, Model> getPersonIdToModelLookup() {
		return personIdToModelLookup;
	}
}
