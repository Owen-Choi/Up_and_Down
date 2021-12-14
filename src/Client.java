import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class Client extends Application implements Initializable {
    static final int serverPort = 10033;
    static StringTokenizer st;
    static String userID = null;
    static Socket user;
    static DataOutputStream dos;
    static DataInputStream dis;
    @FXML
    public TextField ChattingField;
    @FXML
    public TextArea ChattingArea;
    @FXML
    private TextArea UserRankingArea;
    @FXML
    private TextArea UserListArea;
    @FXML
    public Button sendChatting;
    @FXML
    public ListView<String> UserRanking;
    Database db = new Database();
    ResultSet result1 = db.viewRank();
    ObservableList list = FXCollections.observableArrayList();

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("javaFX");
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));//login.fxml에 있는 정보를 불러옴
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);
        primaryStage.show();
        InetAddress ip = InetAddress.getByName("localhost");
        user = new Socket(ip, serverPort);
        dis = new DataInputStream(user.getInputStream());
        dos = new DataOutputStream(user.getOutputStream());
    }

    public void OpenLobby(Button loginBtn, String id) throws IOException {
        Parent mainPage = FXMLLoader.load(getClass().getResource("Lobby.fxml"));
        StackPane root = (StackPane) loginBtn.getScene().getRoot();
        root.getChildren().add(mainPage);

        this.Client_Setting(id);
    }

    public static void main(String[] args) throws IOException, UnknownHostException {
        launch(args);
    }

    public void Client_Setting(String uid) throws IOException, UnknownHostException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            userID = uid;
            // ID broadcasting : 서버에게 클라이언트의 이름을 알려준다.
            dos.writeUTF(userID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*Writer writer = new Writer(user, dis, dos, this, sendChatting);
        Reader reader = new Reader(user, dis, dos, writer, this);
        Thread writer_thread = new Thread(writer);
        Thread reader_thread = new Thread(reader);
        writer_thread.start();
        reader_thread.start();*/
        System.out.println("대기실에 입장되셨습니다.");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendChatting.setOnAction(event -> send(ChattingField.getText()));
        try {
            loadData();
            receive();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnAction(ActionEvent event){
        Platform.exit();
    }

    public void sendMessage(ActionEvent event) throws IOException {
        // 여기서 server에게
        String msg = ChattingField.getText();
        dos.writeUTF('\n' + msg + '\n');
        ChattingArea.appendText('\n' + msg + '\n');
        ChattingField.clear();
    }

    public void appendMessage(String msg) {
        this.ChattingArea.appendText(msg + '\n');
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

    void send(String data){
        // 데이터 전송 코드
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    //byte[] byteArr = data.getBytes("UTF-8");
                    //OutputStream outputStream = user.getOutputStream();
                    dos.writeUTF(data);
                    dos.flush();
                    Platform.runLater(()->displayText('\n' + data));
                } catch (IOException e) {
                    Platform.runLater(()->displayText("[서버 통신 안됨]"));
                    //stopClient();
                }

            }
        };
        thread.start();
    }


    void receive() throws IOException {
        // 데이터 받기 코드
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String data = dis.readUTF();
                        System.out.println(data);
                        //Platform.runLater(() -> displayText("[받기 완료]  " + data));
                        Platform.runLater(() -> ChattingArea.appendText(data));
                    } catch (IOException e) {
                        Platform.runLater(() -> displayText("[서버 통신 안됨]"));
                        //stopClient();
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    void displayText(String text){
        ChattingArea.appendText(text + "\n");
    }


}
