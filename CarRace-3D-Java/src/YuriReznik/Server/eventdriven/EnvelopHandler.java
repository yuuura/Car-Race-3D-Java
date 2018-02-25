package YuriReznik.Server.eventdriven;

import YuriReznik.Message.Envelope;

/**
 * Interface for EnvelopHandler
 */
public interface EnvelopHandler {

    void handle(Envelope envelope);
}
