package YuriReznik.Server.eventdriven;

import java.io.IOException;
import java.util.List;

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
 * Handles GET_RACES request/reply
 */
public class GetRacesAndBidsHandler implements EnvelopHandler {

    private Model model;
    private RaceDao raceDao = RaceDao.INSTANCE;
    private BidDao bidDao = BidDao.INSTANCE;

    public GetRacesAndBidsHandler(Model model) {
        this.model = model;
    }

    @Override
    public void handle(Envelope envelope) {
        Envelope envelopeToSend = new Envelope();
        List<Race> allRaces = raceDao.getAllRaces();


        List<Bid> bidsByPersonId = bidDao.getBidsByPersonId(model.getPersonId());
        envelopeToSend.setPersonBids(bidsByPersonId);

        if (allRaces.size() > 0) {
            envelopeToSend.setRaces(allRaces);
        } else {
            envelopeToSend.setAnswer(Answer.NO);
        }
        try {
            model.getObjectOutputStream().writeObject(envelopeToSend.setMessageType(MessageType.GET_RACES_AND_BIDS));
        } catch (IOException e) {
            throw new ActionFailedException("Handling GET_RACES_AND_BIDS failed " + e, e);
        }
    }

}
