package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Message.Envelope;

/**
 * Abstract class that defines client receiver
 */
public abstract class BaseReceiveHandler implements ReceiveHandler {
    /**
     * Receives server request and process them
     *
     * @param envelope
     */
    @Override
    public void handle(Envelope envelope) {
        switch (envelope.getAnswer()) {
            case YES:
                onYES(envelope);
                break;
            case NO:
                onNO(envelope);
                break;
            default:
                onPROBLEM(envelope);
                break;
        }
    }
}
