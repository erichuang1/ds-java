import java.io.*;
import java.net.*;
import java.util.*;

public class MyClient {
    public static Socket socket;
    public static BufferedReader din;
    public static DataOutputStream dout;
    public static ServerGroup servergroup;

    public static void main(String[] args) {
        try {
            // initialise connection
            socket = new Socket("localhost", 50000);
            din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dout = new DataOutputStream(socket.getOutputStream());

            // handshake
            sendandprint("HELO");
            sendandprint("AUTH " + System.getProperty("user.name"));

            // get all servers
            // println("Getting all servers...");
            // sendandprint("REDY");
            // String message2 = sendandreceive("GETS All");
            // Data data2 = new Data(message2);
            // ArrayList<String> reply2 = receivelist(data2.getnrec());
            // servergroup = new ServerGroup(reply2);
            servergroup = new ServerGroup();

            // command branches
            String keyword;
            do {
                // Sample reply
                // JOBN 197 16 45499 1 200 1400
                String[] reply = sendandreceive("REDY").split(" ");
                println("S: " + conwords(reply));
                keyword = reply[0];

                switch (keyword) {
                    case "JCPL":
                        break;
                    case "NONE":
                        break;
                    case "JOBN":
                        Job job = new Job(conwords(reply));
                        String s = job.toString();
                        Server selectedserver = getbestfit(job.getspec());
                        // Server selectedserver = getbestfit(job.getspec());
                        println("Selected Server=" + selectedserver);
                        sendandprint(
                                "SCHD " + job.jobID + " " + selectedserver.serverType + " " + selectedserver.serverID);
                        selectedserver.jobs.add(job);
                        break;

                    default:
                        break;
                }
            } while (!keyword.equals("NONE"));

            // close connection
            sendandprint("QUIT");
            dout.flush();
            dout.close();
            socket.close();
        } catch (

        Exception e) {
            System.out.println(e);
        }
    }

    public static void sendandprint(String s) throws Exception {
        // Thread.sleep(500);
        println("S: " + sendandreceive(s));
    }

    public static String sendandreceive(String s) throws Exception {
        sendonly(s);
        return receiveonly();
    }

    public static String receiveonly() throws Exception {
        return din.readLine().trim();
    }

    public static void sendonly(String s) throws Exception {
        println("C: " + s);
        dout.write((s + "\n").getBytes());
    }

    public static String[] getj() throws Exception {
        String[] reply;
        do {
            reply = sendandreceive("REDY").split(" ");
            println("S: " + conwords(reply));
        } while (reply[0].equals("JCPL")); // JCPL skip
        if (reply[0].equals("NONE"))
            return null;
        return reply;
    }

    public static ServerGroup getcapable(Jobspec jobspec) throws Exception {
        println("Getting capable servers...");
        String message = sendandreceive("GETS Capable " + jobspec);
        println("S: " + message);
        Data data = new Data(message);
        ArrayList<String> reply = receivelist(data.getnrec());
        if (reply == null || reply.size() == 0)
            return null;
        ServerGroup capable = new ServerGroup(reply);
        return capable; // retrieve list
    }

    public static ServerGroup getavailable(Jobspec jobspec) throws Exception {
        println("Getting available servers...");
        String message = sendandreceive("GETS Avail " + jobspec);
        println("S: " + message);
        Data data = new Data(message);
        ArrayList<String> reply = receivelist(data.getnrec());
        if (reply == null || reply.size() == 0)
            return null;
        ServerGroup available = new ServerGroup(reply);
        return available; // retrieve list
    }

    public static ArrayList<String> receivelist(int nRecs) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        if (nRecs <= 0) {
            sendandprint("OK");
            return list;
        }
        sendonly("OK");
        for (int i = 0; i < nRecs; i++) {
            String message = receiveonly();
            println(message);
            list.add(message);
        }
        sendandprint("OK");
        return list;
    }

    private static Server getbestfit(Jobspec jobspec) throws Exception {
        ServerGroup source = getavailable(jobspec);
        if (source == null) {
            sendandprint("REDY");
            source = getcapable(jobspec);
        }
        source = ServerGroup.filterbyleastcore(source);
        servergroup.merge(source);
        source.updateonly(servergroup);
        return source.getleastruntime();
    }

    public static String conwords(String[] ss) throws Exception {
        if (ss == null)
            return "";
        String s = "";
        for (String string : ss)
            s += string + " ";
        return s;
    }

    public static void println(ArrayList<String[]> a) throws Exception {
        for (String sentance[] : a)
            println(conwords(sentance));
    }

    public static void println(Object o) throws Exception {
        print(o + "\n");
    }

    public static void print(Object o) throws Exception {
        // Thread.sleep(100);
        // System.out.print(o);
    }
}