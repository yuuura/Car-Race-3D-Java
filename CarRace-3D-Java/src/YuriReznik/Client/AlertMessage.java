package YuriReznik.Client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AlertMessage {

	private static ClientLog log;

	public static void alertMsg(Stage stg, String msg, AlertType alertType) {
		System.out.printf("== CLIENT == |%-5.5s | %s\n", alertType, msg);
		Platform.runLater(() -> {
			Alert alert = new Alert(alertType);
			alert.initOwner(stg);
			alert.setTitle(alertType.toString());
			alert.setContentText(msg);
			alert.show();
			if (log != null) {
				log.printMsg(String.format("%-5.5s | %s\n", alertType, msg));
			}
		});
	}

	public static void setLogWindow(ClientLog log) {
		AlertMessage.log = log;
	}
}
