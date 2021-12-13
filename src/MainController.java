import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private TextField ChattingField;
    @FXML private TextArea ChattingArea;
    @FXML private TextArea UserRankingArea;
    @FXML private TextArea UserListArea;
    @FXML private Button sendChatting;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendChatting.setOnAction(event -> sendMessage(event));
    }

    public void handleBtnAction(ActionEvent event){
        Platform.exit();
    }

    public void sendMessage(ActionEvent event) {
        String msg = ChattingField.getText();
        ChattingArea.appendText('\n' + msg + '\n');
        ChattingField.clear();
    }
}
