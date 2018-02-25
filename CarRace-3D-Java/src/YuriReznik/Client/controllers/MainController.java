package YuriReznik.Client.controllers;

import java.util.Collections;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Bid;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Race;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.exceptions.NoSuchActionException;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller for the main view
 */
public class MainController implements Controller {

    private GlueObject glueObject;

    public MainController(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    public GlueObject getGlueObject() {
        return glueObject;
    }

    /**
     * Send request to server to get races and existing use bids if any
     */
    public void getRaces() {
        try {
            getGlueObject().handleSend(new Envelope()
                    .setMessageType(MessageType.GET_RACES_AND_BIDS)
            );
        } catch (NoSuchActionException | ActionFailedException e) {
            AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), e.getMessage(), AlertType.ERROR);
        }
    }

    /**
     * Send bid to server
     * @param bid the actual bid
     */
    public void makeBet(Bid bid) {
        try {
            getGlueObject().handleSend(new Envelope()
                    .setMessageType(MessageType.MAKE_BID)
                    .setBid(bid)
            );
        } catch (NoSuchActionException | ActionFailedException e) {
            AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), e.getMessage(), AlertType.ERROR);
        }
    }
}
