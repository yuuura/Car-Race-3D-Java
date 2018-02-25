package YuriReznik.Server.statistics.datamodel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;


public class SystemRevenueProperties {
    private final SimpleStringProperty race;
    private final SimpleDoubleProperty revenue;
    private final SimpleStringProperty car1;
    private final SimpleStringProperty car2;
    private final SimpleStringProperty car3;
    private final SimpleStringProperty car4;
    private final SimpleStringProperty car5;

    public SystemRevenueProperties(String race, Double revenue, String car1, String car2, String car3, String car4, String car5) {
        this.race = new SimpleStringProperty(race);
        this.revenue = new SimpleDoubleProperty(revenue);
        this.car1 = new SimpleStringProperty(car1);
        this.car2 = new SimpleStringProperty(car2);
        this.car3 = new SimpleStringProperty(car3);
        this.car4 = new SimpleStringProperty(car4);
        this.car5 = new SimpleStringProperty(car5);
    }

    public String getRace() {
        return race.get();
    }

    public Double getRevenue() {
        return revenue.get();
    }

    public String getCar1() {
        return car1.get();
    }

    public String getCar2() {
        return car2.get();
    }

    public String getCar3() {
        return car3.get();
    }

    public String getCar4() {
        return car4.get();
    }

    public String getCar5() {
        return car5.get();
    }
}
