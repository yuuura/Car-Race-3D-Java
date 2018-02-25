package YuriReznik.Client.views;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import YuriReznik.Client.controllers.CarController;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.CarManufacture;
import YuriReznik.Message.CarShape;
import YuriReznik.Message.CarSize;
import YuriReznik.Message.Person;
import YuriReznik.Message.Race;
import YuriReznik.Message.Winner;
import YuriReznik.Message.utils.ColorUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by yuri.
 */
public class RaceView implements View {
	
    private static final double CAMERA_INITIAL_DISTANCE = -650;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final int CAR_POS_X_1 = -107;
    private static final int CAR_POS_X_2 = -55;
    private static final int CAR_POS_X_3 = 0;
    private static final int CAR_POS_X_4 = 55;
    private static final int CAR_POS_X_5 = 115;
    private static final int WAIT_STAGE_CLOSE = 50000;
    private static final int TIME_BETWEEN_MUSIC_AND_END = 2000;
    private static final int DELAY_SPEED_CHANGE = 30000;
    public static final int WORLD_VIEW_ANGLE = -70;
    private final double MUSIC_VOLUME = 0.2;
    private final double SOUND_VOLUME = 0.7;
    
    public final String MUSIC_1 = "Music/Misirlou.mp3";
    public final String MUSIC_2 = "Music/IWannaBeYourDog.mp3";
    public final String MUSIC_3 = "Music/HowSoonIsNow.mp3";
    public final String START_ENGINE = "Music/CarEngine.mp3";
    public final String PEELS_OUT = "Music/CARPeelsOut.mp3";
    public final String APPLAUSE = "Music/SMALL_CROWD_APPLAUSE.mp3";
	
    private PerspectiveCamera camera;
    private CameraRotate cameraXform;
    private Group root, world;
    private Stage stage;
    private Scene raceScene;
    private GlueObject glueObject;
    private Race race;
    private Map<Integer, CarController> mapCars;
    private Map<Integer, Integer> mapCarPositions;
    private Map<Integer, String> mapMusic;
    private Map<Integer, Label> mapLblSpeed;
    private Background background;
    private TextEditor count;
    private MediaPlayer mediaPlayer, mediaPlayer_Idle, mediaPlayer_StartCars, mediaPlayer_end;
    private Media media, media_Idle, media_StartCars, media_end;
    private URL resource, resource_Idle, resource_StartCars, resource_end;
    private CarController carWinner;
    private Thread drive, driveEnd;
    private Timeline timeline;
    private long maxDist;
    private volatile boolean toSTOP = false;
    private boolean raceEnded = false;
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    /**
     * Create stage with configurations
     * @param glueObject
     */
    public RaceView(GlueObject glueObject) {
        this.glueObject = glueObject;
        
        root = new Group();
        world = new Group();
        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);

