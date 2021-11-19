import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class Accept implements Runnable{
    ServerSocket serverSocket;
    Socket client;
    DataInputStream dis;
    DataOutputStream dos;
    String name;
    Server nts = new Server();
    public Accept(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while(true) {
            try {
                client = serverSocket.accept();
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                nts.Create_Handler(client, dis, dos, true);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
