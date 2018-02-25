package YuriReznik.Client.controllers;
import java.util.HashMap;
import java.util.Map;

import YuriReznik.Client.views.Background;
import YuriReznik.Client.views.CabrioletShape;
import YuriReznik.Client.views.CarShapeType;
import YuriReznik.Client.views.RaceView;
import YuriReznik.Client.views.SalonShape;
import YuriReznik.Client.views.SportShape;
import YuriReznik.Message.CarManufacture;
import YuriReznik.Message.CarShape;
import YuriReznik.Message.CarSize;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Race car object controller
 * @author yuuura87
 *
 */
public class CarController extends Group{

	private final int CAR_SIZE_LARGE = 35;
	private final int CAR_SIZE_REGULAR = 25;
	private final int CAR_SIZE_MINI = 15;	
	private final int CAR_POS_Y = 80;
    private final int CAR_POS_Z = -235;
    private final String BREND_MERCEDEC = "YuriReznik/Client/views/Images/Mercedes.jpeg";
    private final String BREND_BMW = "YuriReznik/Client/views/Images/BMW.png";
    private final String BREND_AUDI = "YuriReznik/Client/views/Images/Audi.png";
    private final String BREND_SEAT = "YuriReznik/Client/views/Images/Seat.jpeg";
    final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
    
    private Map<CarShape, CarShapeType> mapCarShapes;
    private Map<CarManufacture, String> mapCarManufacture;
    private Map<CarSize, Integer> mapCarSize;
    private long distance = 0;
    private long carId;
    private CarManufacture carManufacture;
    private CarSize carSize;
    private CarShape carShape;
    private Color carColor;
    private Timeline tl;
    
    int move = 1;
	int stop = 0;
	boolean toEnd = false;
    
    /**
     * Build configurations
     */
    public CarController() {
    	buildMaps();
        getTransforms().addAll(rz, ry, rx);
        setTranslateY(CAR_POS_Y);
        setTranslateZ(CAR_POS_Z);
        rx.setAngle(180 - RaceView.WORLD_VIEW_ANGLE);
        ry.setAngle(180);
        createTimeline();
    }
    
    /**
     * Build hash maps for car shapes, car manufacturer and car size
     */
    public void buildMaps() {
    	mapCarShapes = new HashMap<CarShape, CarShapeType>();
    	mapCarShapes.put(CarShape.SALON, new SalonShape());
    	mapCarShapes.put(CarShape.CABRIOLET, new CabrioletShape());
    	mapCarShapes.put(CarShape.SPORT, new SportShape());
    	
    	mapCarManufacture = new HashMap<CarManufacture, String>();
    	mapCarManufacture.put(CarManufacture.MERCEDES, BREND_MERCEDEC);
    	mapCarManufacture.put(CarManufacture.BMW, BREND_BMW);
    	mapCarManufacture.put(CarManufacture.AUDI, BREND_AUDI);
    	mapCarManufacture.put(CarManufacture.SEAT, BREND_SEAT);
    	
    	mapCarSize = new HashMap<CarSize, Integer>();
    	mapCarSize.put(CarSize.MINI, CAR_SIZE_MINI);
    	mapCarSize.put(CarSize.REGULAR, CAR_SIZE_REGULAR);
    	mapCarSize.put(CarSize.LARGE, CAR_SIZE_LARGE);
    }
    
    /**
     * Receive race data for car object
     * @param carId
     * @param carManufacture
     * @param carShape
     * @param carSize
     * @param color
     */
    public void setData(long carId, CarManufacture carManufacture, CarShape carShape, CarSize carSize, Color color) {
    	setCarId(carId);
    	setCarManufacture(carManufacture);
    	setCarShape(carShape);
    	setCarSize(carSize);
    	setCarColor(color);
    	mapCarShapes.get(carShape).buildCar(color, mapCarSize.get(carSize), mapCarManufacture.get(carManufacture));
    	getChildren().add(mapCarShapes.get(carShape).getGroup());
    }
    
    /**
     * Move car on road
     * @param index
     */
    public void moveCar(int index) {
    	distance++;
    	if(getTranslateZ() > (Background.ROAD_LENGTH - 500)) {
    		setTranslateY(CAR_POS_Y);
    		setTranslateZ(CAR_POS_Z);
    		if(toEnd) tl.stop();
    	}
    	else{
    		setTranslateY(getTranslateY() - (index * 0.365));
        	setTranslateZ(getTranslateZ() + index);      	
        	mapCarShapes.get(carShape).getWheelFrontLeft_JuntA().setRotate( mapCarShapes.get(carShape).getWheelFrontLeft_JuntA().getRotate() + index * 5 );
        	mapCarShapes.get(carShape).getWheelFrontLeft_JuntB().setRotate( mapCarShapes.get(carShape).getWheelFrontLeft_JuntB().getRotate() + index * 5 );
        	mapCarShapes.get(carShape).getWheelFrontRight_JuntA().setRotate( mapCarShapes.get(carShape).getWheelFrontRight_JuntA().getRotate() + index * 5 );
        	mapCarShapes.get(carShape).getWheelFrontRight_JuntB().setRotate( mapCarShapes.get(carShape).getWheelFrontRight_JuntB().getRotate() + index * 5 );
        	mapCarShapes.get(carShape).getWheelRearLeft_JuntA().setRotate( mapCarShapes.get(carShape).getWheelRearLeft_JuntA().getRotate() + index * 5 );
        	mapCarShapes.get(carShape).getWheelRearLeft_JuntB().setRotate( mapCarShapes.get(carShape).getWheelRearLeft_JuntB().getRotate() + index * 5 );
        	mapCarShapes.get(carShape).getWheelRearRight_JuntA().setRotate( mapCarShapes.get(carShape).getWheelRearRight_JuntA().getRotate() + index * 5 );
        	mapCarShapes.get(carShape).getWheelRearRight_JuntB().setRotate( mapCarShapes.get(carShape).getWheelRearRight_JuntB().getRotate() + index * 5 );
    	}   	
    }
    
    /**
     * Create time line
     */
    public void createTimeline() {
		EventHandler<ActionEvent> eventHandler = e -> { moveCar(move); };
		tl = new Timeline();
		tl.setCycleCount(Timeline.INDEFINITE);
		KeyFrame kf = new KeyFrame(Duration.millis(50), eventHandler);
		tl.getKeyFrames().add(kf);
	}
    
    /**
     * Set speed
     * @param speed
     */
    public void setSpeed(int speed) {
		if (speed == stop) {
			toEnd = true;
		} else {
			tl.setRate(speed);
			tl.play();
		}
	}

    public CarManufacture getCarManufacture() {
		return carManufacture;
	}
	public CarSize getCarSize() {
		return carSize;
	}
	public CarShape getCarShape() {
		return carShape;
	}
	public Color getCarColor() {
		return carColor;
	}
	public void setCarManufacture(CarManufacture carManufacture) {
		this.carManufacture = carManufacture;
	}
	public void setCarSize(CarSize carSize) {
		this.carSize = carSize;
	}
	public void setCarShape(CarShape carShape) {
		this.carShape = carShape;
	}
	public void setCarColor(Color carColor) {
		this.carColor = carColor;
	}
	public void setCarId(long carId) {
    	this.carId = carId;
    }
    public long getCarId() {
    	return carId;
    }
    public long getDistance() {
    	return distance;
    }
}
