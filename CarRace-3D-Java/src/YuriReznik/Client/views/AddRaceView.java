package YuriReznik.Client.views;

import java.util.ArrayList;
import java.util.List;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Car;
import YuriReznik.Message.Person;
import YuriReznik.Message.Race;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class AddRaceView implements View {

    private GlueObject glueObject;

    private Stage stage;
    private Scene scene;
    private BorderPane paneMain;
    private Button btnAddRace;

    private TableView<CarInfoData> tblvCarInfo;
    private ObservableList<CarInfoData> carInfoData;



    public AddRaceView(GlueObject glueObject) {
        this.glueObject = glueObject;
        stage = new Stage();
        paneMain = new BorderPane();
        createPane();
        scene = new Scene(paneMain, 250, 250);
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Add Race");
        stage.setY(Screen.getPrimary().getVisualBounds().getHeight()/2.3);
        stage.setX(5);
    }

    private void createPane() {
        btnAddRace = new Button("Create a Race From Selected");
        createButtonEvent();
        createTableView();
        paneMain.setCenter(tblvCarInfo);
        paneMain.setBottom(btnAddRace);
    }

    private void createButtonEvent() {
        btnAddRace.setOnAction(event -> {
            ObservableList<CarInfoData> selectedItems = tblvCarInfo.getSelectionModel().getSelectedItems();
            if (selectedItems == null || selectedItems.isEmpty()) {
                AlertMessage.alertMsg(getStage(), "No cars selected", AlertType.ERROR);
                return;
            }

            if (selectedItems.size() < MainView.CARS_NUM) {
                AlertMessage.alertMsg(getStage(), "No enough cars selected. Need "+MainView.CARS_NUM+" cars", AlertType.ERROR);
                return;
            }
            if (selectedItems.size() > MainView.CARS_NUM) {
                AlertMessage.alertMsg(getStage(), "Too much cars selected. Need "+MainView.CARS_NUM+" cars", AlertType.ERROR);
                return;
            }
            List<Car> carsForRace = new ArrayList<>();
            selectedItems.forEach(carInfo -> carsForRace.add(carInfo.getCarObject()));
            getGlueObject().getAddRaceController().addRace(new Race().setCars(carsForRace));
        });
    }

    /**
     * Refresh data of tableView
     */
    public void refreshData(List<Car> cars) {
        List<CarInfoData> result = new ArrayList<>();
        if (cars == null) return;

        for (Car car : cars) {
            result.add(new CarInfoData(car.getId(), car.toString(), car));
        }

        carInfoData.clear();
        carInfoData.addAll(result);
    }

    private void createTableView() {
        tblvCarInfo = new TableView<>();
        carInfoData = FXCollections.observableArrayList();
        TableColumn<CarInfoData, String> carIdColumn = new TableColumn<>("Car Id");
        TableColumn<CarInfoData, String> carDescriptionColumn = new TableColumn<>("Car Description");

        carIdColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));
        carDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("carDescription"));

        carIdColumn.setMinWidth(60);
        carDescriptionColumn.setMinWidth(150);

        tblvCarInfo.setItems(carInfoData);
        tblvCarInfo.getColumns().addAll(carIdColumn, carDescriptionColumn);
        tblvCarInfo.setEditable(false);
        tblvCarInfo.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    @Override
    public void show() {
        stage.show();
    }

    @Override
    public void hide() {
        stage.close();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public GlueObject getGlueObject() {
        return glueObject;
    }

    @Override
    public void setPerson(Person person) {
        throw new UnsupportedOperationException("AddRaceView not supports setPerson()");
    }

    public class CarInfoData {
        private final SimpleLongProperty carId;
        private final SimpleStringProperty carDescription;
        private Car carObject;

        public CarInfoData(Long carId, String carDescription, Car carObject) {
            this.carId = new SimpleLongProperty(carId);
            this.carDescription = new SimpleStringProperty(carDescription);
            this.carObject = carObject;
        }

        public long getCarId() {
            return carId.get();
        }

        public String getCarDescription() {
            return carDescription.get();
        }

        public Car getCarObject() {
            return carObject;
        }

        public void setCarObject(Car carObject) {
            this.carObject = carObject;
        }
    }
}
