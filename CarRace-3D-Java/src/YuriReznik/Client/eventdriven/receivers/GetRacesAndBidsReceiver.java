package YuriReznik.Client.eventdriven.receivers;

import java.util.List;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Bid;
import YuriReznik.Message.Envelope;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles receive Get_RACES event from server to client
 */
public class GetRacesAndBidsReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public GetRacesAndBidsReceiver(GlueObject glueObject) {
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
            glueObject.getMainView().addRacesToView(envelope.getRaces(), envelope.getPersonBids());
            glueObject.getLoginView().hide();
            glueObject.getMainView().show();
        });
    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "Could not find race data.", AlertType.ERROR);
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
