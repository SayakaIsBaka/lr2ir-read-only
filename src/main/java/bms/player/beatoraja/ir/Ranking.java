package bms.player.beatoraja.ir;

import bms.player.beatoraja.ScoreData;
import bms.player.beatoraja.ScoreDatabaseAccessor;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

public class Ranking {
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Score> score = new ArrayList<>();

    public List<Score> getScore() {
        return score;
    }

    public void setScore(List<Score> score) {
        this.score = score;
    }

    public IRScoreData[] toBeatorajaScoreData(IRChartData model, ScoreDatabaseAccessor scoredb) {
        List<Score> scores = getScore();
        List<IRScoreData> res = new ArrayList<>();
        for (Score s : scores) {
            ScoreData tmp = new ScoreData(model.mode);
            tmp.setSha256(model.sha256);
            tmp.setPlayer(s.getName());
            tmp.setClear(s.getBeatorajaClear());
            tmp.setNotes(s.getNotes());
            tmp.setCombo(s.getCombo());
            tmp.setEpg(s.getPg());
            tmp.setEgr(s.getGr());
            tmp.setMinbp(s.getMinbp());
            res.add(new IRScoreData(tmp));
        }
        /*if (lastScoreData != null && lastChart != null && lastChart.sha256.equals(model.sha256)) {
            System.out.println(lastScoreData.player);
            ScoreData tmp2 = new ScoreData(model.mode);
            tmp2.setSha256(model.sha256);
            tmp2.setPlayer(null);
            tmp2.setClear(lastScoreData.clear.id);
            tmp2.setNotes(lastScoreData.notes);
            tmp2.setCombo(lastScoreData.maxcombo);
            tmp2.setEpg(lastScoreData.epg);
            tmp2.setLpg(lastScoreData.lpg);
            tmp2.setEgr(lastScoreData.egr);
            tmp2.setLgr(lastScoreData.lgr);
            tmp2.setMinbp(lastScoreData.minbp);
            res.add(new IRScoreData(tmp2));
            lastScoreData = null;
            lastChart = null;
        } else*/ if (scoredb != null) {
            ScoreData s = scoredb.getScoreData(model.sha256, model.hasUndefinedLN ? model.lntype : 0);
            if (s != null) {
                s.setPlayer(null);
                res.add(new IRScoreData(s));
            }
        }
        return res.toArray(new IRScoreData[0]);
    }
}
