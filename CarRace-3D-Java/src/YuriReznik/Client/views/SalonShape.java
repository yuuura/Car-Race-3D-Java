package YuriReznik.Client.views;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

/**
 * Salon Shape class
 * @author yuuura87
 *
 */
public class SalonShape implements CarShapeType{
	
	private int size = 0;
    private Box top,bottom,windowRear,windowFront,topFront, front, rear, left, right, rearB,
     			wheelFrontLeft_JuntA, wheelFrontLeft_JuntB, wheelFrontRight_JuntA, wheelFrontRight_JuntB, 
     			wheelRearLeft_JuntA, wheelRearLeft_JuntB, wheelRearRight_JuntA, wheelRearRight_JuntB;
    private Cylinder wheelFrontLeft, wheelFrontRight, wheelRearRight, wheelRearLeft;
    private PhongMaterial material, materialManufacture, blackMaterial;
    private Color color;
    private Group salonGroup;
    private String carManufacture;

    public SalonShape() {
    	salonGroup = new Group();
    }
    
    /**
     * Build car with configurations
     */
    @Override
    public void buildCar(Color color, int size, String carManufacture) {
    	setColor(color);
    	setSize(size);
    	setCarManufacture(carManufacture);
    	createMaterials();
        buildShield();
    }
    
    /**
     * Cylinder object configuration
     * @param cyl
     * @param material
     * @param x
     * @param y
     * @param z
     * @param value
     * @param rotate
     */
    public void configCylinder(Cylinder cyl, PhongMaterial material, double x, double y, double z, Point3D value, Double rotate) {
    	if(material != null)
    		cyl.setMaterial(material);
    	cyl.setTranslateX(x * size);
    	cyl.setTranslateY(y * size);
    	cyl.setTranslateZ(z * size);
    	if(value != null) {
    		cyl.setRotationAxis(value);
    		cyl.setRotate(rotate);
    	}
    }
    
    /**
     * Box object configuration
     * @param box
     * @param material
     * @param x
     * @param y
     * @param z
     * @param value
     * @param rotate
     */
    public void configBox(Box box, PhongMaterial material, double x, double y, double z, Point3D value, Double rotate) {
    	if(material != null)
    		box.setMaterial(material);
    	box.setTranslateX(x * size);
    	box.setTranslateY(y * size);
    	box.setTranslateZ(z * size);
    	if(value != null) {
    		box.setRotationAxis(value);
        	box.setRotate(rotate);
    	}
    }
    
