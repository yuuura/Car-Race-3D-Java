package YuriReznik.Server.statistics.datamodel;

import javafx.beans.property.SimpleStringProperty;


public class GamblerProperties {
    private final SimpleStringProperty car;
    private final SimpleStringProperty bet;
    private final SimpleStringProperty profit;

    public GamblerProperties(String car, String bet, String profit) {
        this.car = new SimpleStringProperty(car);
        this.bet = new SimpleStringProperty(bet);
        this.profit = new SimpleStringProperty(profit);
    }

    public String getCar() {
        return car.get();
    }

    public String getBet() {
        return bet.get();
    }

    public String getProfit() {
        return profit.get();
    }
}
