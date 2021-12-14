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
    @FXML public TextField ChattingField;
    @FXML public TextArea ChattingArea;
    @FXML private TextArea UserRankingArea;
    @FXML private TextArea UserListArea;
    @FXML public Button sendChatting;
    @FXML public ListView<String> UserRanking;
    Database db = new Database();
    ResultSet result1 = db.viewRank();
    ObservableList list = FXCollections.observableArrayList();
    public void start(Stage primaryStage)throws Exception {
        primaryStage.setTitle("javaFX");
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));//login.fxml에 있는 정보를 불러옴
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void OpenLobby(Button loginBtn) throws IOException {
        Parent mainPage = FXMLLoader.load(getClass().getResource("Lobby.fxml"));
        StackPane root = (StackPane) loginBtn.getScene().getRoot();
        root.getChildren().add(mainPage);
        // 로비 열었고 마음만 먹으면 text field/area 접근은 가능해졌는데 어떻게 쓰레드에 접근하지?
    }

    public static void main(String[] args) throws IOException, UnknownHostException {
        launch(args);
    }
    public void Client_Setting(String uid) throws IOException, UnknownHostException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        InetAddress ip = InetAddress.getByName("localhost");
        userID = uid;
        user = new Socket(ip, serverPort);
        dis = new DataInputStream(user.getInputStream());
        dos = new DataOutputStream(user.getOutputStream());
        // ID broadcasting : 서버에게 클라이언트의 이름을 알려준다.
        dos.writeUTF(userID);

        Writer writer = new Writer(user, dis, dos, this, sendChatting);
        Reader reader = new Reader(user, dis, dos, writer, this);
        Thread writer_thread = new Thread(writer);
        Thread reader_thread = new Thread(reader);
        writer_thread.start();
        reader_thread.start();
        System.out.println("대기실에 입장되셨습니다.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sendChatting.setOnAction(event -> {
            try {
                sendMessage(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            loadData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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


}

class Writer implements Runnable {
    Client client;
    Socket userOwn;
    DataOutputStream dos;
    DataInputStream dis;
    String HeadTag = null;
    BufferedReader br;
    Button sendChatting;

    // 소켓은 받을 필요 없나?
    public Writer(Socket userOwn, DataInputStream dis, DataOutputStream dos, Client client, Button sendChatting) {
        this.userOwn = userOwn;
        this.dis = dis;
        this.dos = dos;
        this.br = new BufferedReader(new InputStreamReader(System.in));
        this.client = client;
        this.sendChatting = sendChatting;
    }
    @Override
    public void run() {
            //ChangeMode에서 나오지 않으면 초대에 응할 수 없다.
            //이에 대한 조치는 플레이어가 원할때만 모드를 바꿀 수 있게 한다.
            HeadTag = TAG.CHAT.name();
            System.out.println("If you want to Change mode, please enter \"ChangeMode\" ");
            while(true) {
                sendChatting.setOnAction(event -> {
                    try {
                        client.sendMessage(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

    }


    public boolean ChangeMode() throws IOException{
        System.out.println("please choose the mode number : ");
        System.out.println("1. chatting \n" +
                "2. invite \n" +
                "3. show information ");
        int tempnumber;
        HeadTag = null;
        while(HeadTag == null) {
            tempnumber = Integer.parseInt(br.readLine());
            switch (tempnumber) {
                case 1:
                    HeadTag = TAG.CHAT.name();
                    break;
                case 2:
                    HeadTag = TAG.INVITE.name();
                    dos.writeUTF(TAG.INVITE.name() + "##" + "inviting user");
                    //HeadTag = TAG.INVITE_REPLY.name();
                    break;
                case 3:
                    HeadTag = TAG.SHOW_INFO.name();
                    break;
                default:
                    System.out.println("invalid value. try again");
                    return false;
            }
        }
        return true;
    }
    public void HeadTag_Setter(String newHeadTag) {
        HeadTag = newHeadTag;
    }

}

class Reader implements Runnable {
    Socket userOwn;
    DataOutputStream dos;
    DataInputStream dis;
    StringTokenizer st;
    Writer writer;
    TextArea ta;
    Client client;
    public Reader(Socket userOwn, DataInputStream dis, DataOutputStream dos, Writer writer,
                  Client client) {
        this.userOwn = userOwn;
        this.dis = dis;
        this.dos = dos;
        this.writer = writer;
        this.client = client;
    }
    @Override
    public void run() {
        try {
            while(true) {
                String msgToRead = dis.readUTF();
                //ta.appendText(msgToRead);
                //이 방법은 통하질 않으니 여기서 MainController에 접근해보자.
                Platform.runLater(() ->{
                    client.ChattingArea.appendText(msgToRead);
                });
                MSG_Processor(msgToRead);
            }

        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void MSG_Processor(String msgToRead) throws IOException{
        st = new StringTokenizer(msgToRead, "##");
        String Header = st.nextToken();
        if(Header.equals(TAG.INVITE_REPLY.name())) {
            // 헤더가 INVITE_REPLY라는 것은 사용자가 초대할 다른 사용자의 정보를 넘겨주어야 한다는 뜻이다.
            System.out.println(st.nextToken());
            writer.HeadTag_Setter(TAG.INVITE_REPLY.name());
        }
        else if(Header.equals(TAG.INVITE_REQUEST.name())) {
            String inviter = st.nextToken();
            System.out.println(inviter + " " + st.nextToken());
            System.out.println("초대에 응하실려면 1, 거절하실려면 2를 입력해주세요.");
            //INVITE_PD 뒤에 1이 붙으면 수락, 2가 붙으면 거절의 의미이다.
            writer.HeadTag_Setter(TAG.INVITE_PD.name()+"##"+inviter+"##");
        }
        else if(Header.equals(TAG.INVITE_PERMIT.name())) {
            System.out.println(st.nextToken());
            //게임 진행에 대비할 것 있으면 하기
        }
        else if(Header.equals(TAG.INVITE_DENY.name())) {
            System.out.println(st.nextToken());
        }
        // Header가 위 조건문에 속하지 않는다는 것은 다른 user가 보낸 채팅이라는 뜻이다.
        else
            System.out.println(Header + " : " + st.nextToken());
    }
}