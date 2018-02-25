package YuriReznik.Client.views;

import com.sun.javafx.scene.control.skin.TableViewSkinBase;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Bid;
import YuriReznik.Message.Car;
import YuriReznik.Message.Person;
import YuriReznik.Message.Race;
import YuriReznik.Message.utils.ParseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Min view with races and bids
 */
public class MainView implements View {

    public final static int RACES_COUNT = 1;
    public final static int CARS_NUM = 5;

    private List<TableView<CarProperties>> tableViews = new ArrayList<>();
    private List<ObservableList<CarProperties>> raceDatas = new ArrayList<>();
    private List<Label> lblRaces = new ArrayList<>();
    private List<TableColumn<CarProperties, String>> moneyBetColumns = new ArrayList<>();
    private List<VBox> raceVboxes = new ArrayList<>();
    private Button btnAddRace;

    private List<Race> races;
    private Person person;
    private Map<Long, Map<Long, Integer>> raceIdToCarIdToRowIdLookup = new HashMap<>();
    private Map<Long, Integer> raceIdToRaceIndexLookup = new HashMap<>();

    private Stage stage;
    private Scene sceneMain;
    private BorderPane paneMain;
    private Label lblBalance;
    private double userMoney;

    private int persistancyRacesCount = MainView.RACES_COUNT;

    private GlueObject glueObject;

    public MainView(GlueObject glueObject, double x, double y) {
        this.glueObject = glueObject;
        paneMain = new BorderPane();
        createTableViews();
        stage = new Stage();
        stage.setOnCloseRequest(Event::consume); // disable close
        stage.initStyle(StageStyle.UTILITY);
        sceneMain = new Scene(getViewMainPane(), 330, 810);
        stage.setScene(sceneMain);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setX(x);
        stage.setY(y);
    }

    public void createTableViews() {

        btnAddRace = new Button("Add Race");
        btnAddRace.setPadding(new Insets(10, 10, 20, 10));
        btnAddRace.setOnAction(event -> addRaceAction());

        Label lblMain = new Label("Car Race Bets");
        lblMain.setFont(new Font(25));
        lblBalance = new Label("Balance: " + Double.toString(userMoney));
        lblBalance.setFont(new Font(17));
        VBox vBoxMainBalance = new VBox(lblMain, lblBalance);

        paneMain.setTop(new HBox(vBoxMainBalance, btnAddRace));

    }

    public void addRacesToView(List<Race> races, List<Bid> personBids) {
        tableViews.clear();
        raceDatas.clear();
        lblRaces.clear();
        moneyBetColumns.clear();
        raceVboxes.clear();

        setPersistancyRacesCount(races.size());
        this.races = races;

        for (Race race : races) {
            createRaceTableView(race.getId());
        }

        VBox race = new VBox(raceVboxes.toArray(new VBox[getPersistancyRacesCount()]));
        ScrollPane sp = new ScrollPane();
        sp.setContent(race);


        paneMain.setCenter(sp);


        //createTableViews();
        setDataToTables();
        tableMoneyBetHandler();
        if (personBids != null && !personBids.isEmpty()) {
            for (Bid personBid : personBids) {
                setAcknowledgeCarBid(personBid, true);
            }
        }
    }

    private void createRaceTableView(long raceId) {
        TableView<CarProperties> raceTableView = new TableView<>();
        ObservableList<CarProperties> raceData = FXCollections.observableArrayList();
        TableColumn<CarProperties, String> moneyBetColumn = new TableColumn<>("Money bets");
        Label raceLabel = new Label("Race " + raceId);
        VBox race = new VBox(raceLabel, raceTableView);

        TableColumn<CarProperties, String> carTypeColumn = new TableColumn<>("Car Type");
        carTypeColumn.setMinWidth(150);
        carTypeColumn.setCellValueFactory(new PropertyValueFactory<>("carType"));
        moneyBetColumn.setMinWidth(60);
        moneyBetColumn.setCellValueFactory(new PropertyValueFactory<>("moneyBets"));
        moneyBetColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        TableColumn<CarProperties, String> moneyAfterTax = new TableColumn<>("After Tax");
        moneyAfterTax.setMinWidth(60);
        moneyAfterTax.setCellValueFactory(new PropertyValueFactory<>("afterTax"));
        raceTableView.setItems(raceData);
        raceTableView.getColumns().addAll(carTypeColumn, moneyBetColumn, moneyAfterTax);
        raceTableView.setFixedCellSize(35);
        raceTableView.prefHeightProperty().bind(raceTableView.fixedCellSizeProperty().multiply(Bindings.size(raceTableView.getItems()).add(1)));
        raceTableView.minHeightProperty().bind(raceTableView.prefHeightProperty());
        raceTableView.maxHeightProperty().bind(raceTableView.prefHeightProperty());

        raceLabel.setFont(new Font(15));
        raceLabel.setPadding(new Insets(10,0,5,5));

        race.setAlignment(Pos.CENTER_LEFT);
        raceTableView.setEditable(true);

        tableViews.add(raceTableView);
        raceDatas.add(raceData);
        lblRaces.add(raceLabel);
        moneyBetColumns.add(moneyBetColumn);
        raceVboxes.add(race);
    }

