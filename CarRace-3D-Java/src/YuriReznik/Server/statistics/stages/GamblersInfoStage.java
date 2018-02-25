package YuriReznik.Server.statistics.stages;

import java.util.List;
import java.util.Optional;

import YuriReznik.Message.Bid;
import YuriReznik.Message.Car;
import YuriReznik.Message.Person;
import YuriReznik.Message.utils.ParseUtils;
import YuriReznik.Message.utils.StringUtils;
import YuriReznik.Server.persistancy.BidDao;
import YuriReznik.Server.persistancy.CarDao;
import YuriReznik.Server.persistancy.PersonDao;
import YuriReznik.Server.statistics.datamodel.GamblerProperties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class GamblersInfoStage {

    private Stage stage;
    private Scene scene;
    private BorderPane paneMain;

    private ComboBox<Person> cbPerson;
    private ComboBox<String> cbRaceId;
    private Button btnRefresh;
    private TableView<GamblerProperties> tblvGamblerInfo;
    private ObservableList<GamblerProperties> gamblerInfoData;
    private final Person DEFAULT_GAMBLER = new Person().setName("").setPassword("");

    private Person selectedGambler = DEFAULT_GAMBLER;
    private String selectedRace = "";

    private BidDao bidDao = BidDao.INSTANCE;
    private PersonDao personDao = PersonDao.INSTANCE;
    private CarDao carDao = CarDao.INSTANCE;

    public GamblersInfoStage() {
        stage = new Stage();
        paneMain = new BorderPane();
        createPane();
        scene = new Scene(paneMain, 600, 250);
        stage.initStyle(StageStyle.UTILITY);
        stage.setOnCloseRequest(Event::consume);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Gamblers Information");
        stage.setY(Screen.getPrimary().getVisualBounds().getHeight()/1.45);
        stage.setX(5);
        stage.show();
    }

    private void createPane() {
        cbPerson = new ComboBox<>();
        cbRaceId = new ComboBox<>();
        btnRefresh = new Button("Refresh Data");
        btnRefresh.setOnAction(event -> refreshData());

        Label lblGamblerName = new Label("Gambler Name:");
        lblGamblerName.setAlignment(Pos.CENTER_LEFT);
        lblGamblerName.setPadding(new Insets(10, 10, 20, 10));
        Label lblBalance = new Label("Balance:");
        lblBalance.setAlignment(Pos.CENTER_LEFT);
        lblBalance.setPadding(new Insets(0, 0, 0, 10));
        lblBalance.setFont(new Font(17));
        Label lblRaceId = new Label("Race Id:");
        lblRaceId.setAlignment(Pos.CENTER_LEFT);
        lblRaceId.setPadding(new Insets(10, 10, 20, 10));

        HBox topHBox = new HBox(lblGamblerName, cbPerson, lblRaceId, cbRaceId);
        HBox bottomHBox = new HBox(btnRefresh, lblBalance);

        fillComboBoxes(lblBalance);
        createTableView();

        paneMain.setTop(topHBox);
        paneMain.setCenter(tblvGamblerInfo);
        paneMain.setBottom(bottomHBox);
    }

    /**
     * Refresh view data
     */
    private void refreshData() {
        cbPerson.getItems().clear();
        cbPerson.getItems().add(DEFAULT_GAMBLER);
        cbPerson.getItems().addAll(personDao.getAllPersons().stream().filter(p -> !personDao.isSystemUser(p)).toArray(Person[]::new));

        cbRaceId.getItems().clear();
        cbRaceId.getItems().add("");

        cbPerson.getSelectionModel().select(selectedGambler);
        cbRaceId.getSelectionModel().select(selectedRace);
    }

    private void fillComboBoxes(Label lblBalance) {
        cbPerson.setCellFactory((value) -> new ListCell<Person>(){
            @Override
            protected void updateItem(Person item, boolean empty) {
                super.updateItem(item, empty);

                if(item != null){
                    setText(item.getName());
                }else{
                    setText("");
                }
            }
        });
        cbPerson.getItems().add(DEFAULT_GAMBLER);
        cbPerson.getItems().addAll(personDao.getAllPersons().stream().filter(p -> !personDao.isSystemUser(p)).toArray(Person[]::new));
        cbRaceId.getItems().add("");

        cbPerson.setOnAction(event -> {
            Person selectedItem = cbPerson.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !(StringUtils.isEmpty(selectedItem.getName()) && StringUtils.isEmpty(selectedItem.getPassword()))) {
                cbRaceId.getItems().clear();
                cbRaceId.getItems()
                        .addAll(
                                bidDao.getBidsByPersonId(selectedItem.getId())
                                        .stream()
                                        .map(b -> b.getRaceId())
                                        .sorted(Long::compare)
                                        .map(String::valueOf)
                                        .distinct()
                                        .toArray(String[]::new)
                        );
                lblBalance.setText("Balance: " + selectedItem.getBalance());
                gamblerInfoData.clear();
                selectedGambler = selectedItem;
            }
        });

        cbRaceId.setOnAction(event -> {
            String selectedItem = cbRaceId.getSelectionModel().getSelectedItem();
            Person selectedPerson = cbPerson.getSelectionModel().getSelectedItem();
            Optional<Long> optionalRaceId = ParseUtils.tryParseLong(selectedItem);
            if (!optionalRaceId.isPresent()) return;
            Long raceId = optionalRaceId.get();

            gamblerInfoData.clear();
            List<Bid> personBids = bidDao.getBidsByPersonIdAndRaceId(selectedPerson.getId(), raceId);
            personBids.forEach(bid -> {
                Car car = carDao.getCarById(bid.getCarId());
                if (car != null) {
                    gamblerInfoData.add(new GamblerProperties(
                            car.toString(),
                            String.valueOf(bid.getAmount()),
                            String.valueOf(bid.getProfit()))
                    );
                }
            });
            selectedRace = selectedItem;
        });
    }

    private void createTableView() {
        tblvGamblerInfo = new TableView<>();
        gamblerInfoData = FXCollections.observableArrayList();
        TableColumn<GamblerProperties, String> carColumn = new TableColumn<>("Car");
        TableColumn<GamblerProperties, String> betColumn = new TableColumn<>("Bet");
        TableColumn<GamblerProperties, String> profitColumn = new TableColumn<>("Profit");

        carColumn.setCellValueFactory(new PropertyValueFactory<>("car"));
        betColumn.setCellValueFactory(new PropertyValueFactory<>("bet"));
        profitColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));

        carColumn.setMinWidth(150);
        betColumn.setMinWidth(150);
        profitColumn.setMinWidth(150);

        tblvGamblerInfo.setItems(gamblerInfoData);
        tblvGamblerInfo.getColumns().addAll(carColumn, betColumn, profitColumn);
        tblvGamblerInfo.setEditable(false);
    }
}
