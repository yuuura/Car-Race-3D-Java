package YuriReznik.Server.eventdriven;

import java.io.IOException;
import java.util.Map;

import YuriReznik.Message.Answer;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Person;
import YuriReznik.Server.Model;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.persistancy.PersonDao;
import YuriReznik.Server.persistancy.StatDao;

/**
 * Handles FINISH_RACE request/reply
 */
public class FinishRaceHandler implements EnvelopHandler {

    private StatDao statDao = StatDao.INSTANCE;
    private PersonDao personDao = PersonDao.INSTANCE;


    private Model model;

    public FinishRaceHandler(Model model) {
        this.model = model;
    }

    @Override
    public void handle(Envelope envelope) {
        Envelope envelopeToSend = new Envelope().setMessageType(MessageType.FINISH_RACE);
        try {
            statDao.addStat(envelope.getWinner());
            envelopeToSend.setWinner(envelope.getWinner());
        } catch (ActionFailedException e) {
            envelopeToSend.setAnswer(Answer.NO);
        }

        try {
            model.getObjectOutputStream().writeObject(envelopeToSend);
        } catch (IOException e) {
            throw new ActionFailedException("Handling FINISH_RACE failed " + e, e);
        }

        model.getLog().printMsg("INFOR | Preparing to send UPDATE_PERSON to all users.");
        for (Map.Entry<Long, Model> personModelEntry : model.getPersonIdToModelLookup().entrySet()) {
            try {
                Person updated = personDao.getPersonById(personModelEntry.getKey());
                personModelEntry
                        .getValue()
                        .getObjectOutputStream()
                        .writeObject(
                                envelopeToSend
                                        .setMessageType(MessageType.UPDATE_PERSON)
                                        .setPerson(updated)
                        );
                model.getLog().printMsg(String.format("INFOR | Sent UPDATE_PERSON to personId: %d", personModelEntry.getKey()));
            } catch (IOException e) {
                model.getLog().printMsg(String.format("ERROR | Handling UPDATE_PERSON failed for personId: %d", personModelEntry.getKey()));
            }
        }
        model.printRaceStatistics();
    }
}
