package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Receives server reply on GET_CARS event
 */
public class GetCarsReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public GetCarsReceiver(GlueObject glueObject) {
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
            glueObject.getAddRaceView().refreshData(envelope.getCars());
            glueObject.getAddRaceView().show();
        });
    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "Could not find cars data.", AlertType.ERROR);
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
