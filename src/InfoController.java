import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class InfoController implements Initializable {
    @FXML  private Button cancelBtn;
    @FXML  private Label ID;
    @FXML  private Label NAME;
    @FXML  private Label NICKNAME;
    @FXML  private Label RECORD;
    @FXML  private Label CONNECTION;
    @FXML  private Label EMAIL;
    ResultSet info;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

   }

      //Label마다 DB로 부터 받은 유저정보로 개인정보띄움

    public void close() {
        Stage pop;
        pop = (Stage) cancelBtn.getScene().getWindow();
        pop.close();
    }

    public void initData(String data) throws SQLException {
        Database db = new Database();
        info = db.viewInfo(data);
        info.next();
        try {
            ID.setText(info.getString("ID"));
            NAME.setText(info.getString("NAME"));
            NICKNAME.setText(info.getString("NICKNAME"));
            RECORD.setText(info.getString("WIN"));
            CONNECTION.setText(info.getString("IP"));
            EMAIL.setText(info.getString("EMAIL"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}

