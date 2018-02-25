package YuriReznik.Message;

import java.io.Serializable;

/**
 * Enum of car manufatures
 */
public enum CarManufacture implements Serializable {

    MERCEDES("Mercedes"),
    AUDI("Audi"),
    BMW("BMW"),
    SEAT("Seat");


    private String manufactureName;

    private CarManufacture(String manufactureName) {
        this.manufactureName = manufactureName;
    }

    public String getManufactureName() {
        return manufactureName;
    }

    public static CarManufacture fromString(String manufactureName) {
        for (CarManufacture carManufacture : CarManufacture.values()) {
            if (carManufacture.manufactureName.equalsIgnoreCase(manufactureName)) {
                return carManufacture;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return manufactureName;
    }
}
