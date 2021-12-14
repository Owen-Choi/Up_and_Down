import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;


public class gameController extends Application implements Initializable {
    String user1;
    String user2;
    Database db = new Database();
    @FXML private Text nickname1,nickname2,UP,DOWN,rate1,rate2,number;
    @FXML private TextArea ChattingArea;
    @FXML private TextField ChattingField;
    @FXML private Button Exit;
    @FXML private Button Ready;
    Random rand = new Random();
    int rnum=0;
    int ReadyFlag=0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ready.setOnAction(e->ReadyAction(e));

        //user 1 이랑 user 2에 정보 입력

        /*ResultSet user1_=db.viewInfo(user1);
        ResultSet user2_=db.viewInfo(user2);
        String user1Info=null;
        String user2Info=null;
        try {
            user1Info  = user1_.getString("win") + "/" + user1_.getString("draw") + "/" + user1_.getString("lose");
            user2Info  = user2_.getString("win") + "/" + user2_.getString("draw") + "/" + user2_.getString("lose");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }*/

        try {//ID를 바탕으로 닉네임과 승 무 패 를 띄워줌
            nickname1.setText(loadNN(user1));
            nickname2.setText(loadNN(user2));
            rate1.setText(loadInfo(user1));
            rate2.setText(loadInfo(user2));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void start(Stage primaryStage)throws Exception{
        primaryStage.setTitle("javaFX");
        Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String args[]){
        launch(args);
    }
    public String loadInfo(String id) throws SQLException {
        ResultSet result = db.viewInfo(id);
        String info=null;
        result.next();
        info  = result.getString("win") + "/" + result.getString("draw") + "/" + result.getString("lose");
        return info;
    }
    public String loadNN(String id) throws SQLException {
        ResultSet result = db.viewInfo(id);
        String info=null;
        result.next();
        info  = result.getString("nickname");
        return info;
    }
    public void loadVsInfo(String myId,String OpId, int num) throws SQLException {
        user1=myId;
        user2=OpId;
        rnum=num;
    }
    public void ReadyAction(ActionEvent e){
        if(ReadyFlag==0){
            UP.setFill(Color.RED);
            DOWN.setFill(Color.BLUE);
            ReadyFlag=1;}
        else
        {
            UP.setFill(Color.BLACK);
            DOWN.setFill(Color.BLACK);
            ReadyFlag=0;
        }
        //ready했다는걸 서버에 보내줘야함
    }
    public void close() {
        Stage pop;
        pop = (Stage) Exit.getScene().getWindow();
        pop.close();
    }

}