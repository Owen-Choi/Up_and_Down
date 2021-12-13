import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.sql.*;

public class Main extends Application{
    public void start(Stage primaryStage)throws Exception{
        primaryStage.setTitle("javaFX");
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));//login.fxml에 있는 정보를 불러옴
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String args[]){
        launch(args);
    }
}