package YuriReznik.Server.eventdriven;

import java.io.IOException;

import YuriReznik.Message.Envelope;
import YuriReznik.Message.MessageType;
import YuriReznik.Server.Model;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.persistancy.CarDao;

/**
 * Handles get cars
 */
public class GetCarsHandler implements EnvelopHandler {

    private Model model;
    private CarDao carDao = CarDao.INSTANCE;

    public GetCarsHandler(Model model) {
        this.model = model;
    }

    @Override
    public void handle(Envelope envelope) {
        Envelope envelopeToSend = new Envelope();

        envelopeToSend.setCars(carDao.getAllCars());

        try {
            model.getObjectOutputStream().writeObject(envelopeToSend.setMessageType(MessageType.GET_CARS));
        } catch (IOException e) {
            throw new ActionFailedException("Handling GET_CARS failed " + e, e);
        }
    }
}
