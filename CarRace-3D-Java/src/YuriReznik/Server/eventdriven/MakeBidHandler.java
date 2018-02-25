package YuriReznik.Server.eventdriven;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import YuriReznik.Message.Bid;
import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Message.Race;
import YuriReznik.Server.Model;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.persistancy.BidDao;
import YuriReznik.Server.persistancy.PersonDao;
import YuriReznik.Server.persistancy.RaceDao;

/**
 * Handles MAKE_BID request/reply
 */
public class MakeBidHandler implements EnvelopHandler {

    private Model model;
    private BidDao bidDao = BidDao.INSTANCE;
    private PersonDao personDao = PersonDao.INSTANCE;
    private RaceDao raceDao = RaceDao.INSTANCE;

    public MakeBidHandler(Model model) {
        this.model = model;
    }

    @Override
    public void handle(Envelope envelope) {
        Envelope envelopeToSend = new Envelope();
        Bid bidToPlace = envelope.getBid();
        model.getLog().printMsg(String.format("INFOR | Received Bid for race: %d, for carId: %d from personId: %d",
                bidToPlace.getRaceId(), bidToPlace.getCarId(), bidToPlace.getPersonId()));

        bidToPlace.setPersonId(model.getPersonId());
        bidDao.addBid(bidToPlace);
        envelopeToSend.setPerson(personDao.getPersonById(bidToPlace.getPersonId()));

        envelopeToSend.setBid(bidToPlace);
        try {
            model.getObjectOutputStream().writeObject(envelopeToSend.setMessageType(MessageType.MAKE_BID));
        } catch (IOException e) {
            throw new ActionFailedException("Handling MAKE_BID failed " + e, e);
        }

        startRaceIfNeededWithMaximumMoney();
    }

    /**
     * Send request to client to start race with maximum money if found
     */
    private void startRaceIfNeededWithMaximumMoney() {
        model.getLog().printMsg("INFOR | Checking if there are need to start a race...");

        Map<Long, Long> racesEligibleToStart = bidDao.getRacesEligibleToStart();
        model.getLog().printMsg(String.format("INFOR | Found %d race/s to start", racesEligibleToStart.size()));

        Optional<Map.Entry<Long, Long>> raceWithMaxMoney = racesEligibleToStart.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (raceWithMaxMoney.isPresent()) {
            try {
                Race raceToStart = raceDao.getRaceById(raceWithMaxMoney.get().getKey());
                model.getLog().printMsg(String.format("INFOR | Found race to start. RaceId: %d", raceToStart.getId()));

                raceToStart.setStartDate(new Timestamp(new Date().getTime()));

                model.getLog().printMsg(String.format("INFOR | Broadcasting START_RACE: %d", raceToStart.getId()));
                model.getObjectOutputStream()
                        .writeObject(new Envelope()
                                .setMessageType(MessageType.START_RACE)
                                .setRaces(Collections.singletonList(raceToStart))
                        );
                raceDao.updateRaceStartDate(raceToStart);
            } catch (IOException e) {
                throw new ActionFailedException("Handling START_RACE failed " + e, e);
            }
        } else {
            model.getLog().printMsg(String.format("INFOR | There is no races to start.."));
        }
        model.printRaceStatistics();
    }
}
