package YuriReznik.Server.eventdriven;

import java.io.IOException;

import YuriReznik.Message.Answer;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Person;
import YuriReznik.Server.Model;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.persistancy.PersonDao;

/**
 * Handles CREATE_NEW_ACCOUNT request/reply
 */
public class CreateNewAccountHandler implements EnvelopHandler {

    private Model model;
    private PersonDao personDao = PersonDao.INSTANCE;

    public CreateNewAccountHandler(Model model) {
        this.model = model;
    }

    @Override
    public void handle(Envelope envelope) {
        Envelope envelopeToSend = new Envelope();
        Person person = personDao.addPerson(envelope.getPerson());
        if (person == null) {
            envelopeToSend.setAnswer(Answer.NO);
        } else {
            envelopeToSend.setPerson(person);
        }
        try {
            model.getObjectOutputStream().writeObject(envelopeToSend.setMessageType(MessageType.CREATE_NEW_ACCOUNT));
        } catch (IOException e) {
            throw new ActionFailedException("Handling CREATE_NEW_PERSON failed " + e, e);
        }
    }
}
