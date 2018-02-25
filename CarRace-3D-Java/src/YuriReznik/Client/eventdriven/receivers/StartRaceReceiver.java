package YuriReznik.Client.eventdriven.receivers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.Race;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles server request on start race event
 */
public class StartRaceReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public StartRaceReceiver(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    /**
     * Action to execute on Answer.YES
     *
     * @param envelope
     */
    @Override
    public void onYES(Envelope envelope) {
        if (envelope.getRaces() != null && envelope.getRaces().size() > 0) {
            Race raceToStart = envelope.getRaces().get(0);
            closeOtherViews(raceToStart);
            glueObject.getRaceView().setRace(raceToStart);
            glueObject.getRaceView().show();
            glueObject.getRaceView().startRace();
        } else {
            AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "For some reason server not sent a race", AlertType.ERROR);
        }

    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "For some reason server wasn't able to start race", AlertType.ERROR);
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

    /**
     * Close all client views
     */
    private void closeOtherViews(Race raceToStart) {
        for (GlueObject glueObject : GlueObject.getPersonIdToGlueObject()) {
            glueObject.getCurrentView().hide();
        }
    }
}
