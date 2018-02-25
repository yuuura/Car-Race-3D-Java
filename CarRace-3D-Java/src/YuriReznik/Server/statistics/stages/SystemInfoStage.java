package YuriReznik.Server.statistics.stages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import YuriReznik.Message.Person;
import YuriReznik.Message.Race;
import YuriReznik.Server.persistancy.BidDao;
import YuriReznik.Server.persistancy.PersonDao;
import YuriReznik.Server.persistancy.RaceDao;
import YuriReznik.Server.statistics.datamodel.GamblerRevenueProperties;
import YuriReznik.Server.statistics.datamodel.SystemRevenueProperties;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class SystemInfoStage {

    private final String GAMBLERS_REVENUE = "Gamblers Revenue";
    private final String SYSTEM_REVENUE = "System Revenue";

    private Stage stage;
    private Scene scene;
    private BorderPane paneMain;

    private ComboBox<String> cbQueryType;
    private Button btnRefresh;

    private TableView<SystemRevenueProperties> tblvSystemRevenue;
    private ObservableList<SystemRevenueProperties> systemRevenuePropertiesData;

    private TableView<GamblerRevenueProperties> tblvGamblerRevenue;
    private ObservableList<GamblerRevenueProperties> gamblerRevenuePropertiesData;

    private int activeView = 0;

    private RaceDao raceDao = RaceDao.INSTANCE;
    private BidDao bidDao = BidDao.INSTANCE;
    private PersonDao personDao = PersonDao.INSTANCE;

    public SystemInfoStage() {
        stage = new Stage();
        paneMain = new BorderPane();
        createPane();
        scene = new Scene(paneMain, 1000, 250);
        stage.initStyle(StageStyle.UTILITY);
        stage.setOnCloseRequest(Event::consume);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setTitle("System Information");
        stage.setY(Screen.getPrimary().getVisualBounds().getHeight()/1.45);
        stage.setX(Screen.getPrimary().getVisualBounds().getWidth() / 8);
        stage.show();
    }

    private void createPane() {
        cbQueryType = new ComboBox<>();
        btnRefresh = new Button("Refresh Data");

        Label lblRaceId = new Label("Query Type:");
        lblRaceId.setAlignment(Pos.CENTER_LEFT);
        lblRaceId.setPadding(new Insets(10, 10, 20, 10));

        HBox hBox = new HBox(lblRaceId, cbQueryType);

        fillComboBox();
        addButtonEvent();

        createSystemRevenueTableView();
        createGamblerRevenueTableView();

        paneMain.setTop(hBox);
        paneMain.setBottom(btnRefresh);
    }

    private void addButtonEvent() {
        btnRefresh.setOnAction(event -> {
            refreshGamblersRevenue();
            refreshSystemRevenue();
            switch (activeView) {
                case 1:
                    showSystemRevenue();
                    break;
                case 2:
                    showGamblersRevenue();
                default:
                    break;
            }
        });
    }

    private void fillComboBox() {
        cbQueryType.getItems().addAll("", SYSTEM_REVENUE, GAMBLERS_REVENUE);
        cbQueryType.getSelectionModel().selectFirst();

        cbQueryType.setOnAction(event -> {
            String selectedItem = cbQueryType.getSelectionModel().getSelectedItem();
            switch (selectedItem) {
                case SYSTEM_REVENUE:
                    activeView = 1;
                    showSystemRevenue();
                    break;
                case GAMBLERS_REVENUE:
                    activeView = 2;
                    showGamblersRevenue();
                    break;
                default:
                    break;
            }
        });
    }

    private void refreshSystemRevenue() {
        List<SystemRevenueProperties> result = new ArrayList<>();
        List<Race> allRaces = raceDao.getAllRaces();
        for (Race race : allRaces) {
            double totalMoneyByRace = bidDao.getTotalMoneyByRaceId(race.getId());
            result.add(new SystemRevenueProperties(
                            String.valueOf(race.getId()),
                            totalMoneyByRace,
                            String.valueOf(race.getCars().get(0)),
                            String.valueOf(race.getCars().get(1)),
                            String.valueOf(race.getCars().get(2)),
                            String.valueOf(race.getCars().get(3)),
                            String.valueOf(race.getCars().get(4))
                    )
            );
        }
        systemRevenuePropertiesData.clear();
        systemRevenuePropertiesData.addAll(result);

        // Sort by revenue
        tblvSystemRevenue.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
    }

    private void refreshGamblersRevenue() {
        List<GamblerRevenueProperties> result = new ArrayList<>();


        List<Person> allPersons = personDao.getAllPersons().stream().filter(p -> !personDao.isSystemUser(p)).collect(Collectors.toList());
        for (Person person : allPersons) {
            Map<Long, Double> totalMoneyForRace = bidDao.getTotalMoneyForRaceIdByPerson(person.getId());
            for (Map.Entry<Long, Double> raceToMoney : totalMoneyForRace.entrySet()) {
                result.add(new GamblerRevenueProperties(
                                String.valueOf(person.getId()),
                                person.getName(),
                                String.valueOf(raceToMoney.getKey()),
                                raceToMoney.getValue()
                        )
                );
            }
        }

        gamblerRevenuePropertiesData.clear();
        gamblerRevenuePropertiesData.addAll(result);

        // Set sorting of columns
        tblvGamblerRevenue.getColumns().get(3).setSortType(TableColumn.SortType.DESCENDING);
    }

    private void showSystemRevenue() {
        refreshSystemRevenue();
        paneMain.setCenter(tblvSystemRevenue);
    }

    private void showGamblersRevenue() {
        refreshGamblersRevenue();
        paneMain.setCenter(tblvGamblerRevenue);
    }

    private void createSystemRevenueTableView() {
        tblvSystemRevenue = new TableView<>();
        systemRevenuePropertiesData = FXCollections.observableArrayList();
        TableColumn<SystemRevenueProperties, String> raceColumn = new TableColumn<>("Race Id");
        TableColumn<SystemRevenueProperties, String> revenueColumn = new TableColumn<>("Revenue");
        TableColumn<SystemRevenueProperties, String> car1Column = new TableColumn<>("Car #1");
        TableColumn<SystemRevenueProperties, String> car2Column = new TableColumn<>("Car #2");
        TableColumn<SystemRevenueProperties, String> car3Column = new TableColumn<>("Car #3");
        TableColumn<SystemRevenueProperties, String> car4Column = new TableColumn<>("Car #4");
        TableColumn<SystemRevenueProperties, String> car5Column = new TableColumn<>("Car #5");

        raceColumn.setCellValueFactory(new PropertyValueFactory<>("race"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        car1Column.setCellValueFactory(new PropertyValueFactory<>("car1"));
        car2Column.setCellValueFactory(new PropertyValueFactory<>("car2"));
        car3Column.setCellValueFactory(new PropertyValueFactory<>("car3"));
        car4Column.setCellValueFactory(new PropertyValueFactory<>("car4"));
        car5Column.setCellValueFactory(new PropertyValueFactory<>("car5"));

        raceColumn.setMinWidth(60);
        revenueColumn.setMinWidth(150);
        car1Column.setMinWidth(150);
        car2Column.setMinWidth(150);
        car3Column.setMinWidth(150);
        car4Column.setMinWidth(150);
        car5Column.setMinWidth(150);

        tblvSystemRevenue.setItems(systemRevenuePropertiesData);
        tblvSystemRevenue.getColumns().addAll(raceColumn, revenueColumn, car1Column, car2Column, car3Column, car4Column, car5Column);
        tblvSystemRevenue.setEditable(false);

        tblvSystemRevenue.getSortOrder().add(revenueColumn);
    }

    private void createGamblerRevenueTableView() {
        tblvGamblerRevenue = new TableView<>();

        gamblerRevenuePropertiesData = FXCollections.observableArrayList();
        TableColumn<GamblerRevenueProperties, String> gamblerIdColumn = new TableColumn<>("Gambler Id");
        TableColumn<GamblerRevenueProperties, String> gamblerNameColumn = new TableColumn<>("Gambler Name");
        TableColumn<GamblerRevenueProperties, String> raceIdColumn = new TableColumn<>("Race Id");
        TableColumn<GamblerRevenueProperties, String> revenueColumn = new TableColumn<>("Revenue");

        gamblerIdColumn.setCellValueFactory(new PropertyValueFactory<>("gamblerId"));
        gamblerNameColumn.setCellValueFactory(new PropertyValueFactory<>("gamblerName"));
        raceIdColumn.setCellValueFactory(new PropertyValueFactory<>("raceId"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        gamblerIdColumn.setMinWidth(60);
        gamblerNameColumn.setMinWidth(60);
        raceIdColumn.setMinWidth(60);
        revenueColumn.setMinWidth(150);

        tblvGamblerRevenue.setItems(gamblerRevenuePropertiesData);
        tblvGamblerRevenue.getColumns().addAll(gamblerIdColumn, gamblerNameColumn, raceIdColumn, revenueColumn);
        tblvGamblerRevenue.setEditable(false);

        tblvGamblerRevenue.getSortOrder().add(revenueColumn);
    }
}
