import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyClient {
    public static Socket socket;
    public static BufferedReader din;
    public static DataOutputStream dout;
    public static int dbc = 0;

    public static void main(String[] args) {
        try {
            // initialise connection
            socket = new Socket("localhost", 51200);
            din = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dout = new DataOutputStream(socket.getOutputStream());

            // handshake
            sendmsg("HELO");
            sendmsg("AUTH " + System.getProperty("user.name"));
            sendmsg("REDY");

            // get servers
            ArrayList<String[]> srvList = gets();
            sortstr(srvList, 0); // sort by server type
            sortasc(srvList, 1); // sort by child server id
            sortdsc(srvList, 4); // sort by core count
            // for (String[] s : srvList) 
            //     println(conwords(s));
            // Thread.sleep(100000);

            // job scheduling
            String keyword = "";
            while (!keyword.equals("NONE")) {
                String[] words;

                // JCPL skip & reply parsing
                do {
                    String reply = sendreceive("REDY");
                    if (reply.contains("JOBN 2396"))
                        dbc++;
                    println("S: " + reply);
                    words = reply.split(" ");
                    keyword = words[0];
                } while (keyword.equals("JCPL"));

                // NONE break
                if (keyword.equals("NONE"))
                    break;

                // JOBN stub
                if (keyword.equals("JOBN")) {
                    String jobid = words[2];
                    // capable (job-server) check
                    boolean capable = true;
                    for (String[] server : srvList) {
                        for (int i = 4; i <= 6; i++) {
                            capable = Integer.parseInt(server[i]) >= Integer.parseInt(words[i]);
                            if (!capable)
                                break;
                        }
                        if (capable) { // , then schedule the job
                            sendmsg("SCHD " + jobid + " " + server[0] + " " + server[1]);
                            break;
                        } else { // if not capable
                            println("C: Current server incapable of running such job. (server=" + conwords(server).trim()
                                    + ")");
                        }
                    }
                }
            }

            // close connection
            sendmsg("QUIT");
            dout.flush();
            dout.close();
            socket.close();
        } catch (

        Exception e) {
            System.out.println(e);
        }
    }

    public static void sendmsg(String s) throws Exception {
        // Thread.sleep(500);
        println("S: " + sendreceive(s));
    }

    public static String sendreceive(String s) throws Exception {
        send(s);
        return receive();
    }

    public static String receive() throws Exception {
        return din.readLine().trim();
    }

    public static void send(String s) throws Exception {
        println("C: " + s);
        dout.write((s + "\n").getBytes());
    }

    public static ArrayList<String[]> gets() throws Exception {
        ArrayList<String[]> wordlist = new ArrayList<String[]>();
        int total = Integer.parseInt(sendreceive("GETS All").split(" ")[1]);
        send("OK");
        for (int i = 0; i < total; i++) {
            String s = receive();
            println(s);
            wordlist.add(s.split(" "));
        }
        sendmsg("OK");
        return wordlist;
    }

    public static String conwords(String[] ss) throws Exception {
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

    public static void println(Object o) throws Exception {
        System.out.println(o);
    }

    public static void print(Object o) throws Exception {
        System.out.print(o);
    }

    public static String timestamp() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).toString();
    }
}