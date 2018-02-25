package YuriReznik.Server;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ServerLog extends Stage {
	private ScrollPane srcPane;
	private VBox vBoxInSrcPane;
	private BorderPane mainFrame;
	private Scene scene;

	public ServerLog() {
		srcPane = new ScrollPane();
		srcPane.setFitToHeight(true);
		srcPane.setFitToWidth(true);
		vBoxInSrcPane = new VBox(3);
		mainFrame = new BorderPane();
		scene = new Scene(mainFrame);
		srcPane.setContent(vBoxInSrcPane);
		mainFrame.setCenter(srcPane);
		setScene(scene);
		setTitle("Server Logger");
		setX(0);
		setY(0);
		setHeight(500);
		setWidth(650);
		this.show();
	}

	public void printMsg(String str) {
		Platform.runLater(() -> {
			Label action = new Label(str);
			action.setFont(Font.font("Monospaced"));
			vBoxInSrcPane.getChildren().add(action);
			srcPane.setVvalue(action.getScaleY() + action.getHeight());
			System.out.println("== SERVER == |" + str);
		});
		
	}
}