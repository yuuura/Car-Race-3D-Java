package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles server reply for FINISH_RACE event
 */
public class FinishRaceReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public FinishRaceReceiver(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    /**
     * Action to execute on Answer.YES
     *
     * @param envelope
     */
    @Override
    public void onYES(Envelope envelope) {
        glueObject.getRaceView().hide();

        for (GlueObject glueObject : GlueObject.getPersonIdToGlueObject()) {
            glueObject.getCurrentView().show();
            glueObject.getMainView().disableRaceBetsByRaceId(envelope.getWinner().getRaceId());
        }
    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "For some reason server couldn't store Winner object.", AlertType.ERROR);
    }

    /**
     * Action to execute on Answer.PROBLEM
     *
     * @param envelope
     */
    @Override
    public void onPROBLEM(Envelope envelope) {
        glueObject.getRaceView().hide();
        glueObject.getCurrentView().show();
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "Problem with server connection.", AlertType.ERROR);
    }
}
