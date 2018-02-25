package YuriReznik.Client.eventdriven;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import YuriReznik.Client.ClientLog;
import YuriReznik.Client.controllers.AddRaceController;
import YuriReznik.Client.controllers.LoginController;
import YuriReznik.Client.controllers.MainController;
import YuriReznik.Client.controllers.RaceController;
import YuriReznik.Client.eventdriven.receivers.AddRaceReceiver;
import YuriReznik.Client.eventdriven.receivers.CreateNewAccountReceiver;
import YuriReznik.Client.eventdriven.receivers.FinishRaceReceiver;
import YuriReznik.Client.eventdriven.receivers.GetCarsReceiver;
import YuriReznik.Client.eventdriven.receivers.GetRacesAndBidsReceiver;
import YuriReznik.Client.eventdriven.receivers.LoginReceiver;
import YuriReznik.Client.eventdriven.receivers.MakeBidReceiver;
import YuriReznik.Client.eventdriven.receivers.ReceiveHandler;
import YuriReznik.Client.eventdriven.receivers.StartRaceReceiver;
import YuriReznik.Client.eventdriven.receivers.UpdatePersonReceiver;
import YuriReznik.Client.eventdriven.senders.AddRaceSender;
import YuriReznik.Client.eventdriven.senders.CreateNewAccountSender;
import YuriReznik.Client.eventdriven.senders.FinishRaceSender;
import YuriReznik.Client.eventdriven.senders.GetCarsSender;
import YuriReznik.Client.eventdriven.senders.GetRacesAndBidsSender;
import YuriReznik.Client.eventdriven.senders.LoginSender;
import YuriReznik.Client.eventdriven.senders.MakeBidSender;
import YuriReznik.Client.eventdriven.senders.SendHandler;
import YuriReznik.Client.views.AddRaceView;
import YuriReznik.Client.views.LoginView;
import YuriReznik.Client.views.MainView;
import YuriReznik.Client.views.RaceView;
import YuriReznik.Client.views.View;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Person;
import YuriReznik.Server.exceptions.NoSuchActionException;
import YuriReznik.Server.exceptions.ServerProblemException;

/**
 * YuriReznik.Client connection glue object
 */
public class GlueObject {

    private static Set<GlueObject> personIdToGlueObject = new HashSet<>();

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private Map<MessageType, List<ReceiveHandler>> messageTypeToReceiveHandler = new HashMap<>();
    private Map<MessageType, List<SendHandler>> messageTypeToSendHandler = new HashMap<>();

    private LoginController loginController;
    private MainController mainController;
    private RaceController raceController;
    private AddRaceController addRaceController;

    private LoginView loginView;
    private MainView mainView;
    private RaceView raceView;
    private AddRaceView addRaceView;
    private View currentView;

    private Person person;

    private ClientLog log;

    public GlueObject(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream, double x, double y, ClientLog log) {
        this.loginController = new LoginController(this);
        this.mainController = new MainController(this);
        this.raceController = new RaceController(this);
        this.addRaceController = new AddRaceController(this);

        this.loginView = new LoginView(this, x, y);
        this.mainView = new MainView(this, x, y);
        this.raceView = new RaceView(this);
        this.addRaceView = new AddRaceView(this);

        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
        this.log = log;
        registerHandlers();
        getPersonIdToGlueObject().add(this);
    }

    public static Set<GlueObject> getPersonIdToGlueObject() {
        return personIdToGlueObject;
    }

    private void registerHandlers() {
        registerSendHandlers();
        registerReceiveHandlers();
    }

    private void registerSendHandlers() {
        addSendHandler(MessageType.LOGIN, new LoginSender(loginController));
        addSendHandler(MessageType.CREATE_NEW_ACCOUNT, new CreateNewAccountSender(loginController));
        addSendHandler(MessageType.MAKE_BID, new MakeBidSender(mainController));
        addSendHandler(MessageType.GET_RACES_AND_BIDS, new GetRacesAndBidsSender(mainController));
        addSendHandler(MessageType.FINISH_RACE, new FinishRaceSender(mainController));
        addSendHandler(MessageType.ADD_RACE, new AddRaceSender(mainController));
        addSendHandler(MessageType.GET_CARS, new GetCarsSender(mainController));
    }

