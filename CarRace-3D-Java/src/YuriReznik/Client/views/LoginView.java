package YuriReznik.Client.views;

import java.util.Optional;

import YuriReznik.Client.AlertMessage;
import YuriReznik.Client.eventdriven.GlueObject;
import YuriReznik.Message.Person;
import YuriReznik.Message.utils.ParseUtils;
import YuriReznik.Message.utils.StringUtils;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Login View with add new user and login itself
 */
public class LoginView implements View {

    private GlueObject glueObject;

    private Stage stage;
    private Scene sceneLogIn, sceneNewAccount;

    private BorderPane logInPane, newAccountPane;

    private Button btnBackToLogIn;

    public LoginView(GlueObject glueObject, double x, double y) {
        this.glueObject = glueObject;
        createLoginPane();
        createNewAccountPane();

        stage = new Stage();
        stage.setOnCloseRequest(Event::consume); // disable closing
        stage.initStyle(StageStyle.UTILITY);
        sceneLogIn = new Scene(logInPane, 300, 200);
        sceneNewAccount = new Scene(newAccountPane, 300, 200);
        stage.setScene(sceneLogIn);
        stage.setTitle("Log In");
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setX(x);
        stage.setY(y);
    }

    private void createLoginPane() {
        BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color: deepskyblue");

        GridPane userDetails = new GridPane();

        Label lblName = new Label("Name: ");
        Label lblPassword = new Label("Password: ");
        TextField txtName = new TextField();
        TextField txtPassword = new TextField();
        userDetails.add(lblName, 0, 0);
        userDetails.add(txtName, 1, 0);
        userDetails.add(lblPassword, 0, 1);
        userDetails.add(txtPassword, 1, 1);
        userDetails.setAlignment(Pos.CENTER);

        HBox bottom = new HBox();
        bottom.setPrefWidth(240);
        bottom.setAlignment(Pos.CENTER);
        Button btnOk = new Button("It's me");
        btnOk.setPadding(new Insets(10));
        btnOk.setOnAction(event -> triggerLogin(txtName.getText(), txtPassword.getText()));
        bottom.getChildren().addAll(btnOk);

        HBox topRight = new HBox();
        topRight.setPrefWidth(240);
        topRight.setAlignment(Pos.CENTER_RIGHT);
        Button btnCreateAccount = new Button("New account");
        btnCreateAccount.setPadding(new Insets(3));
        topRight.getChildren().addAll(btnCreateAccount);
        btnCreateAccount.setOnAction(event -> stage.setScene(sceneNewAccount));


        HBox topLeft = new HBox();
        topLeft.setAlignment(Pos.CENTER_RIGHT);
        Label lblLogIn = new Label("Log In");
        lblLogIn.setFont(new Font(20));
        topLeft.getChildren().addAll(lblLogIn);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(topLeft, topRight);

        pane.setTop(top);
        pane.setCenter(userDetails);
        pane.setBottom(bottom);

        logInPane = pane;
    }

    private void createNewAccountPane() {
        BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color: deepskyblue");

        GridPane userDetails_newAccount = new GridPane();
        Label lblNewAccount_newAccount = new Label("Create an account");
        lblNewAccount_newAccount.setFont(new Font(20));

        Label lblName_newAccount = new Label("Name: ");
        Label lblPassword_newAccount = new Label("Password: ");
        Label lblRePassword_newAccount = new Label("Retype Password: ");
        Label lblMoney_newAccount = new Label("Money Transaction: ");
        TextField txtName_newAccount = new TextField();
        TextField txtPassword_newAccount = new TextField();
        TextField txtRePassword_newAccount = new TextField();
        TextField txtMoney_newAccount = new TextField();

        userDetails_newAccount.add(lblName_newAccount, 0, 0);
        userDetails_newAccount.add(txtName_newAccount, 1, 0);
        userDetails_newAccount.add(lblPassword_newAccount, 0, 1);
        userDetails_newAccount.add(txtPassword_newAccount, 1, 1);
        userDetails_newAccount.add(lblRePassword_newAccount, 0, 2);
        userDetails_newAccount.add(txtRePassword_newAccount, 1, 2);
        userDetails_newAccount.add(lblMoney_newAccount, 0, 3);
        userDetails_newAccount.add(txtMoney_newAccount, 1, 3);
        userDetails_newAccount.setAlignment(Pos.CENTER);

        Button btnOk_newAccount = new Button("Ok");
        btnBackToLogIn = new Button("Back");
        btnBackToLogIn.setOnAction((event) -> stage.setScene(sceneLogIn));
        btnOk_newAccount.setPadding(new Insets(10));
        btnOk_newAccount.setOnAction(event ->
                triggerCreateNewAccount(txtName_newAccount.getText(),
                        txtPassword_newAccount.getText(),
                        txtRePassword_newAccount.getText(),
                        txtMoney_newAccount.getText()
                )
        );

        HBox top_newAccount = new HBox();
        top_newAccount.getChildren().addAll(btnBackToLogIn, lblNewAccount_newAccount);

        HBox bottom_newAccount = new HBox();
        bottom_newAccount.getChildren().add(btnOk_newAccount);
        bottom_newAccount.setPrefWidth(160);
        bottom_newAccount.setAlignment(Pos.CENTER);
        btnOk_newAccount.setMinWidth(bottom_newAccount.getPrefWidth());

        pane.setTop(top_newAccount);
        pane.setCenter(userDetails_newAccount);
        pane.setBottom(bottom_newAccount);

        newAccountPane = pane;
    }


    private void triggerCreateNewAccount(String username, String password, String rePassword, String money) {
        if (StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(rePassword)
                || StringUtils.isEmpty(money)) {
            AlertMessage.alertMsg(getStage(), "One of a fields are empty.", AlertType.ERROR);
            return;
        }

        if (!password.equals(rePassword)) {
            AlertMessage.alertMsg(getStage(), "Passwords are not matched.", AlertType.ERROR);
            return;
        }

        Optional<Double> aDouble = ParseUtils.tryParseDouble(money);

        if (!aDouble.isPresent()) {
            AlertMessage.alertMsg(getStage(), "Only numbers allowed in money field.", AlertType.ERROR);
            return;
        }
        getGlueObject().getLoginController().createNewUser(username, password, aDouble.get());
    }

    private void triggerLogin(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            AlertMessage.alertMsg(getStage(), "One of a fields are empty.", AlertType.ERROR);
            return;
        }

        getGlueObject().getLoginController().makeLogin(username, password);
    }

    public Button getBtnBackToLogIn() {
        return btnBackToLogIn;
    }

    @Override
    public void show() {
        getGlueObject().setCurrentView(this);
        Platform.runLater(() -> stage.show());
    }

    @Override
    public void hide() {
        Platform.runLater(() -> stage.hide());
    }

    public GlueObject getGlueObject() {
        return glueObject;
    }

    @Override
    public void setPerson(Person person) {
        throw new UnsupportedOperationException("LoginView not supports setPerson()");
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
