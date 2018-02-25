package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles receive request from server to client
 */
public class CreateNewAccountReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public CreateNewAccountReceiver(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    /**
     * Action to execute on Answer.YES
     *
     * @param envelope
     */
    @Override
    public void onYES(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "Account created successfuly!", AlertType.INFORMATION);
        Platform.runLater(() -> glueObject.getLoginView().getBtnBackToLogIn().fire());
    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "This username already in use, try another one.", AlertType.ERROR);
    }

    /**
     * Action to execute on Answer.PROBLEM
     *
     * @param envelope
     */
    @Override
    public void onPROBLEM(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "YuriReznik.Server connection problems.", AlertType.ERROR);
    }
}