    private void addRaceAction() {
        getGlueObject().getAddRaceController().getCars();
    }

    public ObservableList<CarProperties> getRaceData(int raceIndex) {
        return raceDatas.get(raceIndex);
    }


    public void setAfterTax(int raceIndex, String moneyAfterTax, int rowInTable) {
        raceDatas.get(raceIndex).get(rowInTable).setAfterTax(moneyAfterTax);
    }

    public void setMoneyBet(int raceIndex, String moneyBet, int rowInTable) {
        raceDatas.get(raceIndex).get(rowInTable).setMoneyBet(moneyBet);
    }

    public void setCarTypeData(int raceIndex, String carType, long carId) {
        raceDatas.get(raceIndex).add(new CarProperties(carType, carId));
    }

    public void setBalance(double balance) {
        this.lblBalance.setText("Balance: " + Double.toString(balance));
        setUserMoney(balance);
    }

    public TableColumn<CarProperties, String> getMoneyBetColumn(int raceIndex) {
        return moneyBetColumns.get(raceIndex);
    }

    public BorderPane getViewMainPane() {
        return paneMain;
    }

    public TableView<CarProperties> getTableView(int raceIndex) {
        return tableViews.get(raceIndex);
    }

    public void setUserMoney(double userMoney) {
        this.userMoney = userMoney;
    }

    public void setRaces(List<Race> races) {
        this.races = races;
        tableMoneyBetHandler();
    }

    @Override
    public void setPerson(Person person) {
        this.person = person;
        setBalance(person.getBalance());
    }

    public void updateDataToTables(String bidAmount, String bidAmountAfterTax, double balanceAfterBet, int raceIndex, int rowInTable) {
        setBalance(balanceAfterBet);
        if (raceIndex < getPersistancyRacesCount()) {
            setMoneyBet(raceIndex, bidAmount, rowInTable);
            setAfterTax(raceIndex, bidAmountAfterTax, rowInTable);
        }
    }

    public void setDataToTables() {
        setBalance(person.getBalance());

        for (int raceIndex = 0; raceIndex < getPersistancyRacesCount(); raceIndex++) {
            Race race = races.get(raceIndex);
            Map<Long, Integer> carToRowId = new HashMap<>();
            for(int carIndex = 0; carIndex < MainView.CARS_NUM; carIndex++) {
                Car carData = race.getCars().get(carIndex);
                setCarTypeData(raceIndex, carData.toString(), carData.getId());
                carToRowId.put(carData.getId(), carIndex);
            }
            raceIdToRaceIndexLookup.put(race.getId(), raceIndex);
            raceIdToCarIdToRowIdLookup.put(race.getId(), carToRowId);
        }
    }

    public void tableMoneyBetHandler() {
        for (int raceIndex = 0; raceIndex < getPersistancyRacesCount(); raceIndex++) {
            getMoneyBetColumn(raceIndex).setOnEditCommit(new BetChangeCellHandler(raceIndex));
            // Disable race table view if this race was previously started
            if (races.get(raceIndex).isStarted()) {
                getMoneyBetColumn(raceIndex).getTableView().setDisable(true);
                lblRaces.get(raceIndex).setText("Race " + (raceIndex + 1) + " FINISHED");
            }
        }
    }

    public void disableRaceBetsByRaceIndex(int raceIndex, long raceId) {
        getMoneyBetColumn(raceIndex).getTableView().setDisable(true);
        lblRaces.get(raceIndex).setText("Race " + raceId + " FINISHED");
    }

    public void disableRaceBetsByRaceId(long raceId){
        Integer raceIndex = raceIdToRaceIndexLookup.get(raceId);
        disableRaceBetsByRaceIndex(raceIndex, raceId);
    }

