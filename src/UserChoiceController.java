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
import java.util.ResourceBundle;

public class UserChoiceController implements Initializable {

    @FXML private Button UserInfo;
    @FXML private Button UserVs;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserInfo.setOnAction(e-> {
            try {
                UserInfoAction(e);
            } catch (IOException ioException) {
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
    public void UserInfoAction(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Info.fxml"));
        Parent root = (Parent) loader.load();
        Stage stage = new Stage();
        stage.setTitle("INFO");
        stage.setScene(new Scene(root));
        stage.show();
    }
    public void UserVsAction(ActionEvent e) throws IOException {//1:1신청하는 구현
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Info.fxml"));
        Parent root = (Parent) loader.load();
        Stage stage = new Stage();
        stage.setTitle("VS");
        stage.setScene(new Scene(root));
        stage.show();
    }

}
