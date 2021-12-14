import javafx.scene.Parent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.util.Vector;

public class Server {
    static final int port = 10033;
    static int user_num = 0;
    static Accept accept_thread;
    static Vector<Client_Handler> user_list = new Vector<>();
    static String nameStore;
    static Database db = new Database();
    static String inviter = "", invitee = "";
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        accept_thread = new Accept(serverSocket);
        Thread at = new Thread(accept_thread);
        at.start();
        // 메인 대기실 프레임.
        /*JFrame mainLobby = new Main_Lobby("Main Lobby");
        mainLobby.setVisible(true);*/
    }
    public void Create_Handler(Socket user, DataInputStream dis, DataOutputStream dos, boolean canPlay) throws IOException {
        Client_Handler client_handler = new Client_Handler(user, dis, dos, true);
        user_list.add(client_handler);
        client_handler.setID();
        System.out.println("new user : " + client_handler.userID);
        String nameList = "";
        for(Client_Handler temp : user_list) {
            nameList = nameList + '/' + temp.userID;
        }
        for(Client_Handler temp : user_list) {
            temp.dos.writeUTF("NEW_USER#" + "new user : " + client_handler.userID + "#" + nameList);
        }
        Thread t = new Thread(client_handler);
        t.start();
    }

    public void MSGSend(String msg) throws IOException{
        for(Client_Handler temp : user_list) {
            temp.dos.writeUTF(msg);
        }
    }

    /*public String getID() {
        return nameStore;
    }
    public void setID(String tempName) {
        nameStore = tempName;
    }*/

}


class Client_Handler implements Runnable {
    Socket user;
    final DataInputStream dis;
    final DataOutputStream dos;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String userID;
    boolean CanPlay;
    //유저 정보 필요한거 더 추가하기. 고유번호
    //로그인 후 서버가 만들어 줄 클라이언트의 정보에 대한 생성자.
    public Client_Handler(Socket user, DataInputStream dis, DataOutputStream dos, boolean CanPlay) throws IOException {
        this.user = user;
        this.dis = dis;
        this.dos = dos;
        // Client가 만들어진 직후 BroadCast를 통해 할당되므로 생성자에서는 null로 초기화한다.
        this.userID = null;
        this.CanPlay = CanPlay;
    }
    @Override
    public void run() {
        String msg;
        try {
            while (true) {
                msg = dis.readUTF();
                System.out.println(msg);
                //Spread(msg);
                //customed protocol
                MSG_Processor(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setID() throws IOException{
        String ID_broadCast;
        // 클라이언트가 최초에 자신의 ID를 보내줄 것이다.
        // 그 ID로 Client_handler의 ID를 설정한 뒤 실행시킨다.
        // 이렇게 하는 이유는 Accept_thread에서 ID를 바로 바꿀 방법이 없기 때문이다.
        ID_broadCast = dis.readUTF();
        this.userID = ID_broadCast;
    }

    public void Spread(String msg) throws IOException {
        for(Client_Handler temp : Server.user_list) {
            temp.dos.writeUTF("CHAT#" + this.userID + " : " + msg);
        }
    }

    public void MSG_Processor(String msg) throws IOException {
        StringTokenizer st = new StringTokenizer(msg, "#");
        String tempHeader = st.nextToken();
        switch (tempHeader) {
            case "INVITE" :
                String inviter, invitee;
                inviter = st.nextToken();
                invitee = st.nextToken();
                Server.inviter = inviter;
                Server.invitee = invitee;
                for(Client_Handler temp : Server.user_list) {
                    if(temp.userID.equals(invitee)) {
                        temp.dos.writeUTF("INVITE_REQUEST#" + inviter + " " + "want to invite you, (Y/N)");
                        dos.flush();
                        break;
                    }
                }
                /*for(Client_Handler temp : Server.user_list) {
                    if(temp.userID.equals(inviter)) {
                        temp.dos.writeUTF("CHAT#" + "SYSTEM" + " : " + "waiting for " + invitee + "'s answer");
                        dos.flush();
                        break;
                    }
                }*/
                break;
            case "CHAT" :
                System.out.println(msg);
                String tempMsg = st.nextToken();
                for(Client_Handler temp : Server.user_list) {
                    temp.dos.writeUTF("CHAT#" + this.userID + " : " + tempMsg);
                }
                break;
            case "INVITE_REPLY" :
                String temp = st.nextToken();
                if(temp.equalsIgnoreCase("Y")) {
                    for(Client_Handler templist : Server.user_list) {
                        if(templist.userID.equals(Server.invitee) || templist.userID.equals(Server.inviter))
                            templist.dos.writeUTF("CHAT#"+"Match Start.");
                    }
                }
                else {
                    for(Client_Handler templist : Server.user_list) {
                        if(templist.userID.equals(Server.invitee) || templist.userID.equals(Server.inviter))
                            templist.dos.writeUTF("CHAT#"+"Denied from " + Server.invitee);
                    }
                }
                break;
        }
    }
}

