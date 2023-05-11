package edu.msu.holsche2.project1.Cloud;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import edu.msu.holsche2.project1.Cloud.Models.LeaveResult;
import edu.msu.holsche2.project1.Cloud.Models.LoginResult;
import edu.msu.holsche2.project1.Cloud.Models.MatchResult;
import edu.msu.holsche2.project1.Cloud.Models.NameResult;
import edu.msu.holsche2.project1.Cloud.Models.SaveResult;
import edu.msu.holsche2.project1.Cloud.Models.StateResult;
import edu.msu.holsche2.project1.Cloud.Models.StatusResult;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class Cloud {
    private static final String BASE_URL = "https://webdev.cse.msu.edu/~ghalebba/cse476/project2/";
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();


    public boolean saveUserToCloud(String user, String pw) {
        // Create an XML packet with the user and password information
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startDocument(null, null);

            xml.startTag("", "connect4");

            xml.startTag("", "user");
            xml.text(user);
            xml.endTag("", "user");

            xml.startTag("", "pw");
            xml.text(pw);
            xml.endTag("", "pw");

            xml.endTag("", "connect4");

            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return false;
        }

        String xmlData = writer.toString();
        Connect4Service service = retrofit.create(Connect4Service.class);
        try {
            Response<SaveResult> response = service.saveUserSignUp(xmlData).execute();
            if (response.isSuccessful()) {
                SaveResult result = response.body();
                if (result.getStatus() != null && result.getStatus().equals("yes")) {
                    return true;
                }
                Log.e("SaveUserSignUp", "Failed to save, message = '" + result.getMessage() + "'");
                return false;
            }
            Log.e("SaveUserSignUp", "Failed to save, message = '" + response.code() + "'");
            return false;
        } catch (IOException e) {
            Log.e("SaveUserSignUp", "Exception occurred while trying to save user!", e);
            return false;
        } catch (RuntimeException e) {    // to catch xml errors to help debug step 6
            Log.e("SaveUserSignUp", "Runtime Exception: " + e.getMessage());
            return false;
        }

    }

    public int loadUserFromCloud(String user, String pw) {
        // Create an XML packet with the user and password information
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startDocument(null, null);

            xml.startTag("", "connect4");

            xml.startTag("", "user");
            xml.text(user);
            xml.endTag("", "user");

            xml.startTag("", "pw");
            xml.text(pw);
            xml.endTag("", "pw");

            xml.endTag("", "connect4");

            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return -1;
        }

        String xmlData = writer.toString();

        Connect4Service service = retrofit.create(Connect4Service.class);
        try {
            Response<LoginResult> response = service.loginResult(xmlData).execute();
            Log.i("LoadUserFromCloud", response.toString());
            if (response.isSuccessful()) {
                LoginResult result = response.body();
                if (result.getStatus() != null && result.getStatus().equals("yes")) {
                    return result.getUserId();
                }
                Log.e("LoadUserFromCloud", "Failed to load, message = '" + result.getMessage() + "'");
                return -1;
            }
            Log.e("LoadUserFromCloud", "Failed to load, message = '" + response.code() + "'");
            return -1;
        } catch (IOException e) {
            Log.e("LoadUserFromCloud", "Exception occurred while trying to load user!", e);
            return -1;
        } catch (RuntimeException e) {    // to catch xml errors to help debug step 6
            Log.e("LoadUserFromCloud", "Runtime Exception: " + e.getMessage());
            return -1;
        }
    }

    // function to save game state to the cloud

    /**
     * Save the state of the game to the server
     * @param state The encoded game state
     * @return True if the request was successful, else false.
     */
    public boolean saveGameState(String roomid, String userid, String state, String status) {
        // create a retrofit object to handle the requests
        Connect4Service service = retrofit.create(Connect4Service.class);

        // send the save game state post request with the necessary data
        try {
            Response<SaveResult> response = service.saveGameState(roomid, userid, state, status).execute();
            if (response.isSuccessful()) {
                SaveResult result = response.body();
                if (result.getStatus() != null && result.getStatus().equals("yes")) {
                    return true;
                }
                Log.e("SaveGameState", "Failed to save, message = '" + result.getMessage() + "'");
                return false;
            }
            Log.e("SaveGameState", "Failed to save, message = '" + response.code() + "'");
            return false;
        } catch (IOException e) {
            Log.e("SaveGameState", "Exception occurred while trying to save user!", e);
            return false;
        } catch (RuntimeException e) {    // to catch xml errors to help debug step 6
            Log.e("SaveGameState", "Runtime Exception: " + e.getMessage());
            return false;
        }

    }

    public MatchResult joinGame(String userid) {
        Connect4Service service = retrofit.create(Connect4Service.class);
        Call<MatchResult> call = service.joinGame(userid);
        try {
            Response<MatchResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("joinGame", "Failed to join game: " + response.code() + " " + response.message());
                return null;
            }
        } catch (IOException e) {
            Log.e("joinGame", "Failed to join game", e);
            return null;
        }
    }

    public LeaveResult leaveGame(String roomid, String userid){
        Connect4Service service = retrofit.create(Connect4Service.class);
        Call<LeaveResult> call = service.leaveGame(roomid, userid);
        try {
            Response<LeaveResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("leaveGame", "Failed to join game: " + response.code() + " " + response.message());
                return null;
            }
        } catch (IOException e) {
            Log.e("leaveGame", "Failed to join game", e);
            return null;
        }

    }

    public StatusResult getGameStatus(String roomid, String userid) {
        Connect4Service service = retrofit.create(Connect4Service.class);
        Call<StatusResult> call = service.getGameStatus(roomid, userid);
        try {
            Response<StatusResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("getGameStatus", "Failed to get game status: " + response.code() + " " + response.message());
                return null;
            }
        } catch (IOException e) {
            Log.e("getGameStatus", "Failed to get game status", e);
            return null;
        }
    }

    public boolean checkForMatchInCloud(String userId) {
        MatchResult result = joinGame(userId);
        if (result != null && "yes".equals(result.getStatus())) {
            return true;
        }
        Log.e("CheckForMatchInCloud", "Failed to check match, message = '" + (result != null ? result.getMessage() : "null") + "'");
        return false;
    }

    public NameResult getGuestName(String roomid, String isHost) {
        Connect4Service service = retrofit.create(Connect4Service.class);
        Call<NameResult> call = service.getUsername(roomid, isHost);
        try {
            Response<NameResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("getGameStatus", "Failed to get game status: " + response.code() + " " + response.message());
                return null;
            }
        } catch (IOException e) {
            Log.e("getGameStatus", "Failed to get game status", e);
            return null;
        }
    }

    public NameResult getHostName(String roomid, String isHost) {
        Connect4Service service = retrofit.create(Connect4Service.class);
        Call<NameResult> call = service.getUsername(roomid, isHost);
        try {
            Response<NameResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("getGameStatus", "Failed to get host name: " + response.code() + " " + response.message());
                return null;
            }
        } catch (IOException e) {
            Log.e("getGameStatus", "Failed to get host name", e);
            return null;
        }
    }

    public StateResult getGameState(String roomid, String userid){
        Connect4Service service = retrofit.create(Connect4Service.class);
        Call<StateResult> call = service.getGameState(roomid, userid);
        try {
            Response<StateResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Log.e("getGameState", "Failed to get game status: " + response.code() + " " + response.message());
                return null;
            }
        } catch (IOException e) {
            Log.e("getGameState", "Failed to get game status", e);
            return null;
        }


    }

}
