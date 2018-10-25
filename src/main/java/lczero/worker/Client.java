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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    @Value("${server}")
    String server;

    String cwd; // Current working directory


    void test() throws IOException {

        // Test out connectivity.

        // Get test config
        TestConfig testConfig = getTestConfig();
        if(testConfig != null){

            // Setup the test binary
            setupTestBinary(testConfig);

            setupCutechess(testConfig);


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

    private void setupCutechess(TestConfig testConfig) throws IOException {

        if(!Files.exists(Paths.get(cwd).resolve("cutechess.exe"))) {
            DownloadUtility.downloadFile(testConfig.baseUrlForTools + "cutechess.exe", cwd);
        }

        if(!Files.exists(Paths.get(cwd).resolve("qt5core.dll"))) {
            DownloadUtility.downloadFile(testConfig.baseUrlForTools + "qt5core.dll", cwd);
        }


    }

    private boolean setupTestBinary(TestConfig testConfig) throws IOException {

        String savedFile = DownloadUtility.downloadFile(testConfig.lc0url, cwd);

        if(savedFile.equals("")) return false;

        // Unzip the downloaded file.
        Path saved = Paths.get(savedFile);
        Path cwdPath = Paths.get(cwd);
        String savedFilename = saved.getFileName().toString();

        String targetDirectory = cwdPath.resolve( savedFilename.substring(0, savedFilename.length() - 4 ) ).toString();
        Files.createDirectory(Paths.get(targetDirectory));


        DownloadUtility.unzipFile(savedFile, targetDirectory);

        return true;

    }

    private

    TestConfig getTestConfig() throws IOException {

        JSONObject jsonObject = httpGet(server + "/getTestConfig");

        if (jsonObject == null) return null;
        TestConfig testConfig = new TestConfig();
        testConfig.lc0url = jsonObject.getString("lc0url");
        testConfig.parameters = jsonObject.getString("parameters");
        testConfig.tcControl = jsonObject.getString("tcControl");
        testConfig.testID = jsonObject.getLong("testID");
        testConfig.baseUrlForTools = jsonObject.getString("baseUrlForTools");

        return testConfig;

    }

    Game getNewGame(long testID) throws IOException  {

        JSONObject jsonObject = httpGet(server + "/newGame?testID=" + testID);
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

    public void setCwd(String cwd) {
        this.cwd = cwd;
    }

    private static JSONObject httpGet(String url) throws IOException {
        URL obj = new URL(url);
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

        return new JSONObject(data);
    }
}
