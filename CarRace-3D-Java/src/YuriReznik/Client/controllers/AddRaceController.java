package YuriReznik.Client.controllers;

import java.util.Collections;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Race;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.exceptions.NoSuchActionException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class AddRaceController implements Controller {

    private GlueObject glueObject;

    public AddRaceController(GlueObject glueObject) {
        this.glueObject = glueObject;
    }

    @Override
    public GlueObject getGlueObject() {
        return glueObject;
    }


    /**
     * Send request for ADD_RACE
     * @param race
     */
    public void addRace(Race race) {
        try {
            getGlueObject().handleSend(new Envelope()
                    .setMessageType(MessageType.ADD_RACE)
                    .setRaces(Collections.singletonList(race))
            );
        } catch (NoSuchActionException | ActionFailedException e) {
            AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), e.getMessage(), AlertType.ERROR);
        }
    }

    /**
     * Send request for GET_CARS
     */
    public void getCars() {
        try {
            getGlueObject().handleSend(new Envelope()
                    .setMessageType(MessageType.GET_CARS)
            );
        } catch (NoSuchActionException | ActionFailedException e) {
            AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), e.getMessage(), AlertType.ERROR);
        }
    }
}
