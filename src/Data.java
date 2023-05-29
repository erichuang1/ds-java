
public class Data {
    public String nRecs, recLen;

    public Data(String string) {
        String[] parse = string.split(" ");
        nRecs = parse[1];
        recLen = parse[2];
    }

    public int getnrec() {
        return Integer.parseInt(nRecs);
    }

    public String toString() {
        return "DATA " + nRecs + " " + recLen;
    }
}
