package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Updated view with latest Person information
 */
public class UpdatePersonReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public UpdatePersonReceiver(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    /**
     * Action to execute on Answer.YES
     *
     * @param envelope
     */
    @Override
    public void onYES(Envelope envelope) {
        Platform.runLater(() -> glueObject.setPerson(envelope.getPerson()));
    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "Envelope problems.", AlertType.ERROR);
    }

    /**
     * Action to execute on Answer.PROBLEM
     *
     * @param envelope
     */
    @Override
    public void onPROBLEM(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "Problem with server connection.", AlertType.ERROR);
    }
}
