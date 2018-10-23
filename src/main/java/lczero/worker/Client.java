package lczero.worker;

import lczero.worker.model.Game;
import lczero.worker.model.GameSubmission;
import lczero.worker.model.TestConfig;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    @Value("${server}")
    String server;


    void test() throws IOException {

        // Test out connectivity.

        // Get test config
        TestConfig testConfig = getTestConfig();
        if(testConfig != null){

            // Get a Game
            Game newGame = getNewGame(testConfig.testID);


            if(newGame != null ){

                // TODO: simulate a game.

                // Create submission
                GameSubmission submission = new GameSubmission();
                submission.testID = testConfig.testID;
                submission.openingPGN = "Opening PNG";
                submission.PGN = "Final PNG";

                submitGame(submission);

            }

        }

        log.info("Done");


    }

    TestConfig getTestConfig() throws IOException {

        URL obj = new URL(server + "/getTestConfig");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("content-type", "text-plain");
        con.setDoOutput(false);

        try{

            int x = con.getResponseCode();
            if(x!=200)
                log.warn("Got Response: " + x);

        }catch(java.net.ConnectException e ){
            log.warn("Can not connect to server to get config.");
            return null;
        }

        InputStream inStream =  con.getInputStream();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(inStream));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        String data = response.toString();

        JSONObject jsonObject = new JSONObject(data);
        TestConfig testConfig = new TestConfig();
        testConfig.lc0url = jsonObject.getString("lc0url");
        testConfig.parameters = jsonObject.getString("parameters");
        testConfig.tcControl = jsonObject.getString("tcControl");
        testConfig.testID = jsonObject.getLong("testID");

        return testConfig;

    }

    Game getNewGame(long testID) throws IOException  {

        URL obj = new URL(server + "/newGame?testID=" + testID);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("content-type", "text-plain");
        con.setDoOutput(false);

        try{

            int x = con.getResponseCode();
            if(x!=200)
                log.warn("Got Response: " + x);

        }catch(java.net.ConnectException e ){
            log.warn("Can not connect to server to get config.");
            return null;
        }

        InputStream inStream =  con.getInputStream();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(inStream));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        String data = response.toString();
        JSONObject jsonObject = new JSONObject(data);
        Game g = new Game();
        g.expirationDate = jsonObject.getLong("expirationDate");
        g.openingPGN = jsonObject.getString("openingPGN");
        g.testID = jsonObject.getLong("testID");

        return g;

    }

    void submitGame(GameSubmission submission)  throws IOException  {

        URL obj = new URL(server + "/submitGame");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("content-type", "application/json; charset=utf8");
        con.setDoInput(true);
        con.setDoOutput(true);
        OutputStream output = con.getOutputStream();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("testID", submission.testID);
        jsonObject.put("openingPGN", submission.openingPGN);
        jsonObject.put("PGN", submission.PGN);

        output.write(jsonObject.toString().getBytes());
        output.close();

        log.info("Got response code from server: " + con.getResponseCode());
    }

}
