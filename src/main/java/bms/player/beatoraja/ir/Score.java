package bms.player.beatoraja.ir;

public class Score {
    private String name;
    private int id;
    private int clear;
    private int notes;
    private int combo;
    private int pg;
    private int gr;
    private int minbp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClear() {
        return clear;
    }

    public int getBeatorajaClear() {
        switch (clear) {
            case 1: // Failed
                return 1;
            case 2: // Easy
                return 4;
            case 3: // Groove
                return 5;
            case 4: // Hard
                return 6;
            case 5: // FC
                if (pg + gr == notes) // Perfect
                    return 9;
                else
                    return 8;
            default:
                return 0;
        }
    }

    public void setClear(int clear) {
        this.clear = clear;
    }

    public int getNotes() {
        return notes;
    }

    public void setNotes(int notes) {
        this.notes = notes;
    }

    public int getCombo() {
        return combo;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public int getPg() {
        return pg;
    }

    public void setPg(int pg) {
        this.pg = pg;
    }

    public int getGr() {
        return gr;
    }

    public void setGr(int gr) {
        this.gr = gr;
    }

    public int getMinbp() {
        return minbp;
    }

    public void setMinbp(int minbp) {
        this.minbp = minbp;
    }
}
