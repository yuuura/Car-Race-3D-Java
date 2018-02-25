package YuriReznik.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Envelope;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.exceptions.NoSuchActionException;
import YuriReznik.Server.exceptions.ServerProblemException;
import javafx.application.Application;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class CarRace_StartClient extends Application {

	private static final String HOSTNAME = "localhost";
	private static final int PORT = 8000;

	public static Socket socket;

	private double x, y;

	private ClientLog log;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		startClient();
	}

	/**
	 * Connect to server and start a client flow
	 */
	private void startClient() {
		try {
			socket = new Socket(HOSTNAME, PORT);
			ObjectOutputStream toServerObj = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream fromServerObj = new ObjectInputStream(socket.getInputStream());

			GlueObject glueObject = new GlueObject(toServerObj, fromServerObj, x, y, log);

			// Creates read listener threads
			new Thread(() -> {
				while (true) {
					try {
						Envelope envelope = (Envelope) glueObject.getObjectInputStream().readObject();
						glueObject.handleReceive(envelope);
					} catch (IOException | ClassNotFoundException e) {
						AlertMessage.alertMsg(null,
								String.format("Person: %s. Generic connection problem. %s Closing...", glueObject.getPerson(), e.getMessage()),
								AlertType.ERROR
						);
						break;
					} catch (ServerProblemException | NoSuchActionException | ActionFailedException e) {
						AlertMessage.alertMsg(null,
								String.format("Person: %s. Failed to execute action on server. %s Closing...", glueObject.getPerson(), e.getMessage()),
								AlertType.ERROR
						);
						break;
					} catch (NullPointerException e) {
						AlertMessage.alertMsg(null,
								String.format("Person: %s. Failed to receive correct envelope.", glueObject.getPerson()),
								AlertType.ERROR
						);
					}
				}
			}).start();

			glueObject.getLoginView().show();

		} catch (ActionFailedException e) {
			AlertMessage.alertMsg(null, "Failed to execute action on server. " + e.getMessage() + "Closing...", AlertType.ERROR);
		} catch (IOException e) {
			AlertMessage.alertMsg(null, "Person: %s. Server not available\n" + e.getMessage() , AlertType.ERROR);
		}
	}

	public CarRace_StartClient setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public ClientLog getLog() {
		return log;
	}

	public CarRace_StartClient setLog(ClientLog log) {
		this.log = log;
		return this;
	}
}