package bms.player.beatoraja.ir;

public class LR2IRSongData {
    public String md5;
    public String id;
    public String lastUpdate;

    LR2IRSongData(String md5, String id) {
        this.md5 = md5;
        this.id = id;
        this.lastUpdate = "";
    }

    public String toUrlEncodedForm() {
        return "songmd5=" + md5 + "&id=" + id + "&lastupdate=" + lastUpdate;
    }
}
