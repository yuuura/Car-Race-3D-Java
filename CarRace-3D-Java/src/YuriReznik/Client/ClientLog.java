package YuriReznik.Client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ClientLog extends Stage {
	private ScrollPane srcPane;
	private VBox vBoxInSrcPane;
	private BorderPane mainFrame;
	private Scene scene;

	public ClientLog() {
		srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		vBoxInSrcPane = new VBox(3);
		mainFrame = new BorderPane();
		scene = new Scene(mainFrame);
		srcPane.setContent(vBoxInSrcPane);
		mainFrame.setCenter(srcPane);
		setScene(scene);
		setTitle("Client Logger");
		setX(Screen.getPrimary().getVisualBounds().getWidth() - 650);
		setY(Screen.getPrimary().getVisualBounds().getHeight() - 500);
		setHeight(500);
		setWidth(650);
	}

	public void printMsg(String str) {
		Platform.runLater(() -> {
			Label action = new Label(str);
			action.setFont(Font.font("Monospaced"));
			vBoxInSrcPane.getChildren().add(action);
			srcPane.setVvalue(action.getScaleY() + action.getHeight());
		});
		
	}
}