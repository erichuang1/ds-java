
public class Job {
    public String submitTime, jobID, estRuntime, core, memory, disk;

    public Job(String string) {
        String[] parse = string.split(" ");
        submitTime = parse[1];
        jobID = parse[2];
        estRuntime = parse[3];
        core = parse[4];
        memory = parse[5];
        disk = parse[6];
    }

    public Job(Job sourcejob) {
        submitTime = sourcejob.submitTime;
        jobID = sourcejob.jobID;
        estRuntime = sourcejob.estRuntime;
        core = sourcejob.core;
        memory = sourcejob.memory;
        disk = sourcejob.disk;
    }

    public Jobspec getspec() {
        return new Jobspec(core, memory, disk);
    }

    public String toString() {
        return "JOBN "
                + submitTime + " "
                + jobID + " "
                + estRuntime + " "
                + core + " "
                + memory + " "
                + disk;
    }

    public int getruntime() {
        return Integer.parseInt(estRuntime);
    }

}
