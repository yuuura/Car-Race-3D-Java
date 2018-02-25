package YuriReznik.Server.statistics.datamodel;

import javafx.beans.property.SimpleStringProperty;

/**
 * TableView RaceProperties Data moel
 */
public class RaceProperties {
    private final SimpleStringProperty car;
    private final SimpleStringProperty gamblers;
    private final SimpleStringProperty winnerCar;

    public RaceProperties(String car, String gamblers, boolean winnerCar) {
        this.car = new SimpleStringProperty(car);
        this.gamblers = new SimpleStringProperty(gamblers);
        this.winnerCar = new SimpleStringProperty(String.valueOf(winnerCar));
    }

    public String getCar() {
        return car.get();
    }

    public String getGamblers() {
        return gamblers.get();
    }

    public String getWinnerCar() {
        return winnerCar.get();
    }

}
