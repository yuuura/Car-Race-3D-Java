package YuriReznik.Client.eventdriven.senders;

import java.io.IOException;

import YuriReznik.Client.controllers.Controller;
import YuriReznik.Message.Envelope;
import YuriReznik.Server.exceptions.ActionFailedException;

/**
 * Handles send of FINISH_RACE event
 */
public class FinishRaceSender implements SendHandler {

    private Controller controller;

    public FinishRaceSender(Controller controller) {
        this.controller = controller;
    }

    /**
     * Sends client request to server
     *
     * @param envelope to send
     */
    @Override
    public void handle(Envelope envelope) {
        try {
            controller.getGlueObject().getObjectOutputStream().writeObject(envelope);
        } catch (IOException e) {
            throw new ActionFailedException("Failed to execute FINISH_RACE of " + envelope.getMessageType());
        }
    }
}
