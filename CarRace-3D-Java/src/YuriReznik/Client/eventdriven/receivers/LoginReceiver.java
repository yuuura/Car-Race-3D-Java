package YuriReznik.Client.eventdriven.receivers;


import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles LOGIN reply from server to client
 */
public class LoginReceiver extends BaseReceiveHandler {

    private GlueObject glueObject;

    public LoginReceiver(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    /**
     * Action to execute on Answer.YES
     *
     * @param envelope
     */
    @Override
    public void onYES(Envelope envelope) {
        glueObject.setPerson(envelope.getPerson());
        glueObject.getMainController().getRaces();
    }

    /**
     * Action to execute on Answer.NO
     *
     * @param envelope
     */
    @Override
    public void onNO(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "This user does not exist.", AlertType.ERROR);
    }

    /**
     * Action to execute on Answer.PROBLEM
     *
     * @param envelope
     */
    @Override
    public void onPROBLEM(Envelope envelope) {
        AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), "Person: " + envelope.getPerson().getName() + " already connected", AlertType.ERROR);
    }
}
