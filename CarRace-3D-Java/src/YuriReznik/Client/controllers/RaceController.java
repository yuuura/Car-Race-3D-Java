package YuriReznik.Client.controllers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Winner;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.exceptions.NoSuchActionException;
import javafx.scene.control.Alert.AlertType;


public class RaceController implements Controller {

    private GlueObject glueObject;

    public RaceController(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    public GlueObject getGlueObject() {
        return glueObject;
    }

    /**
     * Send winner to server on race completion
     * @param winner
     */
    public void sendWinner(Winner winner) {
        try {
            getGlueObject().handleSend(new Envelope()
                    .setMessageType(MessageType.FINISH_RACE)
                    .setWinner(winner)
            );
        } catch (NoSuchActionException | ActionFailedException e) {
            AlertMessage.alertMsg(null, e.getMessage(), AlertType.ERROR);
        }
    }
}
