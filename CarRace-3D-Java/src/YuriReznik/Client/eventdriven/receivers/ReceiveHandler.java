package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Message.Envelope;
import YuriReznik.Server.eventdriven.EnvelopHandler;

/**
 * Handles receive request from server to client
 */
public interface ReceiveHandler extends EnvelopHandler{
    /**
     * Receives server request and process them
     * @param envelope
     */
    void handle(Envelope envelope);

    /**
     * Action to execute on Answer.YES
     * @param envelope
     */
    void onYES(Envelope envelope);

    /**
     * Action to execute on Answer.NO
     * @param envelope
     */
    void onNO(Envelope envelope);

    /**
     * Action to execute on Answer.PROBLEM
     * @param envelope
     */
    void onPROBLEM(Envelope envelope);
}
