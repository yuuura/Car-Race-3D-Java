package YuriReznik.Client.views;

import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Person;
import javafx.stage.Stage;

/**
 * Interface that defines view
 */
public interface View {

    void show();

    void hide();

    Stage getStage();

    GlueObject getGlueObject();

    void setPerson(Person person);

}
