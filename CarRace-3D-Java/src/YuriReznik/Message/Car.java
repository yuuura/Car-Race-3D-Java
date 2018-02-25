package YuriReznik.Message;

import java.awt.*;
import java.io.Serializable;

/**
 * Object that holds Car information
 */
public class Car implements Serializable {

    private static final long serialVersionUID = 5683172138859738969L;
    private long id;
    private CarManufacture carManufacture;
    private Color color;
    private CarSize carSize;
    private CarShape carShape;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CarManufacture getCarManufacture() {
        return carManufacture;
    }

    public void setCarManufacture(CarManufacture carManufacture) {
        this.carManufacture = carManufacture;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public CarSize getCarSize() {
        return carSize;
    }

    public void setCarSize(CarSize carSize) {
        this.carSize = carSize;
    }

    public CarShape getCarShape() {
        return carShape;
    }

    public void setCarShape(CarShape carShape) {
        this.carShape = carShape;
    }
    
    @Override
	public String toString() {
		return getCarManufacture() + ", " + getCarShape() + ", " + getCarSize();
	}

}
