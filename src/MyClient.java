import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
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
            // ArrayList<Server> servergroup = new ArrayList<Server>();
            // sendandprint("REDY");
            // String nRec = sendandreceive("GETS All").split(" ")[1];
            // ArrayList<String> servers = receivelistsimple();
            // for (String server : servers) {
            //     servergroup.add(new Server(server));
            // }
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
                        Job jobn = new Job(conwords(reply));
                        Server selectedserver = getbestfit2(jobn.getspec());
                        // selectedserver = getfirstcapable(reply[4], reply[5], reply[6]);
                        // selectedserver = getbestfit(reply[4], reply[5], reply[6]);
                        println("Selected Server=" + selectedserver);

                        
                        sendandprint("SCHD " + jobn.jobID + " " + selectedserver.serverType + " " + selectedserver.serverID);
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

    public static ArrayList<String[]> getcapable(String cores, String mem, String disk) throws Exception {
        println("Getting capable servers...");
        String[] data = sendandreceive("GETS Capable " + cores + " " + mem + " " + disk).split(" "); // primary command
        String nRecs = data[1]; // parameters
        return receivelist(nRecs); // retrieve list
    }

    public static ServerGroup getcapable2(Jobspec jobspec) throws Exception {
        println("Getting capable servers...");
        String message = sendandreceive("GETS Capable " + jobspec);
        Data data = new Data(message);
        ArrayList<String> reply = receivelist(data.getnrec());
        ServerGroup capable = new ServerGroup(reply);
        return capable; // retrieve list
    }

    public static ArrayList<String> receivelist(int nRecs) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        sendonly("OK");
        for (int i = 0; i < nRecs; i++) {
            String message = receiveonly();
            println(message);
            list.add(message);
        }
        sendandprint("OK");
        return list;
    }

    public static ArrayList<String> receivelistsimple() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        sendonly("OK");
        String m;
        do {
            m = receiveonly();
            println(m);
            list.add(m);
        } while (!m.equals("."));
        sendandprint("OK");
        return list;
    }

    public static String[] getfirstcapable(String cores, String mem, String disk) throws Exception {
        return getcapable(cores, mem, disk).get(0);
    }

    private static String[] getbestfit(String cores, String mem, String disk) throws Exception {
        println("Sorting servers based on core count...");
        ArrayList<String[]> servers = getcapable(cores, mem, disk);
        sortasc(servers, 4);
        println("List of servers: ");
        println(servers);

        for (String[] server : servers) {
            if (Integer.parseInt(server[4]) >= Integer.parseInt(cores)) {
                return server;
            }
        }
        return null;
    }

    private static Server getbestfit2(Jobspec jobspec) throws Exception {
        ServerGroup capable = getcapable2(jobspec);
        servergroup.merge(capable);
        return capable.getleastruntime();
    }

    public static ArrayList<String[]> gets() throws Exception {
        ArrayList<String[]> wordlist = new ArrayList<String[]>();
        int total = Integer.parseInt(sendandreceive("GETS All").split(" ")[1]);
        sendonly("OK");
        for (int i = 0; i < total; i++) {
            String s = receiveonly();
            println(s);
            wordlist.add(s.split(" "));
        }
        sendandprint("OK");
        return wordlist;
    }

    public static String conwords(String[] ss) throws Exception {
        if (ss == null)
            return "";
        String s = "";
        for (String string : ss)
            s += string + " ";
        return s;
    }

    private static void sortstr(ArrayList<String[]> srvList, int column) throws Exception {
        Comparator<String[]> comparator = new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                String str1 = o1[column];
                String str2 = o2[column];
                return str1.compareTo(str2);
            }
        };
        Collections.sort(srvList, comparator);
    }

    private static void sortdsc(ArrayList<String[]> srvList, int column) throws Exception {
        Comparator<String[]> comparator = new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                int num1 = Integer.parseInt(o1[column]);
                int num2 = Integer.parseInt(o2[column]);
                if (num1 < num2)
                    return 1;
                else if (num1 > num2)
                    return -1;
                else
                    return 0;
            }
        };
        Collections.sort(srvList, comparator);
    }

    private static void sortasc(ArrayList<String[]> srvList, int column) throws Exception {
        Comparator<String[]> comparator = new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                int num1 = Integer.parseInt(o1[column]);
                int num2 = Integer.parseInt(o2[column]);
                if (num1 > num2)
                    return 1;
                else if (num1 < num2)
                    return -1;
                else
                    return 0;
            }
        };
        Collections.sort(srvList, comparator);
    }

    public static void println(ArrayList<String[]> a) throws Exception {
        // String msg = "";
        // for (String sentance[] : a) {
        // msg+=conwords(sentance)+", ";
        // }
        // if (msg.length() >= 2) {
        // msg = msg.substring(0, msg.length()-2);
        // }
        // msg = "[" + msg + "]";

        for (String sentance[] : a)
            println(conwords(sentance));
    }

    public static void println(Object o) throws Exception {
        print(o + "\n");
    }

    public static void print(Object o) throws Exception {
        // System.out.print(o);
    }

    public static String timestamp() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).toString();
    }
}