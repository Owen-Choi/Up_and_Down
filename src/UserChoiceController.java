import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserChoiceController implements Initializable {

    @FXML private Button UserInfo;
    @FXML private Button UserVs;
    String temp, tempId;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserInfo.setOnAction(e-> {
            try {
                UserInfoAction(e);
            } catch (IOException | SQLException ioException) {
                ioException.printStackTrace();
            }
        });
        UserVs.setOnAction(e-> {
            try {
                UserVsAction(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }
    public void UserInfoAction(ActionEvent e) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Info.fxml"));
        Parent root = (Parent) loader.load();

        InfoController ic = loader.getController();
        ic.initData(temp);

        Stage stage = new Stage();
        stage.setTitle("INFO");
        stage.setScene(new Scene(root));
        stage.show();
    }
    public void UserVsAction(ActionEvent e) throws IOException {//1:1신청하는 구현
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
        Parent root = (Parent) loader.load();
        Client client = loader.getController();
        client.initData("INVITE"+"#"+tempId+"#"+temp);
    }
    public void initData(String data, String id) {
        temp = data;
        tempId = id;
    }

}
