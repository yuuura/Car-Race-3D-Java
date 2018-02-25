package YuriReznik.Server.eventdriven;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import YuriReznik.Message.Answer;
import YuriReznik.Message.Bid;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Race;
import YuriReznik.Server.Model;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.persistancy.BidDao;
import YuriReznik.Server.persistancy.RaceDao;

/**
 * Handles add race
 */
public class AddRaceHandler implements EnvelopHandler {

    private Model model;
    private RaceDao raceDao = RaceDao.INSTANCE;
    private BidDao bidDao = BidDao.INSTANCE;

    public AddRaceHandler(Model model) {
        this.model = model;
    }

    @Override
    public void handle(Envelope envelope) {
        Envelope envelopeToSend = new Envelope();

        List<Race> races = envelope.getRaces();
        if (races != null && races.size() > 0) {
            Race race = raceDao.createRace(envelope.getRaces().get(0));
            if (race == null) {
                envelopeToSend.setAnswer(Answer.NO);
            } else {
                List<Bid> bidsByPersonId = bidDao.getBidsByPersonId(model.getPersonId());
                envelopeToSend.setRaces(raceDao.getAllRaces());
                envelope.setPersonBids(bidsByPersonId);
            }

            model.getLog().printMsg("INFOR | Broadcasting ADD_RACE event");
            for (Map.Entry<Long, Model> personModelEntry : model.getPersonIdToModelLookup().entrySet()) {
                try {
                    personModelEntry.getValue().getObjectOutputStream().writeObject(envelopeToSend.setMessageType(MessageType.ADD_RACE));
                    model.getLog().printMsg(String.format("INFOR | Sent ADD_RACE to personId: %d", personModelEntry.getKey()));
                } catch (IOException e) {
                    model.getLog().printMsg(String.format("ERROR | Handling ADD_RACE failed for personId: %d", personModelEntry.getKey()));
                }
            }
        } else {
            throw new ActionFailedException("Handling ADD_RACE failed. Received empty race");
        }


    }
}
