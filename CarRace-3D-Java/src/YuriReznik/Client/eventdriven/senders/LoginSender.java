package YuriReznik.Client.eventdriven.senders;

import java.io.IOException;

import YuriReznik.Client.controllers.Controller;
import YuriReznik.Message.Envelope;
import YuriReznik.Server.exceptions.ActionFailedException;

/**
 * Handles Send LOGIN request from client
 */
public class LoginSender implements SendHandler {


    private Controller controller;

    public LoginSender(Controller controller) {
        this.controller = controller;
    }

    /**
     * Sends client request to server
     * @param envelope to send
     */
    @Override
    public void handle(Envelope envelope) {
        try {
            controller.getGlueObject().getObjectOutputStream().writeObject(envelope);
        } catch (IOException e) {
            throw new ActionFailedException("Failed to execute SEND of " + envelope.getMessageType());
        }
    }
}