    private void addSendHandler(MessageType messageType, SendHandler sendHandler) {
        List<SendHandler> sendHandlers = messageTypeToSendHandler.computeIfAbsent(messageType, k -> new ArrayList<>());

        if (sendHandler != null) {
            sendHandlers.add(sendHandler);
        }
    }

    public void handleSend(Envelope envelope) throws NoSuchActionException {
        MessageType messageType = envelope.getMessageType();
        log.printMsg(String.format("INFOR | Start to handle Send of message: %s, person: %s", messageType, person));
        List<SendHandler> sendHandlers = messageTypeToSendHandler.get(messageType);
        if (sendHandlers == null) {
            throw new NoSuchActionException("No such action supported by client");
        }

        envelope.setMessageType(messageType);
        for (SendHandler sendHandler : sendHandlers) {
            sendHandler.handle(envelope);
        }
        log.printMsg(String.format("INFOR | Finished handling Send of message: %s, person: %s", messageType, person));
    }

    private void registerReceiveHandlers() {
        addReceiveHandler(MessageType.LOGIN, new LoginReceiver(this));
        addReceiveHandler(MessageType.CREATE_NEW_ACCOUNT, new CreateNewAccountReceiver(this));
        addReceiveHandler(MessageType.MAKE_BID, new MakeBidReceiver(this));
        addReceiveHandler(MessageType.GET_RACES_AND_BIDS, new GetRacesAndBidsReceiver(this));
        addReceiveHandler(MessageType.START_RACE, new StartRaceReceiver(this));
        addReceiveHandler(MessageType.FINISH_RACE, new FinishRaceReceiver(this));
        addReceiveHandler(MessageType.ADD_RACE, new AddRaceReceiver(this));
        addReceiveHandler(MessageType.GET_CARS, new GetCarsReceiver(this));
        addReceiveHandler(MessageType.UPDATE_PERSON, new UpdatePersonReceiver(this));
    }

    private void addReceiveHandler(MessageType messageType, ReceiveHandler receiveHandler) {
        List<ReceiveHandler> receiveHandlers = messageTypeToReceiveHandler.computeIfAbsent(messageType, k -> new ArrayList<>());

        if (receiveHandler != null) {
            receiveHandlers.add(receiveHandler);
        }
    }

    /**
     * Handles receive from server
     * @param envelope message itself
     * @throws NoSuchActionException if no such action supported by server
     * @throws ServerProblemException if action failed because of internal server problems
     */
    public void handleReceive(Envelope envelope) throws NoSuchActionException, ServerProblemException {
        MessageType messageType = envelope.getMessageType();
        log.printMsg(String.format("INFOR | Start to handle Receive of message: %s, person: %s", messageType, person));
        if (messageType == null) {
            throw new NoSuchActionException("Message type can't be null");
        }
        List<ReceiveHandler> receiveHandlers = messageTypeToReceiveHandler.get(messageType);
        if (receiveHandlers == null) {
            throw new NoSuchActionException("No such action supported by client");
        }

        for (ReceiveHandler receiveHandler : receiveHandlers) {
            receiveHandler.handle(envelope);
        }
        log.printMsg(String.format("INFOR | Finished handling Receive of message: %s, person: %s", messageType, person));
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public MainController getMainController() {
        return mainController;
    }

    public RaceController getRaceController() {
        return raceController;
    }

    public AddRaceController getAddRaceController() {
        return addRaceController;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public MainView getMainView() {
        return mainView;
    }

    public RaceView getRaceView() {
        return raceView;
    }

    public AddRaceView getAddRaceView() {
        return addRaceView;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        getMainView().setPerson(person);
    }

    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }

    public ClientLog getLog() {
        return log;
    }

    public void setLog(ClientLog log) {
        this.log = log;
    }
}
