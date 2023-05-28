import java.util.ArrayList;

public class Server {
    public String serverType, serverID, state, curStartTime, core, memory, disk, wJobs, rJobs;
    public ArrayList<Job> jobs;

    public Server() {
    }

    public Server(String string) {
        this.init(string);
    }

    private void init(String string) {
        String[] parse = string.split(" ");
        serverType = parse[0];
        serverID = parse[1];
        state = parse[2];
        curStartTime = parse[3];
        core = parse[4];
        memory = parse[5];
        disk = parse[6];
        wJobs = parse[7];
        rJobs = parse[8];
        jobs = new ArrayList<Job>();
    }

    public static String gettype(String string) {
        return string.split(" ")[0];
    }

    public String toString() {
        return serverType + " "
                + serverID + " "
                + state + " "
                + curStartTime + " "
                + core + " "
                + memory + " "
                + disk + " "
                + wJobs + " "
                + rJobs;
    }

    public void merge(Server sourceserver) {
        serverType = sourceserver.serverType;
        serverID = sourceserver.serverID;
        state = sourceserver.state;
        curStartTime = sourceserver.curStartTime;
        core = sourceserver.core;
        memory = sourceserver.memory;
        disk = sourceserver.disk;
        wJobs = sourceserver.wJobs;
        rJobs = sourceserver.rJobs;

        // add newer jobs from sourceserver
        for (int i = Math.max(jobs.size() - 1, 0); i < sourceserver.jobs.size(); i++) {
            jobs.add(sourceserver.jobs.get(i));
        }
    }

    public int getruntime() {
        int runtime = 0;
        for (Job job : jobs) {
            runtime = runtime + Integer.parseInt(job.estRuntime);
        }
        return runtime;
    }
}
