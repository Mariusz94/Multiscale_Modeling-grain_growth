package pl.mariuszlyszczarz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainScreen.fxml"));
        primaryStage.setTitle("Hello World");
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        primaryStage.setScene(new Scene(root, 1300, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
