
public class Jobspec {
    public String core, memory, disk;

    public Jobspec(String c, String m, String d) {
        core = c;
        memory =m;
        disk= d;
    }

    public String toString() {
        return core + " "
                + memory + " "
                + disk;
    }
}