        stage = new Stage();
        stage.setOnCloseRequest(Event::consume);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("Race");
        createRaceScene();
        stage.setScene(raceScene);
        additionalConfiguration(); 
    }

    public Race getRace() {
        return race;
    }

    /**
     * Receive race parameters and prepare cars
     * @param race
     */
    public void setRace(Race race) {
        this.race = race;
        raceConfiguration();
    }
    @Override
    public void show() {
        Platform.runLater(() -> stage.show());
    }
    @Override
    public void hide() {
        Platform.runLater(() -> stage.close());
    }
    @Override
    public Stage getStage() {
        return stage;
    }
    public GlueObject getGlueObject() {
        return glueObject;
    }
    @Override
    public void setPerson(Person person) {
        throw new UnsupportedOperationException("RaceView not supports setPerson()");
    }

    /**
     * Configure race data
     */
    public void raceConfiguration() {
    	buildMaps();
    	buildMedia();
        buildCars();
        configRace();
    }
    
    /**
     * Create camera, background and mouse handler
     */
    private void additionalConfiguration() {
    	camera = new PerspectiveCamera(true);
    	cameraXform = new CameraRotate();
    	count = new TextEditor();
    	buildCamera();
    	buildBackground();
    	handleMouse(raceScene);
    }

    /**
     * Create scene
     */
    private void createRaceScene() {
    	raceScene = new Scene(root, 1024, 768, true);
        raceScene.setFill(Color.GREY);
        stage.setOnCloseRequest(event -> {
        	if(raceEnded) { endRace(); completeRace(); }
        	else event.consume();
        });
    }

    /**
     * Start cars in background
     */
    public void startRace() {
        Platform.runLater(() -> { go(); });
    }

    /**
     * Build cars by race data
     */
    public void buildCars() {
  
    	for(int index = 0; index < race.getCars().size(); index++) {
    		mapCars.get(index).setData(
    										race.getCars().get(index).getId(),
    										CarManufacture.fromString(race.getCars().get(index).getCarManufacture().getManufactureName()), 
    										CarShape.fromString(race.getCars().get(index).getCarShape().getShapeName()), 
    										CarSize.fromString(race.getCars().get(index).getCarSize().getSizeName()),
    										ColorUtil.awtToFx(race.getCars().get(index).getColor())
    									);
    		mapCars.get(index).setTranslateX(mapCarPositions.get(index));
    		world.getChildren().add(mapCars.get(index));
    	}
    }
    
    /**
     * Start race cars
     */
    public void go() {
    	
        timeline = new Timeline(
        							new KeyFrame(Duration.seconds(0), e -> playMusic(SoundType.IDLE)), 
        							new KeyFrame(Duration.seconds(3), e -> count.setText("3")), 
        							new KeyFrame(Duration.seconds(4), e -> count.setText("2")),
        							new KeyFrame(Duration.seconds(5), e -> count.setText("1")),
        							new KeyFrame(Duration.seconds(6), e -> playMusic(SoundType.START)),
        							new KeyFrame(Duration.seconds(6), e -> {
        				        	//Platform.runLater(() -> { 
       					        		count.getLblGroup().setTranslateX(TextEditor.POS_X - 50); 
       					        		//});
      						        	count.setText("GO"); }), 
     								new KeyFrame(Duration.seconds(7), e -> count.setText(""))
      							);
        Platform.runLater(timeline::play);
    }
    
    /**
     * Sound type flags
     */
    public enum SoundType {
    	IDLE, 
    	START, 
    	END
    }
    
    /**
     * Play media by type
     * @param sound
     */
    public void playMusic(SoundType sound) {
    	switch (sound) {
    		case IDLE: 
    					mediaPlayer_Idle.play();
    					break;
    		case START: 
    					mediaPlayer_StartCars.play();
    					mediaPlayer.play();
    					break;
    		case END: 
    					mediaPlayer_end.play();
    					break;
    	default: 		break;
    	}
    }
    
    /**
     * Configure race processes
     */
    public void configRace() {
    	
    	drive = new Thread(() -> {
			while(!toSTOP) {
	    			int random1 = (new Random().nextInt(15)) + 5;
	    			int random2 = (new Random().nextInt(15)) + 5;
	    			int random3 = (new Random().nextInt(15)) + 5;
	    			int random4 = (new Random().nextInt(15)) + 5;
	    			int random5 = (new Random().nextInt(15)) + 5;
	    			Platform.runLater(() -> {
	    				mapLblSpeed.get(0).setText("1 Car Speed: " + String.valueOf(random1));
	    				mapLblSpeed.get(1).setText("2 Car Speed: " + String.valueOf(random2));
	    				mapLblSpeed.get(2).setText("3 Car Speed: " + String.valueOf(random3));
	    				mapLblSpeed.get(3).setText("4 Car Speed: " + String.valueOf(random4));
	    				mapLblSpeed.get(4).setText("5 Car Speed: " + String.valueOf(random5));
	    			});
	    			mapCars.get(0).setSpeed(random1);
	    			mapCars.get(1).setSpeed(random2);
	    			mapCars.get(2).setSpeed(random3);
	    			mapCars.get(3).setSpeed(random4);
	    			mapCars.get(4).setSpeed(random5);
	        		
	    		try {
					Thread.sleep(DELAY_SPEED_CHANGE);
				} catch (Exception e) {}
    		}			
    	});
    	
    	driveEnd = new Thread(() -> {
    		try {
				Thread.sleep(TIME_BETWEEN_MUSIC_AND_END);
				endRace();
				Thread.sleep(WAIT_STAGE_CLOSE);
				completeRace();
				
			} catch (Exception e) {}
    	});
    	world.getChildren().add(count.getLblGroup());
    }
    
    /**
     * Make winner object
     */
    private synchronized void completeRace() {
     
        getGlueObject()
                .getRaceController()
                .sendWinner(new Winner()
                        .setRaceId(race.getId())
                        .setWinnerCarId(carWinner.getCarId())
                        .setDistance(maxDist));
    }
    
    /**
     * Find winner car
     */
    public void endRace() {
    	raceEnded = true;
    	playMusic(SoundType.END);
    	maxDist = -1;
    	carWinner = null;
		for(CarController car : mapCars.values()) {
    		car.setSpeed(0);
    		if(car.getDistance() > maxDist){
    			maxDist = car.getDistance();
    			carWinner = car;
    		}
		}
		count.setSize(TextEditor.TEXT_SIZE / 4);
		Platform.runLater(() -> { 
			count.getLblGroup().setTranslateX(TextEditor.POS_X - 20);
			count.setText("Winner: \n" + carWinner.getCarManufacture().getManufactureName() + 
									"\n" + carWinner.getCarShape() + 
									"\n" + carWinner.getCarSize() + "\n");
		});
    }
    
    /**
     * Build media configurations with handlers
     */
    public void buildMedia() {
    	
       	resource = getClass().getResource(mapMusic.get(ThreadLocalRandom.current().nextInt(0, 2)));
        media = new Media(resource.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(MUSIC_VOLUME);        
        resource_Idle = getClass().getResource(START_ENGINE);
		media_Idle = new Media(resource_Idle.toString());
		mediaPlayer_Idle = new MediaPlayer(media_Idle);
		mediaPlayer_Idle.setVolume(SOUND_VOLUME);		
		resource_StartCars = getClass().getResource(PEELS_OUT);
        media_StartCars = new Media(resource_StartCars.toString());
        mediaPlayer_StartCars = new MediaPlayer(media_StartCars);
        mediaPlayer_StartCars.setVolume(SOUND_VOLUME);       
        resource_end = getClass().getResource(APPLAUSE);
        media_end = new Media(resource_end.toString());
        mediaPlayer_end = new MediaPlayer(media_end);
        mediaPlayer_end.setVolume(SOUND_VOLUME);
    	
    	mediaPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				toSTOP = true;
				driveEnd.start();
			}
        });
    	
    	mediaPlayer.setOnPlaying(new Runnable() {

    		@Override
    		public void run() {
    			drive.start();
    		}
    	});
    }
    
    /**
     * Build hash maps
     */
    public void buildMaps() {
    	
    	mapCars = new HashMap<Integer, CarController>();
        mapCarPositions = new HashMap<Integer, Integer>();
        mapMusic = new HashMap<Integer, String>();
        mapLblSpeed = new HashMap<Integer, Label>();
    	
    	for(int index = 0; index < race.getCars().size(); index++)
    		mapCars.put(index, new CarController());
    	
    	mapCarPositions.put(0, CAR_POS_X_1);
    	mapCarPositions.put(1, CAR_POS_X_2);
    	mapCarPositions.put(2, CAR_POS_X_3);
    	mapCarPositions.put(3, CAR_POS_X_4);
    	mapCarPositions.put(4, CAR_POS_X_5);   	
    	mapMusic.put(0, MUSIC_1);
    	mapMusic.put(1, MUSIC_2);
    	mapMusic.put(2, MUSIC_3);   	
    	mapLblSpeed.put(0, count.getLblSpeed1());
    	mapLblSpeed.put(1, count.getLblSpeed2());
    	mapLblSpeed.put(2, count.getLblSpeed3());
    	mapLblSpeed.put(3, count.getLblSpeed4());
    	mapLblSpeed.put(4, count.getLblSpeed5());
    }
    
    /**
     * Add background to global world
     */
    public void buildBackground() {
    	background = new Background();
        background.rx.setAngle(WORLD_VIEW_ANGLE);
        world.getChildren().addAll(background);
    }
    
    /**
     * Create camera and add to Scene
     */
    private void buildCamera() {      
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(camera);
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);       
        raceScene.setCamera(camera);
    }
    
    /**
     * Mouse handler
     * @param scene
     * @param root
     */
    private void handleMouse(Scene scene) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX); 
                mouseDeltaY = (mousePosY - mouseOldY); 
                
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*ROTATION_SPEED);  
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*ROTATION_SPEED);  
                }
            }
        });
    }
    
    /**
     * Text editor group for graphics
     * @author yuuura87
     *
     */
    class TextEditor extends Group {
    	private static final int POS_X = -27;
    	private static final int POS_Y = -120;
    	private static final int POS_Z = -60;
    	private final int POS_X_SPEED = -140;
    	private final int POS_Z_SPEED = -10;
    	private final int POS_Y_SPEED1 = -160;
    	private final int POS_Y_SPEED2 = -150;
    	private final int POS_Y_SPEED3 = -140;
    	private final int POS_Y_SPEED4 = -130;
    	private final int POS_Y_SPEED5 = -120;
    	private static final int TEXT_SIZE = 100;
    	private final int TEXT_SIZE_SPEED = 10;
    	private String txt = "", speed1, speed2, speed3, speed4, speed5;
    	private Label lblCount, lblSpeed1, lblSpeed2, lblSpeed3, lblSpeed4, lblSpeed5;
    	private String txtCSSStyle;
		public Group lblGroup = new Group();
		public TextEditor() {
			
			txtCSSStyle = "-fx-text-fill: white;";			
			buildLabels();			
		}
		
		public void buildLabels() {
			lblCount = new Label(this.txt);
			buildLabel(lblCount, TEXT_SIZE, txtCSSStyle, POS_X, POS_Y, POS_Z);			
			lblSpeed1 = new Label(speed1);
			buildLabel(lblSpeed1, TEXT_SIZE_SPEED, txtCSSStyle, POS_X_SPEED, POS_Y_SPEED1, POS_Z_SPEED);			
			lblSpeed2 = new Label(speed2);
			buildLabel(lblSpeed2, TEXT_SIZE_SPEED, txtCSSStyle, POS_X_SPEED, POS_Y_SPEED2, POS_Z_SPEED);			
			lblSpeed3 = new Label(speed3);
			buildLabel(lblSpeed3, TEXT_SIZE_SPEED, txtCSSStyle, POS_X_SPEED, POS_Y_SPEED3, POS_Z_SPEED);			
			lblSpeed4 = new Label(speed4);
			buildLabel(lblSpeed4, TEXT_SIZE_SPEED, txtCSSStyle, POS_X_SPEED, POS_Y_SPEED4, POS_Z_SPEED);			
			lblSpeed5 = new Label(speed5);
			buildLabel(lblSpeed5, TEXT_SIZE_SPEED, txtCSSStyle, POS_X_SPEED, POS_Y_SPEED5, POS_Z_SPEED);
			
			lblGroup.getChildren().addAll(lblCount, lblSpeed1, lblSpeed2, lblSpeed3, lblSpeed4, lblSpeed5);
		}
		
		/**
		 * Build labels
		 * @param lbl
		 * @param txtSize
		 * @param style
		 * @param x
		 * @param y
		 * @param z
		 */
		public void buildLabel(Label lbl, int txtSize, String style, int x, int y, int z) {
			lbl.setFont(new Font(txtSize));
			lbl.setStyle(style);
			lbl.setTranslateX(x);
			lbl.setTranslateY(y);
			lbl.setTranslateZ(z);
		}
		
		public Group getLblGroup() {
			return lblGroup;
		}
		public void setText(String count) {
			lblCount.setText(count);
		}
		public void setSize(int size) {
			lblCount.setFont(new Font(size));
		}
		public Label getLblSpeed1() {
			return lblSpeed1;
		}
		public Label getLblSpeed2() {
			return lblSpeed2;
		}
		public Label getLblSpeed3() {
			return lblSpeed3;
		}
		public Label getLblSpeed4() {
			return lblSpeed4;
		}
		public Label getLblSpeed5() {
			return lblSpeed5;
		}
	}
}
