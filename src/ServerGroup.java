import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
        servers.put(server.serverType, server);
    }

    public void merge(Server sourceserver) {
        Server server = servers.get(sourceserver.serverType);
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
            int runtime = server.getruntime();
            if (runtime < leastruntime) {
                leastruntime = runtime;
                leasttype = server.serverType;
            }
        }
        return servers.get(leasttype);
    }

}
