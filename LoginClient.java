import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginClient extends Application {
  private static final String SERVER_IP = "127.0.0.1";
  private static final int SERVER_PORT = 8080;

  private BufferedReader in;
  private PrintWriter out;

  private TextField usernameField;
  private PasswordField passwordField;
  private Label messageLabel;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Login");

    Label usernameLabel = new Label("Username");
    usernameField = new TextField();
    Label passwordLabel = new Label("Password");
    passwordField = new PasswordField();
    Button loginButton = new Button("Login");
    messageLabel = new Label();

    loginButton.setOnAction(e -> {
      String username = usernameField.getText();
      String password = passwordField.getText();

      //どちらかが入力されていないとき
      if (username.isEmpty() || password.isEmpty()) {
        messageLabel.setText("Please enter username and password");
        return;
      }

      //入力情報をサーバーに送信
      out.println("LOGIN_c " + username + " " + password);
    });

    VBox layout = new VBox(10);
    layout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, messageLabel);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(10));

    Scene scene = new Scene(layout, 300, 200);
    primaryStage.setScene(scene);
    primaryStage.show();

    connectToServer();

    new Thread(() -> {
      try {
        String message;
        while ((message = in.readLine()) != null) {

          //成功したとき
          if (message.startsWith("SUCCESS_s")) {
            Platform.runLater(() -> {
              messageLabel.setText("Login successful");
            });
          
          //失敗したとき
          } else if (message.startsWith("FAILURE_s")) {
            Platform.runLater(() -> {
              messageLabel.setText("Login failed");
            });
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  private void connectToServer() {
    try {
      Socket socket = new Socket(SERVER_IP, SERVER_PORT);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}