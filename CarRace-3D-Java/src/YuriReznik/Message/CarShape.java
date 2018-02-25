package YuriReznik.Message;

import java.io.Serializable;

/**
 * Enum that hold car shape types
 */
public enum CarShape implements Serializable {

    SPORT("sport"),
    SALON("salon"),
    CABRIOLET("cabriolet");



    private String shapeName;
    private CarShape(String shapeName) {
        this.shapeName = shapeName;
    }

    public String getShapeName() {
        return shapeName;
    }

    public static CarShape fromString(String shapeName) {
        for (CarShape carShape : CarShape.values()) {
            if (carShape.shapeName.equalsIgnoreCase(shapeName)) {
                return carShape;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return shapeName;
    }
}
