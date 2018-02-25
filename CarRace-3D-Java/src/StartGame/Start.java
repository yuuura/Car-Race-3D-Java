package StartGame;

import java.util.Arrays;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.CarRace_StartClient;
import YuriReznik.Client.ClientLog;
import YuriReznik.Server.CarRace_StartServer;
import YuriReznik.Server.persistancy.DataBaseAccess;
import YuriReznik.Server.statistics.Statistics;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Start extends Application {

	private Button btnStartClient = new Button("Start Client");
	private Button btnStartServer = new Button("Start Server");
	private Button btnShowStatistics = new Button("Show Statistics");

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane pane = new BorderPane();
		HBox buttons = new HBox();
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(btnStartServer, btnStartClient, btnShowStatistics);
		pane.setCenter(buttons);
		pane.setStyle("-fx-background-color: orange");
		Scene scene = new Scene(pane, 400, 100);
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.setTitle("Car Race"); // Set the stage title
		primaryStage.setOnCloseRequest(event -> {
			try {
				Platform.exit();
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		btnStartServer.setOnAction(event -> {
			Platform.runLater(() -> {
				try {
					new CarRace_StartServer().start(new Stage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			btnStartServer.setDisable(true);
            btnStartClient.setDisable(false);
			btnShowStatistics.setDisable(false);
        });
		btnStartClient.setOnAction(event -> Platform.runLater(() -> {
			try {
				ClientLog log = new ClientLog();
				new CarRace_StartClient()
						.setLocation(Screen.getPrimary().getVisualBounds().getWidth() / 3, 150)
						.setLog(log)
						.start(new Stage());

				new CarRace_StartClient()
						.setLocation(Screen.getPrimary().getVisualBounds().getWidth() / 2, 150)
						.setLog(log)
						.start(new Stage());

				new CarRace_StartClient()
						.setLocation(Screen.getPrimary().getVisualBounds().getWidth() / 1.4 , 150)
						.setLog(log)
						.start(new Stage());
				btnStartClient.setDisable(true);
				AlertMessage.setLogWindow(log);
				log.show();
			} catch (Exception e) {
				e.printStackTrace(); }
        }));
		btnShowStatistics.setOnAction(event -> {
			new Statistics();
			btnShowStatistics.setDisable(true);
		});
		btnStartClient.setDisable(true);
		btnShowStatistics.setDisable(true);
		primaryStage.show(); // Display the stage
		primaryStage.setAlwaysOnTop(true);
		// Relocate windows to top
		primaryStage.setX(Screen.getPrimary().getVisualBounds().getWidth()/3);
		primaryStage.setY(0);
	}

	public static void main(String[] args) {
		if (args != null && Arrays.asList(args).contains("init")) {
			DataBaseAccess.INSTANCE.importInitialData();
		}
		launch(args);
	}
}
