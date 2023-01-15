package model;

public class Cll {
    private final String cllUnicode;
    private final String skdUnicode;
    private final String cllName;

    public Cll(String cllUnicode, String skdUnicode, String cllName) {
        this.cllUnicode = cllUnicode;
        this.skdUnicode = skdUnicode;
        this.cllName = cllName;
    }

    public int getCllUnicode() {
        return Integer.parseInt(cllUnicode);
    }

    public String getSkdUnicode() {
        return skdUnicode;
    }

    public String getCllName() {
        return cllName;
    }
}
