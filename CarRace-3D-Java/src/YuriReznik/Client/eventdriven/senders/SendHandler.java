package YuriReznik.Client.eventdriven.senders;

import YuriReznik.Message.Envelope;
import YuriReznik.Server.eventdriven.EnvelopHandler;

/**
 * Handles Send request from client
 */
public interface SendHandler extends EnvelopHandler {
    /**
     * Sends client request to server
     * @param envelope to send
     */
    void handle(Envelope envelope);
}
