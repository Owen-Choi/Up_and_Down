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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
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
    static String userID = null;
    static Socket user;
    static DataOutputStream dos;
    static DataInputStream dis;
    @FXML public TextField ChattingField;
    @FXML public TextArea ChattingArea;
    @FXML private TextArea UserRankingArea;
    @FXML public Button sendChatting;
    @FXML public ListView<String> UserRanking;
    @FXML private ListView<String> UserList;
    static String msg;
    static String header = "CHAT#";
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
        scene.getStylesheets().add("UserChoice.css");
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
            loadRank();
            //loadList();
            receive();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    void send(String data){
        // 데이터 전송 코드
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    //byte[] byteArr = data.getBytes("UTF-8");
                    //OutputStream outputStream = user.getOutputStream();
                    dos.writeUTF(header + data);
                    dos.flush();
                    ChattingField.clear();
                    if(Client.header.equals("INVITE_REPLY#"))
                        Client.header = "CHAT#";
                    //Platform.runLater(()->displayText('\n' + data));
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
            String Input;
            @Override
            public void run() {
                while (true) {
                    try {
                        Input = dis.readUTF();
                        System.out.println(Input);
                        String data = HeadChecker(Input);
                        //Platform.runLater(() -> displayText("[받기 완료]  " + data));
                        Platform.runLater(() -> ChattingArea.appendText('\n' + data));
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

    void UpdateUserList(String UserNameList) {
        String userName = null;
        Platform.runLater(() -> UserList.getItems().clear());
        list.removeAll(list);
        StringTokenizer st = new StringTokenizer(UserNameList, "/");
        while(st.hasMoreTokens()) {
            userName = st.nextToken();
            if(userName != null)
                list.add(userName);
        }
        Platform.runLater(() -> UserList.getItems().addAll(list));
    }

    String HeadChecker(String data) {
        StringTokenizer st = new StringTokenizer(data, "#");
        String header = st.nextToken();
        if(header.equals("NEW_USER")) {
            String notice = st.nextToken();
            UpdateUserList(st.nextToken());
            return notice;
        }
        else if(header.equals("INVITE_REQUEST")) {
            Client.header = "INVITE_REPLY#";
            return st.nextToken();
        }
        else
            return st.nextToken();
    }

    public void loadRank() throws SQLException {
        ResultSet result1 = db.viewRank();
        String printStr=null;
        list.removeAll(list);
        // 유저 수 받아와서 변수에 넣어줘야겠다.

        while(!result1.isLast()){
            printStr = null;
            result1.next();
            printStr = result1.getString("nickname") + " " + result1.getString("win") + "WIN " + result1.getString("draw") + "DRAW " + result1.getString("lose")+"LOSE";
            list.add(printStr);
        }
        UserRanking.getItems().addAll(list);
    }

    @FXML
    public void displaySelected(MouseEvent event) throws IOException {
        String list = UserList.getSelectionModel().getSelectedItem();

        if(list==null || list.isEmpty()) {

        }
        else {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("UserChoice.fxml"));
                Parent root = (Parent) loader.load();

                UserChoiceController ucc = loader.getController();
                ucc.initData(list, userID);

                Stage stage = new Stage();
                stage.setTitle("Choice");
                stage.setScene(new Scene(root));
                stage.show();

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void initData(String data) throws IOException {
        msg = data;
        System.out.println(msg);
        dos.writeUTF(msg);
        receive();
    }

}
