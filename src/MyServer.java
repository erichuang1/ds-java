import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

public class MyServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(6666);
            Socket s = ss.accept();// establishes connection
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());  
            System.out.println(timestamp() + " client-msg= " + dis.readUTF());
            dos.writeUTF(timestamp() + " " + "G'DAY");
            System.out.println(timestamp() + " client-msg= " + dis.readUTF());
            dos.writeUTF(timestamp() + " " + "BYE");
            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static String timestamp() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).toString();
    }
}