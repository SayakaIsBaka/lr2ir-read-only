package bms.player.beatoraja.ir;

public class ScoreRival {
    private String hash;
    private int clear;
    private int notes;
    private int combo;
    private int pg;
    private int gr;
    private int gd;
    private int bd;
    private int pr;
    private int minbp;
    private int option;
    private int lastupdate;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getClear() {
        return clear;
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

    public int getGd() {
        return gd;
    }

    public void setGd(int gd) {
        this.gd = gd;
    }

    public int getBd() {
        return bd;
    }

    public void setBd(int bd) {
        this.bd = bd;
    }

    public int getPr() {
        return pr;
    }

    public void setPr(int pr) {
        this.pr = pr;
    }

    public int getMinbp() {
        return minbp;
    }

    public void setMinbp(int minbp) {
        this.minbp = minbp;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public int getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(int lastupdate) {
        this.lastupdate = lastupdate;
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
}