    /**
     * Build shield of car
     */
    @Override
    public void buildShield() {
    	top = new Box(size, size, 1);
    	configBox(top, material, 0, -0.1, -0.68, null, null);
    	bottom = new Box(size, size * 3, 1);
    	configBox(bottom, material, 0, 0, 0, null, null);
    	windowRear = new Box(size, size * 0.44721, 1);
    	configBox(windowRear, material, 0, -0.7764, -0.58, Rotate.X_AXIS, -26.56505);
    	windowFront = new Box(size, size * 0.48826, 1);
    	configBox(windowFront, null, 0, 0.5761468, -0.54, Rotate.X_AXIS, 34.9920202);
    	topFront = new Box(size, size * 0.8, 1);
    	configBox(topFront, material, 0, 1.136, -0.4, null, null);
    	rear = new Box(size, size * 0.6, size * 0.46666667);
    	configBox(rear, material, 0, -1.2, -0.24, null, null);
    	rearB = new Box(size, 1, size * 0.46666667);
    	configBox(rearB, materialManufacture, 0, -1.52, -0.24, null, null);
    	front = new Box(size,size * 0.4, 3);
    	configBox(front, material, 0, 1.52, -0.2, Rotate.X_AXIS, 92d);
    	left = new Box(size * 0.4, size * 3, 1);
    	configBox(left, material, 0.5, 0, -0.195, Rotate.Y_AXIS, 90d);
    	right = new Box(size * 0.4, size * 3, 1);
    	configBox(right, material, -0.5, 0, -0.195, Rotate.Y_AXIS, 90d);
        
    	wheelFrontLeft = new Cylinder(size * 0.2, size * 0.1);
    	configCylinder(wheelFrontLeft, blackMaterial, 0.5, 1.23, 0.005, Rotate.Z_AXIS, 90d);
        wheelFrontLeft_JuntA = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelFrontLeft_JuntA, null, 0.56, 1.23, 0.005, Rotate.X_AXIS, 0d);
        wheelFrontLeft_JuntB = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelFrontLeft_JuntB, null, 0.56, 1.23, 0.005, Rotate.X_AXIS, 90d);
        
        wheelFrontRight = new Cylinder(size * 0.2, size * 0.1);
        configCylinder(wheelFrontRight, blackMaterial, -0.5, 1.23, 0.005, Rotate.Z_AXIS, 90d);
        wheelFrontRight_JuntA = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelFrontRight_JuntA, null, -0.56, 1.23, 0.005, Rotate.X_AXIS, 0d);
        wheelFrontRight_JuntB = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelFrontRight_JuntB, null, -0.56, 1.23, 0.005, Rotate.X_AXIS, 90d);
        
        wheelRearRight = new Cylinder(size * 0.2, size * 0.1);
        configCylinder(wheelRearRight, blackMaterial, -0.5, -0.9, 0.005, Rotate.Z_AXIS, 90d);
        wheelRearRight_JuntA = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelRearRight_JuntA, null, -0.56, -0.9, 0.005, Rotate.X_AXIS, 0d);
        wheelRearRight_JuntB = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelRearRight_JuntB, null, -0.56, -0.9, 0.005, Rotate.X_AXIS, 90d);
        
        wheelRearLeft = new Cylinder(size * 0.2, size * 0.1);
        configCylinder(wheelRearLeft, blackMaterial, 0.5, -0.9, 0.005, Rotate.Z_AXIS, 90d);
        wheelRearLeft_JuntA = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelRearLeft_JuntA, null, 0.56, -0.9, 0.005, Rotate.X_AXIS, 0d);
        wheelRearLeft_JuntB = new Box(size * 0.01, size*0.3, 0.6);
        configBox(wheelRearLeft_JuntB, null, 0.56, -0.9, 0.005, Rotate.X_AXIS, 90d);
    	
        salonGroup.getChildren().addAll(
        		bottom, 
        		front, 
        		left, 
        		right, 
        		wheelFrontLeft, 
        		wheelFrontRight, 
        		wheelRearLeft, 
        		wheelRearRight, 
        		rear, 
        		topFront, 
        		windowFront, 
        		windowRear, 
        		top, 
        		rearB, 
        		wheelFrontLeft_JuntA, 
        		wheelFrontLeft_JuntB, 
        		wheelFrontRight_JuntA, 
        		wheelFrontRight_JuntB, 
        		wheelRearLeft_JuntA, 
        		wheelRearLeft_JuntB, 
        		wheelRearRight_JuntA, 
        		wheelRearRight_JuntB);
    }
    
    /**
     * Create material for color and manufacturer brand
     */
    public void createMaterials() {
    	material = new PhongMaterial();
        material.setDiffuseColor(color);
        materialManufacture = new PhongMaterial();
    	materialManufacture.setDiffuseMap(new Image(carManufacture, 110d, 110d, true, true));
    	blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.BLACK);
	}

	public Group getGroup() {
		return salonGroup;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public void setCarManufacture(String carManufacture) {
		this.carManufacture = carManufacture;
	}
	@Override
	public Box getWheelFrontLeft_JuntA() {
		return wheelFrontLeft_JuntA;
	}
	@Override
	public Box getWheelFrontLeft_JuntB() {
		return wheelFrontLeft_JuntB;
	}
	@Override
	public Box getWheelFrontRight_JuntA() {
		return wheelFrontRight_JuntA;
	}
	@Override
	public Box getWheelFrontRight_JuntB() {
		return wheelFrontRight_JuntB;
	}
	@Override
	public Box getWheelRearLeft_JuntA() {
		return wheelRearLeft_JuntA;
	}
	@Override
	public Box getWheelRearLeft_JuntB() {
		return wheelRearLeft_JuntB;
	}
	@Override
	public Box getWheelRearRight_JuntA() {
		return wheelRearRight_JuntA;
	}
	@Override
	public Box getWheelRearRight_JuntB() {
		return wheelRearRight_JuntB;
	}
}