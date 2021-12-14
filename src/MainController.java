/*import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private TextField ChattingField;
    @FXML private TextArea ChattingArea;
    @FXML private TextArea UserRankingArea;
    @FXML private TextArea UserListArea;
    @FXML private Button sendChatting;
    @FXML private ListView<String> UserRanking;
    Database db = new Database();
    ResultSet result1 = db.viewRank();
    ObservableList list = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendChatting.setOnAction(event -> sendMessage(event));
        try {
            loadData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void handleBtnAction(ActionEvent event){
        Platform.exit();
    }

    public void sendMessage(ActionEvent event) {
        // 여기서 server에게
        String msg = ChattingField.getText();
        ChattingArea.appendText('\n' + msg + '\n');
        ChattingField.clear();
    }

    public void loadData() throws SQLException {
        String printStr=null;
        for(int i=0; i<4; i++) {
            printStr=null;
            result1.next();
            printStr = result1.getString("nickname") + " " + result1.getString("win") + " " + result1.getString("draw") + " " + result1.getString("lose");
            list.add(printStr);

        }
        UserRanking.getItems().addAll(list);
    }

}*/



