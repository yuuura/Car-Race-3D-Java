package YuriReznik.Message;

import java.io.Serializable;

/**
 * Enum that holds types of car sizes
 */
public enum CarSize implements Serializable {
    MINI("mini"),
    REGULAR("regular"),
    LARGE("large");

    private String sizeName;
    private CarSize(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public static CarSize fromString(String shapeName) {
        for (CarSize carSize : CarSize.values()) {
            if (carSize.sizeName.equalsIgnoreCase(shapeName)) {
                return carSize;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return sizeName;
    }
}
