package babafeng.socketclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by babafeng on 2016/12/28.
 */

// socket客户端实现
public class SocketClient {
    public static String send(String host, int port, String data) throws Exception {
        Socket socket = new Socket(host, port);
        DataOutputStream dataoutputstream = new DataOutputStream(socket.getOutputStream());

        // write to server
        dataoutputstream.writeUTF(data);
        dataoutputstream.flush();

        // read from server
        DataInputStream datainputstream = new DataInputStream(socket.getInputStream());
        String message = datainputstream.readUTF();

        // Print message from server
        dataoutputstream.close();
        datainputstream.close();
        socket.close();
        return message;
    }
}