package YuriReznik.Client.views;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

/**
 * Car shape interface
 * @author yuuura87
 *
 */
public interface CarShapeType {

	public void buildCar(Color color, int size, String carManufacture);
	public void buildShield();
	public Group getGroup();
	public Box getWheelFrontLeft_JuntA();
	public Box getWheelFrontLeft_JuntB();
	public Box getWheelFrontRight_JuntA();
	public Box getWheelFrontRight_JuntB();
	public Box getWheelRearLeft_JuntA();
	public Box getWheelRearLeft_JuntB();
	public Box getWheelRearRight_JuntA();
	public Box getWheelRearRight_JuntB();
}
