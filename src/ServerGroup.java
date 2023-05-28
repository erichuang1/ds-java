import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ServerGroup {
    HashMap<String, Server> servers;

    public ServerGroup() {
        servers = new HashMap<String, Server>();
    }

    public ServerGroup(ArrayList<String> reply) {
        servers = new HashMap<String, Server>();
        for (String string : reply) {
            this.add(string);
        }
    }

    public void add(String string) {
        Server server = new Server(string);
        this.add(server);
    }

    public void add(Server server) {
        servers.put(server.key, server);
    }

    public void merge(Server sourceserver) {
        Server server = servers.get(sourceserver.key);
        if (server == null)
            this.add(sourceserver);
        else
            server.merge(sourceserver);
    }

    public void merge(ServerGroup servergroup) {
        for (Map.Entry<String, Server> entry : servergroup.servers.entrySet()) {
            this.merge(entry.getValue());
        }
    }

    public Server getleastruntime() {
        int leastruntime = Integer.MAX_VALUE;
        String leasttype = "";
        for (Map.Entry<String, Server> entry : servers.entrySet()) {
            Server server = entry.getValue();
            int runtime = server.printruntime();
            if (runtime < leastruntime) {
                leastruntime = runtime;
                leasttype = server.key;
            }
        }
        return servers.get(leasttype);
    }

    public String printruntime() {
        String s = "";
        for (Map.Entry<String, Server> entry : servers.entrySet()) {
            Server server = entry.getValue();
            s += server + ", runtime=" + server.printruntime() + "\n";
        }
        return s;
    }

    public String toString() {
        String s = "";
        for (Map.Entry<String, Server> entry : servers.entrySet()) {
            Server server = entry.getValue();
            s += server
                    + "\nruntime=" + server.printruntime()
                    + "\njobs=" + server.printjobs();
        }
        return s;
    }

    public static ServerGroup filterbyleastcore(ServerGroup sourceservergroup) {
        int leastcore = Integer.MAX_VALUE;
        for (Map.Entry<String, Server> entry : sourceservergroup.servers.entrySet()) {
            Server server = entry.getValue();
            if (server.getcore() < leastcore)
                leastcore = server.getcore();
        }

        ServerGroup servergroup = new ServerGroup();
        for (Map.Entry<String, Server> entry : sourceservergroup.servers.entrySet()) {
            Server server = entry.getValue();
            if (server.getcore() == leastcore)
                servergroup.add(server);
        }
        return servergroup;
    }

    public void updateonly(ServerGroup servergroup) {
        for (Map.Entry<String, Server> entry : servergroup.servers.entrySet()) {
            Server server = entry.getValue();
            if (servers.get(server.key) != null)
                servers.replace(server.key, server);
        }
    }

    // private void sortasc(ArrayList<Server> serverlist) {
    // Comparator<Server> comparator = new Comparator<Server>() {
    // @Override
    // public int compare(Server o1, Server o2) {
    // int num1 = o1.getcore();
    // int num2 = o2.getcore();
    // if (num1 > num2)
    // return 1;
    // else if (num1 < num2)
    // return -1;
    // else
    // return 0;
    // }
    // };
    // Collections.sort(serverlist, comparator);
    // }

    // private void sortstr(ArrayList<String[]> srvList, int column) throws
    // Exception {
    // Comparator<String[]> comparator = new Comparator<String[]>() {
    // @Override
    // public int compare(String[] o1, String[] o2) {
    // String str1 = o1[column];
    // String str2 = o2[column];
    // return str1.compareTo(str2);
    // }
    // };
    // Collections.sort(srvList, comparator);
    // }

    // private void sortdsc(ArrayList<String[]> srvList, int column) throws
    // Exception {
    // Comparator<String[]> comparator = new Comparator<String[]>() {
    // @Override
    // public int compare(String[] o1, String[] o2) {
    // int num1 = Integer.parseInt(o1[column]);
    // int num2 = Integer.parseInt(o2[column]);
    // if (num1 < num2)
    // return 1;
    // else if (num1 > num2)
    // return -1;
    // else
    // return 0;
    // }
    // };
    // Collections.sort(srvList, comparator);
    // }
}
