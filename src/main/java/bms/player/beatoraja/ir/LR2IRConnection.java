package bms.player.beatoraja.ir;

import bms.player.beatoraja.Config;
import bms.player.beatoraja.ScoreData;
import bms.player.beatoraja.ScoreDatabaseAccessor;
import bms.player.beatoraja.song.SQLiteSongDatabaseAccessor;
import bms.player.beatoraja.song.SongDatabaseAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LR2IRConnection implements IRConnection {
    public static final String NAME = "LR2IR Read Only";
    public static final String HOME = "";
    public static final String VERSION = "0.0.1";

    private static final String IRUrl = "http://dream-pro.info/~lavalse/LR2IR/2";

    private static IRScoreData lastScoreData = null;
    private static IRChartData lastChart = null;
    private static ScoreDatabaseAccessor scoredb = null;
    private static SongDatabaseAccessor songdb = null;
    private static String userId = "";
    private static Document myPage = null;

    private static Object convertXMLToObject(String xml, Class c) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            Object res = xmlMapper.readValue(xml, c);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static IRScoreData[] lr2ScoreDataToBeatoraja(Ranking ranking, IRChartData model) {
        List<Score> scores = ranking.getScore();
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

    private String makePOSTRequest(String uri, String data) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(IRUrl + uri);

            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_FORM_URLENCODED);
            httpPost.setEntity(entity);
            httpPost.setHeader("Connection", "close");

            CloseableHttpResponse response = client.execute(httpPost);
            String r = EntityUtils.toString(response.getEntity(), "SHIFT-JIS");

            boolean res = response.getStatusLine().getStatusCode() == 200;
            client.close();
            if (res)
                return r;
            else
                return null;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public IRResponse<IRPlayerData> register(String id, String pass, String name) {
        return null;
    }

    public IRResponse<IRPlayerData> login(String id, String pass) {
        Config c = Config.read();
        try {
            scoredb = new ScoreDatabaseAccessor(c.getPlayerpath() + File.separatorChar + c.getPlayername() + File.separatorChar + "score.db");
            songdb = new SQLiteSongDatabaseAccessor(c.getSongpath(), c.getBmsroot());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error opening score.db, shown ranking will not take PB into account");
        }
        userId = id;
        ResponseCreator<IRPlayerData> rc = new ResponseCreator<>();
        try {
            myPage = Jsoup.connect("http://www.dream-pro.info/~lavalse/LR2IR/search.cgi?mode=mypage&playerid=" + id).get();
        } catch (IOException e) {
            e.printStackTrace();
            return rc.create(false, "Error getting myPage, invalid user ID?", null);
        }
        String name = myPage.selectXpath("//table[1]/tbody/tr[1]/td").text();
        return rc.create(true, "Using user " + name, new IRPlayerData(id, name, ""));
    }

    public IRResponse<Object> sendPlayData(IRChartData model, IRScoreData score) {
        ResponseCreator<Object> rc = new ResponseCreator<>();
        System.out.println(score.player);
        lastChart = model;
        lastScoreData = score;
        return rc.create(true, "sendPlayData", null);
    }

    public IRResponse<Object> sendCoursePlayData(IRCourseData course, IRScoreData score) {
        return null;
    }

    public IRResponse<IRPlayerData[]> getRivals() {
        ResponseCreator<IRPlayerData[]> rc = new ResponseCreator<>();
        Elements r = myPage.selectXpath("//table[1]/tbody/tr[last()]/td/a");
        List<IRPlayerData> res = new ArrayList<>();
        for (Element e : r) {
            String name = e.text();
            String url = e.attr("href");
            String id = "";
            try {
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), Charset.forName("SHIFT-JIS"));
                id = params.stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue)).get("playerid");
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Rival: " + name);
            System.out.println("ID: " + id);
            res.add(new IRPlayerData(id, name, ""));
        }
        return rc.create(true, "Successfully parsed rivals", res.toArray(new IRPlayerData[0]));
    }

    public IRResponse<IRTableData[]> getTableDatas() {
        ResponseCreator<IRTableData[]> rc = new ResponseCreator<>();
        return rc.create(false, "Unimplemented.", new IRTableData[0]);
    }

    public IRResponse<IRScoreData[]> getPlayData(IRPlayerData irpd, IRChartData model) {
        ResponseCreator<IRScoreData[]> rc = new ResponseCreator<>();
        if (irpd != null) { // Rival loading
            try {
                String res = makePOSTRequest("/getplayerxml.cgi", "id=" + irpd.id + "&lastupdate=");
                ScoreList scoreList = (ScoreList) convertXMLToObject(res.substring(1).replace("<rivalname>" + irpd.name + "</rivalname>", ""), ScoreList.class);
                IRScoreData[] scoreData = scoreList.toBeatorajaScoreData(songdb, irpd.name);
                return rc.create(true, "Rival Data", scoreData);
            } catch (Exception e) {
                e.printStackTrace();
                return rc.create(false, "Internal Exception", new IRScoreData[0]);
            }
        }
        LR2IRSongData lr2IRSongData = new LR2IRSongData(model.md5, "114328");
        try {
            String res = makePOSTRequest("/getrankingxml.cgi", lr2IRSongData.toUrlEncodedForm());
            Ranking ranking = (Ranking)convertXMLToObject(res.substring(1).replace("<lastupdate></lastupdate>", ""), Ranking.class);
            IRScoreData[] scoreData = lr2ScoreDataToBeatoraja(ranking, model);
            System.out.println("Retrieved data from LR2IR");
            return rc.create(true, "Score", scoreData);
        } catch (Exception e) {
            e.printStackTrace();
            return rc.create(false, "Internal Exception", new IRScoreData[0]);
        }
    }

    public IRResponse<IRScoreData[]> getCoursePlayData(IRPlayerData irpd, IRCourseData course) {
        ResponseCreator<IRScoreData[]> rc = new ResponseCreator<>();
        return rc.create(false, "Unimplemented.", new IRScoreData[0]);
    }

    public String getSongURL(IRChartData song) {
        return "http://www.dream-pro.info/~lavalse/LR2IR/search.cgi?mode=ranking&bmsmd5=" + song.md5;
    }

    public String getCourseURL(IRCourseData course) {
        return null;
    }

    public String getPlayerURL(IRPlayerData irpd) {
        return "http://www.dream-pro.info/~lavalse/LR2IR/search.cgi?mode=mypage&playerid=" + irpd.id;
    }
}

