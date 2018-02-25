package YuriReznik.Server.eventdriven;

import java.io.IOException;

import YuriReznik.Message.Answer;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Person;
import YuriReznik.Server.Model;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.persistancy.BidDao;
import YuriReznik.Server.persistancy.PersonDao;

/**
 * Handles LOGIN request/reply
 */
public class LoginHandler implements EnvelopHandler {

    private Model model;
    private PersonDao personDao = PersonDao.INSTANCE;
    private BidDao bidDao = BidDao.INSTANCE;

    public LoginHandler(Model model) {
        this.model = model;
    }

    @Override
    public void handle(Envelope envelope) {
        Person personToLogin = envelope.getPerson();

        Envelope envelopeToSend = new Envelope();
        Person foundPerson = personDao.getPersonByNameAndPassword(personToLogin);

        if (foundPerson == null) {
            envelopeToSend.setAnswer(Answer.NO);
        } else if (model.getPersonIdToModelLookup().keySet().contains(foundPerson.getId())) {
            envelopeToSend.setPerson(foundPerson);
            envelopeToSend.setAnswer(Answer.PROBLEM);
        } else {
            envelopeToSend.setPerson(foundPerson);
            model.setPersonId(foundPerson.getId());
            model.getPersonIdToModelLookup().put(foundPerson.getId(), model);
        }

        try {
            model.getObjectOutputStream().writeObject(envelopeToSend.setMessageType(MessageType.LOGIN));
        } catch (IOException e) {
            throw new ActionFailedException("Handling LOGIN failed " + e, e);
        }
    }
}
