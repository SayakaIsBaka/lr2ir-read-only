package bms.player.beatoraja.ir;

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
}
