package uk.kihira.tails.client;

import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CloudManager {
    private static final String BASE_URL = "https://api.tails.foxes.rocks";
    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";

    private final Session session;
    private final String serverId;

    private long lastAuthTime;

    public CloudManager(final Session session, final String serverId) {
        this.session = session;
        this.serverId = serverId;
    }

    public boolean submitData(final String key, final String data) {
        // todo submit data to remote server
        HttpURLConnection conn;

        if (key.equals("active")) {
            // todo this is their "default"/active setup
            //URL url = new URL(BASE_URL+"/player/"+session.getProfile().getId()+"/active");
        }

        try {
            URL url = new URL(BASE_URL+"/player/"+session.getProfile().getId()+"/library/"+key);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStreamWriter stream = new OutputStreamWriter(conn.getOutputStream())) {
                stream.write(data);
                stream.flush();
                stream.close();
            }

            if (conn.getResponseCode() != 200) {
                // todo handle error
                return false;
            }
            SubmitResponse response = Tails.gson.fromJson(IOUtils.toString(conn.getInputStream()), SubmitResponse.class);
            // todo store id

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Nullable
    public PartsData fetchPartsData(final String key) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(BASE_URL+"/player/"+session.getProfile().getId()+"/library/"+key);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            switch (conn.getResponseCode()) {
                case 200:
                    // todo add data to local cache
                    return Tails.gson.fromJson(IOUtils.toString(conn.getInputStream()), PartsData.class);
                case 404:
                    Tails.logger.error("Failed to load parts data for ID "); // todo
                    break;
                default:
                    Tails.logger.error("Unhandled response code in fetching parts data: "+conn.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(conn);
        }

        return null;
    }

    public ArrayList<String> multiFetchData(final String ... keys) {

        // todo
        return new ArrayList<>();
    }

    private boolean checkAndRefeshAuth() {
        if (lastAuthTime < System.currentTimeMillis() - 300000L) {
            return true;
        }

        HttpURLConnection conn = null;
        String data = "{\"accessToken\":\"" +session.getToken()+ "\", \"serverId\":\"" +serverId+ "\", \"selectedProfile\":\"" +session.getProfile().getId()+'"';

        try {
            URL url = new URL(JOIN_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content Type", "application/json");

            try (OutputStreamWriter stream = new OutputStreamWriter(conn.getOutputStream())) {
                stream.write(data);
                stream.flush();
                stream.close();
            }

            int res = conn.getResponseCode();
            if (res != 204) { // returns non 204 if error occurred
                try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                    // todo proper error handling
                    Tails.logger.error("Failed to authenticate user\n "+IOUtils.toString(reader));
                    reader.close();
                }
                return false;
            }

            lastAuthTime = System.currentTimeMillis();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(conn);
        }

        return false;
    }

    private class SubmitResponse {
        public final String objectUUID = "";
    }
}
