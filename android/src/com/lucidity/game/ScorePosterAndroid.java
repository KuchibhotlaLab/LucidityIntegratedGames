package com.lucidity.game;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScorePosterAndroid implements ScorePoster {
    Context context;
    //urls for uploading scores
    private String url_block_game = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_blockgame_score.php";
    private String url_obj_game = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_objectrecognitiongame_score.php";
    private String url_sp_game = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_spatialgame_score.php";
    private String url_ntf_game = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_nametofacegame_score.php";
    private String url_ftn_game = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_facetonamegame_score.php";
    private String url_re_game = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_recallgame_score.php";
    private String url_full_test = "http://ec2-174-129-156-45.compute-1.amazonaws.com/lucidity/add_full_test_suite.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //remembers the start time of the testSuite
    private String testSuiteStartTime;

    public ScorePosterAndroid(Context c) {
        context = c;
    }

    public ScorePosterAndroid(Context c, String time) {
        context = c;
        testSuiteStartTime = time;
    }

    public void postScoreBlock(String username, String dateTime, String location, String menu,
                               String difficulty, int score, int[] trialSuccess, double[][] attemptTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-BlockGameScores")
                .build();
        BlockGameScoreDAO gameScoreDAO = database.getBlockGameScoreDAO();

        BlockGameScore gameScore = new BlockGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setDifficulty(difficulty);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime11(attemptTime[0][0]);
        gameScore.setTrialtime12(attemptTime[0][1]);
        gameScore.setTrialtime13(attemptTime[0][2]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime21(attemptTime[1][0]);
        gameScore.setTrialtime22(attemptTime[1][1]);
        gameScore.setTrialtime23(attemptTime[1][2]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime31(attemptTime[2][0]);
        gameScore.setTrialtime32(attemptTime[2][1]);
        gameScore.setTrialtime33(attemptTime[2][2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime41(attemptTime[3][0]);
        gameScore.setTrialtime42(attemptTime[3][1]);
        gameScore.setTrialtime43(attemptTime[3][2]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime51(attemptTime[4][0]);
        gameScore.setTrialtime52(attemptTime[4][1]);
        gameScore.setTrialtime53(attemptTime[4][2]);

        gameScoreDAO.insert(gameScore);
        database.close();
    }

    public void postScoreObj(String username, String dateTime, String location, String menu,
                             String difficulty, int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ObjGameScores")
                .build();
        ObjGameScoreDAO gameScoreDAO = database.getObjGameScoreDAO();

        ObjGameScore gameScore = new ObjGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setDifficulty(difficulty);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
        database.close();
    }

    public void postScoreSp(String username, String dateTime, String location, String menu,
                     String difficulty, int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-SpGameScores")
                .build();
        SpGameScoreDAO gameScoreDAO = database.getSpGameScoreDAO();

        SpGameScore gameScore = new SpGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setDifficulty(difficulty);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
        database.close();
    }

    public void postScoreNtF(String username, String dateTime, String location, String menu,
                      int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-NtFGameScores")
                .build();
        NtFGameScoreDAO gameScoreDAO = database.getNtFGameScoreDAO();

        NtFGameScore gameScore = new NtFGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
        database.close();
    }

    public void postScoreFtN(String username, String dateTime, String location, String menu,
                      int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-FtNGameScores")
                .build();
        FtNGameScoreDAO gameScoreDAO = database.getFtNGameScoreDAO();

        FtNGameScore gameScore = new FtNGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
        database.close();
    }

    public void postScoreRe(String username, String dateTime, String location, String menu,
                            String mode, int score, int[] trialSuccess, double[] trialTime) {

        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ReGameScores")
                .build();
        ReGameScoreDAO gameScoreDAO = database.getReGameScoreDAO();

        ReGameScore gameScore = new ReGameScore();
        gameScore.setUsername(username);
        gameScore.setTime(dateTime);
        gameScore.setLocation(location);
        gameScore.setMenu(menu);
        gameScore.setMode(mode);
        gameScore.setScore(String.valueOf(score));
        gameScore.setTrial1(trialSuccess[0]);
        gameScore.setTrialtime1(trialTime[0]);
        gameScore.setTrial2(trialSuccess[1]);
        gameScore.setTrialtime2(trialTime[1]);
        gameScore.setTrial3(trialSuccess[2]);
        gameScore.setTrialtime3(trialTime[2]);
        gameScore.setTrial4(trialSuccess[3]);
        gameScore.setTrialtime4(trialTime[3]);
        gameScore.setTrial5(trialSuccess[4]);
        gameScore.setTrialtime5(trialTime[4]);

        gameScoreDAO.insert(gameScore);
        database.close();
    }

    public void postOnline(String username) {
        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-BlockGameScores")
                .build();
        BlockGameScoreDAO blockGameScoreDAO = database.getBlockGameScoreDAO();
        List<BlockGameScore> blockGameScoreList = blockGameScoreDAO.getUserBlockGameScores(username);
        for (BlockGameScore s: blockGameScoreList) {
            postScoreBlockOnline(s, blockGameScoreDAO);
        }
        database.close();

        database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ObjGameScores")
                .build();
        ObjGameScoreDAO objGameScoreDAO = database.getObjGameScoreDAO();
        List<ObjGameScore> objGameScoreList = objGameScoreDAO.getUserObjGameScores(username);
        for (ObjGameScore s: objGameScoreList) {
            postScoreObjOnline(s, objGameScoreDAO);
        }
        database.close();

        database = Room.databaseBuilder(context, LucidityDatabase.class, "db-SpGameScores")
                .build();
        SpGameScoreDAO spGameScoreDAO = database.getSpGameScoreDAO();
        List<SpGameScore> spGameScoreList = spGameScoreDAO.getUserSpGameScores(username);
        for (SpGameScore s: spGameScoreList) {
            postScoreSpOnline(s, spGameScoreDAO);
        }
        database.close();

        database = Room.databaseBuilder(context, LucidityDatabase.class, "db-FtNGameScores")
                .build();
        FtNGameScoreDAO ftnGameScoreDAO = database.getFtNGameScoreDAO();
        List<FtNGameScore> ftnGameScoreList = ftnGameScoreDAO.getUserFtNGameScores(username);
        for (FtNGameScore s: ftnGameScoreList) {
            postScoreFtNOnline(s, ftnGameScoreDAO);
        }
        database.close();

        database = Room.databaseBuilder(context, LucidityDatabase.class, "db-NtFGameScores")
                .build();
        NtFGameScoreDAO ntfGameScoreDAO = database.getNtFGameScoreDAO();
        List<NtFGameScore> ntfGameScoreList = ntfGameScoreDAO.getUserNtFGameScores(username);
        for (NtFGameScore s: ntfGameScoreList) {
            postScoreNtFOnline(s, ntfGameScoreDAO);
        }
        database.close();

        database = Room.databaseBuilder(context, LucidityDatabase.class, "db-ReGameScores")
                .build();
        ReGameScoreDAO reGameScoreDAO = database.getReGameScoreDAO();
        List<ReGameScore> reGameScoreList = reGameScoreDAO.getUserReGameScores(username);
        for (ReGameScore s: reGameScoreList) {
            postScoreReOnline(s, reGameScoreDAO);
        }
        database.close();
    }

    public void postSuiteOnline(String username) {
        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-FullTestRuns")
                .build();
        FullTestRunDAO fullTestRunDAO = database.getFullTestRunDAO();

        List<FullTestRun> fullTestRuns = fullTestRunDAO.getUserTestSuiteRuns(username);
        for (FullTestRun f: fullTestRuns) {
            if(!f.getTesttime1().isEmpty()) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", f.getUsername()));
                params.add(new BasicNameValuePair("startTime", f.getTime()));
                params.add(new BasicNameValuePair("menu", f.getMenu()));
                params.add(new BasicNameValuePair("picture", f.getPicture()));
                params.add(new BasicNameValuePair("testtype1", f.getTesttype1()));
                params.add(new BasicNameValuePair("testtime1", f.getTesttime1()));
                params.add(new BasicNameValuePair("testtype2", f.getTesttype2()));
                params.add(new BasicNameValuePair("testtime2", f.getTesttime2()));
                params.add(new BasicNameValuePair("testtype3", f.getTesttype3()));
                params.add(new BasicNameValuePair("testtime3", f.getTesttime3()));
                params.add(new BasicNameValuePair("testtype4", f.getTesttype4()));
                params.add(new BasicNameValuePair("testtime4", f.getTesttime4()));

                // getting JSON Object
                JSONObject json = jsonParser.makeHttpRequest(url_full_test,
                        "POST", params);

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    String msg = json.getString(TAG_MESSAGE);

                    if (success == 1) {
                        fullTestRunDAO.delete(f);
                    } else {
                        Log.d("Check Test Suite Added", msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            } else {
                fullTestRunDAO.delete(f);
            }
        }
        database.close();
    }

    public void updateTestRun(String username, int counter, String dateTime) {
        LucidityDatabase database = Room.databaseBuilder(context, LucidityDatabase.class, "db-FullTestRuns")
                .build();
        FullTestRunDAO fullTestRunDAO = database.getFullTestRunDAO();

        switch (counter){
            case 0:
                fullTestRunDAO.updateTime1(dateTime, username, testSuiteStartTime);
                break;
            case 1:
                fullTestRunDAO.updateTime2(dateTime, username, testSuiteStartTime);
                break;
            case 2:
                fullTestRunDAO.updateTime3(dateTime, username, testSuiteStartTime);
                break;
            case 3:
                fullTestRunDAO.updateTime4(dateTime, username, testSuiteStartTime);
                break;
            case 4:
                fullTestRunDAO.updateTime5(dateTime, username, testSuiteStartTime);
                break;
        }
    }

    private void postScoreBlockOnline(BlockGameScore s, BlockGameScoreDAO gameScoreDAO) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", s.getUsername()));
        params.add(new BasicNameValuePair("time", s.getTime()));
        params.add(new BasicNameValuePair("location", s.getLocation()));
        params.add(new BasicNameValuePair("menu", s.getMenu()));
        params.add(new BasicNameValuePair("difficulty", s.getDifficulty()));
        params.add(new BasicNameValuePair("score", s.getScore()));
        params.add(new BasicNameValuePair("trial1", String.valueOf(s.getTrial1())));
        params.add(new BasicNameValuePair("trial1-1time", String.valueOf(s.getTrialtime11())));
        params.add(new BasicNameValuePair("trial1-2time", String.valueOf(s.getTrialtime12())));
        params.add(new BasicNameValuePair("trial1-3time", String.valueOf(s.getTrialtime13())));
        params.add(new BasicNameValuePair("trial2", String.valueOf(s.getTrial2())));
        params.add(new BasicNameValuePair("trial2-1time", String.valueOf(s.getTrialtime21())));
        params.add(new BasicNameValuePair("trial2-2time", String.valueOf(s.getTrialtime22())));
        params.add(new BasicNameValuePair("trial2-3time", String.valueOf(s.getTrialtime23())));
        params.add(new BasicNameValuePair("trial3", String.valueOf(s.getTrial3())));
        params.add(new BasicNameValuePair("trial3-1time", String.valueOf(s.getTrialtime31())));
        params.add(new BasicNameValuePair("trial3-2time", String.valueOf(s.getTrialtime32())));
        params.add(new BasicNameValuePair("trial3-3time", String.valueOf(s.getTrialtime33())));
        params.add(new BasicNameValuePair("trial4", String.valueOf(s.getTrial4())));
        params.add(new BasicNameValuePair("trial4-1time", String.valueOf(s.getTrialtime41())));
        params.add(new BasicNameValuePair("trial4-2time", String.valueOf(s.getTrialtime42())));
        params.add(new BasicNameValuePair("trial4-3time", String.valueOf(s.getTrialtime43())));
        params.add(new BasicNameValuePair("trial5", String.valueOf(s.getTrial5())));
        params.add(new BasicNameValuePair("trial5-1time", String.valueOf(s.getTrialtime51())));
        params.add(new BasicNameValuePair("trial5-2time", String.valueOf(s.getTrialtime52())));
        params.add(new BasicNameValuePair("trial5-3time", String.valueOf(s.getTrialtime53())));

        // getting JSON Object
        JSONObject json = jsonParser.makeHttpRequest(url_block_game,
                "POST", params);

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            String msg = json.getString(TAG_MESSAGE);

            if (success == 1) {
                gameScoreDAO.delete(s);
            } else {
                Log.d("Check Score Added", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void postScoreObjOnline(ObjGameScore s, ObjGameScoreDAO gameScoreDAO) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", s.getUsername()));
        params.add(new BasicNameValuePair("time", s.getTime()));
        params.add(new BasicNameValuePair("location", s.getLocation()));
        params.add(new BasicNameValuePair("menu", s.getMenu()));
        params.add(new BasicNameValuePair("difficulty", s.getDifficulty()));
        params.add(new BasicNameValuePair("score", s.getScore()));
        params.add(new BasicNameValuePair("trial1", String.valueOf(s.getTrial1())));
        params.add(new BasicNameValuePair("trial1time", String.valueOf(s.getTrialtime1())));
        params.add(new BasicNameValuePair("trial2", String.valueOf(s.getTrial2())));
        params.add(new BasicNameValuePair("trial2time", String.valueOf(s.getTrialtime2())));
        params.add(new BasicNameValuePair("trial3", String.valueOf(s.getTrial3())));
        params.add(new BasicNameValuePair("trial3time", String.valueOf(s.getTrialtime3())));
        params.add(new BasicNameValuePair("trial4", String.valueOf(s.getTrial4())));
        params.add(new BasicNameValuePair("trial4time", String.valueOf(s.getTrialtime4())));
        params.add(new BasicNameValuePair("trial5", String.valueOf(s.getTrial5())));
        params.add(new BasicNameValuePair("trial5time", String.valueOf(s.getTrialtime5())));

        // getting JSON Object
        JSONObject json = jsonParser.makeHttpRequest(url_obj_game,
                "POST", params);

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            String msg = json.getString(TAG_MESSAGE);

            if (success == 1) {
                gameScoreDAO.delete(s);
            } else {
                Log.d("Check Score Added", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void postScoreSpOnline(SpGameScore s, SpGameScoreDAO gameScoreDAO) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", s.getUsername()));
        params.add(new BasicNameValuePair("time", s.getTime()));
        params.add(new BasicNameValuePair("location", s.getLocation()));
        params.add(new BasicNameValuePair("menu", s.getMenu()));
        params.add(new BasicNameValuePair("difficulty", s.getDifficulty()));
        params.add(new BasicNameValuePair("score", s.getScore()));
        params.add(new BasicNameValuePair("trial1", String.valueOf(s.getTrial1())));
        params.add(new BasicNameValuePair("trial1time", String.valueOf(s.getTrialtime1())));
        params.add(new BasicNameValuePair("trial2", String.valueOf(s.getTrial2())));
        params.add(new BasicNameValuePair("trial2time", String.valueOf(s.getTrialtime2())));
        params.add(new BasicNameValuePair("trial3", String.valueOf(s.getTrial3())));
        params.add(new BasicNameValuePair("trial3time", String.valueOf(s.getTrialtime3())));
        params.add(new BasicNameValuePair("trial4", String.valueOf(s.getTrial4())));
        params.add(new BasicNameValuePair("trial4time", String.valueOf(s.getTrialtime4())));
        params.add(new BasicNameValuePair("trial5", String.valueOf(s.getTrial5())));
        params.add(new BasicNameValuePair("trial5time", String.valueOf(s.getTrialtime5())));

        // getting JSON Object
        JSONObject json = jsonParser.makeHttpRequest(url_sp_game,
                "POST", params);

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            String msg = json.getString(TAG_MESSAGE);

            if (success == 1) {
                gameScoreDAO.delete(s);
            } else {
                Log.d("Check Score Added", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void postScoreFtNOnline(FtNGameScore s, FtNGameScoreDAO gameScoreDAO) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", s.getUsername()));
        params.add(new BasicNameValuePair("time", s.getTime()));
        params.add(new BasicNameValuePair("location", s.getLocation()));
        params.add(new BasicNameValuePair("menu", s.getMenu()));
        params.add(new BasicNameValuePair("score", s.getScore()));
        params.add(new BasicNameValuePair("trial1", String.valueOf(s.getTrial1())));
        params.add(new BasicNameValuePair("trial1time", String.valueOf(s.getTrialtime1())));
        params.add(new BasicNameValuePair("trial2", String.valueOf(s.getTrial2())));
        params.add(new BasicNameValuePair("trial2time", String.valueOf(s.getTrialtime2())));
        params.add(new BasicNameValuePair("trial3", String.valueOf(s.getTrial3())));
        params.add(new BasicNameValuePair("trial3time", String.valueOf(s.getTrialtime3())));
        params.add(new BasicNameValuePair("trial4", String.valueOf(s.getTrial4())));
        params.add(new BasicNameValuePair("trial4time", String.valueOf(s.getTrialtime4())));
        params.add(new BasicNameValuePair("trial5", String.valueOf(s.getTrial5())));
        params.add(new BasicNameValuePair("trial5time", String.valueOf(s.getTrialtime5())));

        // getting JSON Object
        JSONObject json = jsonParser.makeHttpRequest(url_ftn_game,
                "POST", params);

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            String msg = json.getString(TAG_MESSAGE);

            if (success == 1) {
                gameScoreDAO.delete(s);
            } else {
                Log.d("Check Score Added", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void postScoreNtFOnline(NtFGameScore s, NtFGameScoreDAO gameScoreDAO) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", s.getUsername()));
        params.add(new BasicNameValuePair("time", s.getTime()));
        params.add(new BasicNameValuePair("location", s.getLocation()));
        params.add(new BasicNameValuePair("menu", s.getMenu()));
        params.add(new BasicNameValuePair("score", s.getScore()));
        params.add(new BasicNameValuePair("trial1", String.valueOf(s.getTrial1())));
        params.add(new BasicNameValuePair("trial1time", String.valueOf(s.getTrialtime1())));
        params.add(new BasicNameValuePair("trial2", String.valueOf(s.getTrial2())));
        params.add(new BasicNameValuePair("trial2time", String.valueOf(s.getTrialtime2())));
        params.add(new BasicNameValuePair("trial3", String.valueOf(s.getTrial3())));
        params.add(new BasicNameValuePair("trial3time", String.valueOf(s.getTrialtime3())));
        params.add(new BasicNameValuePair("trial4", String.valueOf(s.getTrial4())));
        params.add(new BasicNameValuePair("trial4time", String.valueOf(s.getTrialtime4())));
        params.add(new BasicNameValuePair("trial5", String.valueOf(s.getTrial5())));
        params.add(new BasicNameValuePair("trial5time", String.valueOf(s.getTrialtime5())));

        // getting JSON Object
        JSONObject json = jsonParser.makeHttpRequest(url_ntf_game,
                "POST", params);

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            String msg = json.getString(TAG_MESSAGE);

            if (success == 1) {
                gameScoreDAO.delete(s);
            } else {
                Log.d("Check Score Added", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void postScoreReOnline(ReGameScore s, ReGameScoreDAO gameScoreDAO) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", s.getUsername()));
        params.add(new BasicNameValuePair("time", s.getTime()));
        params.add(new BasicNameValuePair("location", s.getLocation()));
        params.add(new BasicNameValuePair("menu", s.getMenu()));
        params.add(new BasicNameValuePair("mode", s.getMode()));
        params.add(new BasicNameValuePair("score", s.getScore()));
        params.add(new BasicNameValuePair("trial1", String.valueOf(s.getTrial1())));
        params.add(new BasicNameValuePair("trial1time", String.valueOf(s.getTrialtime1())));
        params.add(new BasicNameValuePair("trial2", String.valueOf(s.getTrial2())));
        params.add(new BasicNameValuePair("trial2time", String.valueOf(s.getTrialtime2())));
        params.add(new BasicNameValuePair("trial3", String.valueOf(s.getTrial3())));
        params.add(new BasicNameValuePair("trial3time", String.valueOf(s.getTrialtime3())));
        params.add(new BasicNameValuePair("trial4", String.valueOf(s.getTrial4())));
        params.add(new BasicNameValuePair("trial4time", String.valueOf(s.getTrialtime4())));
        params.add(new BasicNameValuePair("trial5", String.valueOf(s.getTrial5())));
        params.add(new BasicNameValuePair("trial5time", String.valueOf(s.getTrialtime5())));

        // getting JSON Object
        JSONObject json = jsonParser.makeHttpRequest(url_re_game,
                "POST", params);

        // check for success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            String msg = json.getString(TAG_MESSAGE);

            if (success == 1) {
                gameScoreDAO.delete(s);
            } else {
                Log.d("Check Score Added", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public String getTestSuiteStartTime() {
        return testSuiteStartTime;
    }
}