    public void setAcknowledgeCarBid(Bid bid, boolean acknowledged) {
        Integer raceIndex = raceIdToRaceIndexLookup.get(bid.getRaceId());
        Map<Long, Integer> carToRowId = raceIdToCarIdToRowIdLookup.get(bid.getRaceId());
        Integer rowInTable = carToRowId.get(bid.getCarId());

        String bidAmount = "";
        String bidAmountAfterTax = "";
        if (acknowledged) {
            bidAmount = String.valueOf(bid.getAmount());
            bidAmountAfterTax = String.valueOf(bid.getAmountAfterTax());
        }
        updateDataToTables(bidAmount, bidAmountAfterTax, person.getBalance(), raceIndex, rowInTable);
        CarProperties carProperties = getTableView(raceIndex).getItems().get(rowInTable);
        carProperties.setBetAcknowleged(acknowledged);
        getTableView(raceIndex).getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
    }

    public GlueObject getGlueObject() {
        return glueObject;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {
        getGlueObject().setCurrentView(this);
        Platform.runLater(() -> {
            stage.setTitle("Car Race Bets for person #" + getGlueObject().getPerson().getId());
            stage.show();
        });
    }

    @Override
    public void hide() {
        Platform.runLater(() -> stage.close());
    }

    public int getPersistancyRacesCount() {
        return persistancyRacesCount;
    }

    public void setPersistancyRacesCount(int persistancyRacesCount) {
        this.persistancyRacesCount = persistancyRacesCount;
    }

    public class CarProperties {
        private final SimpleStringProperty carType;
        private final SimpleStringProperty moneyBets;
        private final SimpleStringProperty afterTax;
        private boolean isBetAcknowleged = false;
        private long carId;

        public CarProperties(String carType, long carId) {
            this.carType = new SimpleStringProperty(carType);
            this.moneyBets = new SimpleStringProperty();
            this.afterTax = new SimpleStringProperty();
            this.carId = carId;
        }

        public long getCarId() {
            return carId;
        }
        public void setAfterTax(String afterTax) {
            this.afterTax.set(afterTax);
        }
        public String getAfterTax() {
            return afterTax.get();
        }
        public void setMoneyBet(String moneyBets) {
            this.moneyBets.set(moneyBets);
        }
        public String getMoneyBets() {
            return moneyBets.get();
        }
        public String getCarType() {
            return carType.get();
        }

        public boolean isBetAcknowleged() {
            return isBetAcknowleged;
        }

        public void setBetAcknowleged(boolean betAcknowleged) {
            isBetAcknowleged = betAcknowleged;
        }
    }

    private class BetChangeCellHandler implements EventHandler<CellEditEvent<CarProperties, String>> {
        private int raceIndex;


        public BetChangeCellHandler(int raceIndex) {
            this.raceIndex = raceIndex;
        }
        /**
         * Invoked when a specific event of the type for which this handler is
         * registered happens.
         *
         * @param event the event which occurred
         */
        @Override
        public void handle(CellEditEvent<CarProperties, String> event) {
            int rowInTable = event.getTablePosition().getRow();
            CarProperties carProperties = event.getTableView().getItems().get(rowInTable);

            if (carProperties.isBetAcknowleged()) {
                AlertMessage.alertMsg(getStage(), "Can't change existing bet.", AlertType.ERROR);
                carProperties.setMoneyBet(event.getOldValue());
            } else {
                Optional<Double> aDouble = ParseUtils.tryParseDouble(event.getNewValue());
                if (!aDouble.isPresent()) {
                    AlertMessage.alertMsg(getStage(), "Only numbers are allowed in this field.", AlertType.ERROR);
                    carProperties.setMoneyBet(event.getOldValue());
                } else if (aDouble.get() < 1) {
                    AlertMessage.alertMsg(getStage(), "Only numbers that greater than 0 are allowed.", AlertType.ERROR);
                } else {

                    carProperties.setMoneyBet(event.getNewValue());
                    sendBet(
                            races.get(raceIndex).getId(),
                            person.getId(),
                            carProperties.getCarId(),
                            aDouble.get(),
                            raceIndex,
                            rowInTable
                    );
                }
            }
            event.getTableView().getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);
        }

        private void sendBet(long raceId, long personId, long carId, double bet, int raceIndex, int rowInTable) {
            Map<Long, Integer> carToRowId = raceIdToCarIdToRowIdLookup.computeIfAbsent(raceId, id -> new HashMap<>());
            carToRowId.put(carId, rowInTable);

            getGlueObject().getMainController().makeBet(new Bid(raceId, personId, carId, bet));
        }
    }
}
