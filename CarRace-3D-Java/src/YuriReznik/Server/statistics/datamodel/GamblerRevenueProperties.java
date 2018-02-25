package YuriReznik.Server.statistics.datamodel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;


public class GamblerRevenueProperties {
    private final SimpleStringProperty gamblerId;
    private final SimpleStringProperty gamblerName;
    private final SimpleStringProperty raceId;
    private final SimpleDoubleProperty revenue;

    public GamblerRevenueProperties(String gamblerId, String gamblerName, String raceId, Double revenue) {
        this.gamblerId = new SimpleStringProperty(gamblerId);
        this.gamblerName = new SimpleStringProperty(gamblerName);
        this.raceId = new SimpleStringProperty(raceId);
        this.revenue = new SimpleDoubleProperty(revenue);
    }

    public String getGamblerId() {
        return gamblerId.get();
    }

    public String getGamblerName() {
        return gamblerName.get();
    }

    public String getRaceId() {
        return raceId.get();
    }

    public Double getRevenue() {
        return revenue.get();
    }
}
