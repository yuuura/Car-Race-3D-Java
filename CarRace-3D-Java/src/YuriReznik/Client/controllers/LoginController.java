package YuriReznik.Client.controllers;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Person;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.exceptions.NoSuchActionException;
import javafx.scene.control.Alert.AlertType;

/**
 * Login controller
 */
public class LoginController implements Controller {

    private GlueObject glueObject;

    public LoginController(GlueObject glueObject) {
        this.glueObject = glueObject;
    }


    public GlueObject getGlueObject() {
        return glueObject;
    }


    public void makeLogin(String username, String password) {
        try {
            glueObject.handleSend(new Envelope()
                            .setMessageType(MessageType.LOGIN)
                            .setPerson(new Person(username, password))
            );
        } catch (NoSuchActionException | ActionFailedException e) {
            AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), e.getMessage(), AlertType.ERROR);
        }
    }

    public void createNewUser(String username, String password, double money) {
        try {
            glueObject.handleSend(new Envelope()
                    .setMessageType(MessageType.CREATE_NEW_ACCOUNT)
                    .setPerson(new Person(username, password, money))
            );
        } catch (NoSuchActionException | ActionFailedException e) {
            AlertMessage.alertMsg(glueObject.getCurrentView().getStage(), e.getMessage(), AlertType.ERROR);
        }
    }
}
