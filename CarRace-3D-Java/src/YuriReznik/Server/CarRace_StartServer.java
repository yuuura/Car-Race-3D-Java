package YuriReznik.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import YuriReznik.Message.*;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.exceptions.NoSuchActionException;
import javafx.application.Application;
import javafx.stage.Stage;

public class CarRace_StartServer extends Application {

    private Map<Long, Model> personIdToModelLookup = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private Socket socket;
    private int portNumber = 8000;
    private int clientNo = 0;
    private ServerLog log;

    @Override
    public void start(Stage primaryStage) throws Exception {
        log = new ServerLog();
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(portNumber);
                while (true) {
                    socket = serverSocket.accept();
                    new Thread(new HandleAClient(socket, clientNo++)).start();
                }
            } catch (IOException ex) {
            } finally {
                try { serverSocket.close(); } catch (IOException e) { }
            }
        }).start();
    }

    class HandleAClient implements Runnable {
        private Socket socket;
        private ObjectOutputStream outputToClientObj;
        private ObjectInputStream inputFromClientObj;
        private int clientID;
        private Model model;

        /**
         * Construct a thread
         */
        public HandleAClient(Socket socket, int clientID) {
            this.socket = socket;
            this.clientID = clientID;
            this.model = new Model(clientID, log, personIdToModelLookup);
        }

        /**
         * Run a thread
         */
        public void run() {
            try {
                outputToClientObj = new ObjectOutputStream(socket.getOutputStream());
                inputFromClientObj = new ObjectInputStream(socket.getInputStream());
                model.setObjectOutputStream(outputToClientObj);

                // Continuously serve the client
                while (true) { // Receive client YuriReznik.Message from the client
                    try {
                        Envelope envelope = (Envelope) inputFromClientObj.readObject();
                        model.handle(envelope);
                    } catch (ActionFailedException | NoSuchActionException e) {
                        log.printMsg("ERROR | " + e.getMessage());
                        Envelope envelop = new Envelope();
                        envelop.setAnswer(Answer.PROBLEM);
                        outputToClientObj.writeObject(envelop);
                    } catch (ClassNotFoundException e) {

                    }

                }

            } catch (SocketException ex) {
                System.out.println("here");
            }
            catch (IOException ex) {}
            finally {
                try { socket.close(); } catch (Exception e) { }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
