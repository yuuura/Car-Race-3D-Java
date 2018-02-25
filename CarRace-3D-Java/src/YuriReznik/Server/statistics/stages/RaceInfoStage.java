package YuriReznik.Server.statistics.stages;


import java.util.List;
import java.util.Optional;

import YuriReznik.Message.Race;
import YuriReznik.Message.utils.ParseUtils;
import YuriReznik.Message.utils.StringUtils;
import YuriReznik.Server.persistancy.BidDao;
import YuriReznik.Server.persistancy.RaceDao;
import YuriReznik.Server.statistics.datamodel.RaceProperties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Race information stage view
 */
public class RaceInfoStage {

    private Stage stage;
    private Scene scene;
    private BorderPane paneMain;
    private Button btnRefresh;

    private ComboBox<String> cbRaceId;
    private TableView<RaceProperties> tblvRaceInfo;
    private ObservableList<RaceProperties> raceInfoData;

    private String selectedRace = "";

    private RaceDao raceDao = RaceDao.INSTANCE;
    private BidDao bidDao = BidDao.INSTANCE;


    public RaceInfoStage() {
        stage = new Stage();
        paneMain = new BorderPane();
        createPane();
        scene = new Scene(paneMain, 750, 250);
        stage.initStyle(StageStyle.UTILITY);
        stage.setOnCloseRequest(Event::consume);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Race Information");
        stage.setY(Screen.getPrimary().getVisualBounds().getHeight()/2.3);
        stage.setX(5);
        stage.show();
    }

    private void createPane() {
        cbRaceId = new ComboBox<>();
        btnRefresh = new Button("Refresh Data");
        btnRefresh.setOnAction(event -> refreshData());


        Label lblRaceId = new Label("Race Id:");
        lblRaceId.setAlignment(Pos.CENTER_LEFT);
        lblRaceId.setPadding(new Insets(10, 10, 20, 10));
        Label lblCreateDate = new Label("Create Date:");
        lblCreateDate.setAlignment(Pos.CENTER_LEFT);
        lblCreateDate.setPadding(new Insets(10, 10, 20, 10));

        Label lblStartDate = new Label("Start Date:");
        lblStartDate.setAlignment(Pos.CENTER_LEFT);
        lblStartDate.setPadding(new Insets(10, 10, 20, 10));

        Label lblTotalMoney = new Label("Total Money:");
        lblTotalMoney.setPadding(new Insets(0, 0, 0, 10));
        lblTotalMoney.setFont(new Font(17));

        HBox topHBox = new HBox(lblRaceId, cbRaceId, lblCreateDate, lblStartDate);
        HBox bottomHBox = new HBox(btnRefresh, lblTotalMoney);

        fillComboBox(lblCreateDate, lblStartDate, lblTotalMoney);
        createTableView();

        paneMain.setTop(topHBox);
        paneMain.setCenter(tblvRaceInfo);
        paneMain.setBottom(bottomHBox);
    }

    private void refreshData() {
        cbRaceId.getItems().clear();
        cbRaceId.getItems().add("");
        cbRaceId.getItems().addAll(raceDao.getAllRaces().stream().map(r -> String.valueOf(r.getId())).toArray(String[]::new));
        cbRaceId.getSelectionModel().select(selectedRace);
    }

    private void fillComboBox(Label lblCreateDate, Label lblStartDate, Label lblTotalMoney) {
        cbRaceId.getItems().add("");
        cbRaceId.getItems().addAll(raceDao.getAllRaces().stream().map(r -> String.valueOf(r.getId())).toArray(String[]::new));

        cbRaceId.setOnAction(event -> {
            String selectedItem = cbRaceId.getSelectionModel().getSelectedItem();
            if (!StringUtils.isEmpty(selectedItem)) {
                Optional<Long> optionalRaceId = ParseUtils.tryParseLong(selectedItem);
                if (!optionalRaceId.isPresent()) return;
                Long raceId = optionalRaceId.get();
                Race race = raceDao.getRaceById(raceId);
                double totalMoney = bidDao.getTotalMoneyByRaceId(raceId);

                lblCreateDate.setText("Create Date: " + race.getCreateDate().toString());
                lblStartDate.setText("Start Date: " + (race.isStarted() ? race.getStartDate().toString() : "Not started Yet. Not enough bets."));
                lblTotalMoney.setText("Total Money: " + totalMoney);

                List<RaceProperties> raceProperties = raceDao.getRacesAndGamblers(raceId);
                raceInfoData.clear();
                raceInfoData.addAll(raceProperties);
                selectedRace = selectedItem;
            }
        });
    }

    private void createTableView() {
        tblvRaceInfo = new TableView<>();
        raceInfoData = FXCollections.observableArrayList();
        TableColumn<RaceProperties, String> carColumn = new TableColumn<>("Cars");
        TableColumn<RaceProperties, String> gamblersColumn = new TableColumn<>("Gamblers");
        TableColumn<RaceProperties, String> winnerColumn = new TableColumn<>("Winner");

        carColumn.setCellValueFactory(new PropertyValueFactory<>("car"));
        gamblersColumn.setCellValueFactory(new PropertyValueFactory<>("gamblers"));
        winnerColumn.setCellValueFactory(new PropertyValueFactory<>("winnerCar"));

        carColumn.setMinWidth(150);
        gamblersColumn.setMinWidth(300);
        winnerColumn.setMinWidth(60);

        tblvRaceInfo.setItems(raceInfoData);
        tblvRaceInfo.getColumns().addAll(carColumn, gamblersColumn, winnerColumn);
        tblvRaceInfo.setEditable(false);
    }
}
