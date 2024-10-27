package bms.player.beatoraja.ir;

import bms.model.Mode;
import bms.player.beatoraja.ScoreData;
import bms.player.beatoraja.song.SongData;
import bms.player.beatoraja.song.SongDatabaseAccessor;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreList {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ScoreRival> score = new ArrayList<>();

    public List<ScoreRival> getScore() {
        return score;
    }

    public void setScore(List<ScoreRival> score) {
        this.score = score;
    }

    private Mode getEnumValue(int id) {
        for (Mode e : Mode.values()) {
            if (e.id == id) return e;
        }
        return Mode.BEAT_7K;
    }

    public IRScoreData[] toBeatorajaScoreData(SongDatabaseAccessor songdb, String player) {
        List<ScoreRival> scores = getScore();
        List<IRScoreData> res = new ArrayList<>();
        SongData[] sdata = songdb.getSongDatas(scores.stream().map(ScoreRival::getHash).toArray(String[]::new));
        for (ScoreRival s : scores) {
            SongData sd = null;
            try {
                sd = Arrays.stream(sdata).filter(x -> x.getMd5().equals(s.getHash())).findAny().orElse(null);
            } catch (Exception e) {
                continue;
            }
            if (sd == null)
                continue;
            ScoreData tmp = new ScoreData(getEnumValue(sd.getMode()));
            tmp.setSha256(sd.getSha256());
            tmp.setPlayer(player);
            tmp.setClear(s.getBeatorajaClear());
            tmp.setNotes(s.getNotes());
            tmp.setCombo(s.getCombo());
            tmp.setEpg(s.getPg());
            tmp.setEgr(s.getGr());
            tmp.setEgd(s.getGd());
            tmp.setEbd(s.getBd());
            tmp.setEpr(s.getPr());
            tmp.setMinbp(s.getMinbp());
            tmp.setOption(s.getOption());
            res.add(new IRScoreData(tmp));
        }
        return res.toArray(new IRScoreData[0]);
    }
}
