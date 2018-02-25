package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles receive bid reply from server to client
 */
public class MakeBidReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public MakeBidReceiver(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    /**
     * Action to execute on Answer.YES
     *
     * @param envelope
     */
    @Override
    public void onYES(Envelope envelope) {
        Platform.runLater(() -> {
            glueObject.setPerson(envelope.getPerson());
            glueObject.getMainView().setAcknowledgeCarBid(envelope.getBid(), true);
        });
    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        Platform.runLater(() -> glueObject.getMainView().setAcknowledgeCarBid(envelope.getBid(), false));
        AlertMessage.alertMsg(
                glueObject.getCurrentView().getStage(),
                "Can start race num " + envelope.getBid().getRaceId(),
                AlertType.INFORMATION
        );
    }

    /**
     * Action to execute on Answer.PROBLEM
     *
     * @param envelope
     */
    @Override
    public void onPROBLEM(Envelope envelope) {
        Platform.runLater(() -> glueObject.getMainView().setAcknowledgeCarBid(envelope.getBid(), false));
        AlertMessage.alertMsg(
                glueObject.getCurrentView().getStage(),
                "Sorry, problem with server connection.",
                AlertType.ERROR
        );
    }
}
