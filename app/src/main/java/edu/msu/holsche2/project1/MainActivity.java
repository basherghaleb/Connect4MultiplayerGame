package edu.msu.holsche2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import edu.msu.holsche2.project1.Cloud.Cloud;
import edu.msu.holsche2.project1.Cloud.Models.LeaveResult;
import edu.msu.holsche2.project1.Cloud.Models.MatchResult;
import edu.msu.holsche2.project1.Cloud.Models.StatusResult;

public class MainActivity extends AppCompatActivity {

    // Class variables and constants

    private static final int pollingInterval = 1000;

    private boolean polling = true;

    /**
     * The text input for username name
     */
    private EditText inputUsername;

    /**
     * The text input for passwrod
     */
    private EditText inputPassword;

    /**
     * The key for player1's name in the bundle
     */
    private static final String HOST = "host";

    /**
     * The key for player2's name in the bundle
     */
    private static final String GUEST = "guest";

    /**
     * user login
     */
    private String userLogin = "";

    /**
     * user password
     */
    private String userPassword = "";

    /**
     * preference key for login
     */
    private static final String LOGIN = "login";

    /**
     * preference key for password
     */
    private static final String PASSWORD = "password";

    /**
     * User preferences for saving the login if they choose to.
     */
    private SharedPreferences settings = null;

    /**
     * Waiting dialog used while waiting for a second player to join.
     */
    WaitingDlg waitDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign UI components to their respective variables
        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);

        // load user's preferences if they exist
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        userLogin = settings.getString(LOGIN, "");
        userPassword = settings.getString(PASSWORD, "");

        // if the users preferences are loaded, set the UI
        if (!userLogin.equals("") && !userPassword.equals("")) {
            // set the login inputs
            inputUsername.setText(userLogin);
            inputPassword.setText(userPassword);
            final CheckBox checkBox = (CheckBox)findViewById(R.id.checkRemember);
            checkBox.setChecked(true);
        }

        Button loginButton = findViewById(R.id.Login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOnClick(v);
            }
        });
    }

    @SuppressWarnings("unused")
    public void onStartConnectFour(View view, boolean isHost, String userid, String roomid, String nameHost, String nameGuest) {

        Intent intent = new Intent(this, ConnectFourActivity.class);
        intent.putExtra(HOST, nameHost);
        intent.putExtra(GUEST, nameGuest);
        intent.putExtra(ConnectFourActivity.PLAYER_HOST, isHost);
        intent.putExtra(ConnectFourActivity.ROOM_ID, roomid);
        intent.putExtra(ConnectFourActivity.USER_ID, userid);

//        WaitingDlg waitDlg = new WaitingDlg();
//        waitDlg.show(getSupportFragmentManager(), "waiting");

        startActivity(intent);
        finish();
    }

    /**
     * Called when the signup button is clicked
     *
     * @param view Associated view object
     */
    public void onStartSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Create the options menu, for users to see game instructions
     *
     * @param menu The menu object to create the options for
     * @return True after the menu is successfully created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Responds to the user selecting the menu option
     *
     * @param item The menu option tapped by the user
     * @return True if a valid option was selected, else
     * the return value of the parent class function
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_howto) {
            // Create the how-to-play dialog
            HowDlg dlg = new HowDlg();
            dlg.show(getSupportFragmentManager(), "About");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadUser(final String user, final String password) {
        new Thread(() -> {
            Cloud cloud = new Cloud();
            final int id = cloud.loadUserFromCloud(user, password);

            runOnUiThread(() -> {
                if (id==-1) {
                    Toast.makeText(getApplicationContext(),
                            R.string.loading_user_fail,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.loading_user_success,
                            Toast.LENGTH_SHORT).show();
                    waitDlg = new WaitingDlg();
                    waitDlg.show(getSupportFragmentManager(), "waiting");

                    // Add the pollForMatch(userLogin) call here
                    pollForMatch(String.valueOf(id), user);
                }
            });
        }).start();
    }

    public void loginOnClick(View view) {
        String userLogin = inputUsername.getText().toString().trim();
        String userPassword = inputPassword.getText().toString().trim();

        if (userLogin.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, R.string.empty_field, Toast.LENGTH_SHORT).show();
            return;
        }

        // check if user wants to save their login to preferences
        SharedPreferences.Editor editor = settings.edit();
        CheckBox remember = findViewById(R.id.checkRemember);
        if (remember.isChecked()) {
            // save the login to preferences
            editor.putString(LOGIN, userLogin);
            editor.putString(PASSWORD, userPassword);
            editor.apply();
        } else {
            // remove the login from preferences
            editor.putString(LOGIN, "");
            editor.putString(PASSWORD, "");
            editor.apply();
        }

        loadUser(userLogin, userPassword);
    }
    private void pollForMatch(final String userid, final String username) {
        new Thread(() -> {
            Cloud cloud = new Cloud();
            MatchResult joinResult = cloud.joinGame(userid);
            Log.i("pollForMatch", joinResult.toString());
            if (joinResult == null || !"yes".equals(joinResult.getStatus())) {
                Log.i("pollForMatch", "Join failure");
                return;
            }
            String roomId = joinResult.getRoomid();
            boolean isHost = joinResult.getHost();

            while (!waitDlg.isDismissed()) {
                Log.i("pollForMatch", "test");
                StatusResult statusResult = cloud.getGameStatus(roomId, userid);
                Log.i("pollForMatch", roomId);
//                Log.i("pollForMatch", statusResult != null ? statusResult.getRoomStatus() : "NULL RESULT?");
                if (statusResult != null) {
                    if ("playing".equals(statusResult.getRoomStatus())) {
                        // Match found, start the game
                        String hostName = cloud.getHostName(roomId, String.valueOf(true)).getName();
                        String guestName = cloud.getGuestName(roomId, String.valueOf(false)).getName();

                        runOnUiThread(() -> {
                            // close waiting dialog
                            waitDlg.dismiss();

                            // Set names based on host/guest status
                            if(isHost) {
                                onStartConnectFour(null, isHost, userid, roomId, hostName, guestName);
                            } else {
                                onStartConnectFour(null, isHost, userid, roomId, hostName, guestName);
                            }
                        });
                        return;
                    } else if ("ready".equals(statusResult.getRoomStatus())){
                        // Host left, need to leave game
                        waitDlg.dismiss();
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(),
                                    R.string.host_left_room,
                                    Toast.LENGTH_SHORT).show();
                        });

                    }
                }
                // Wait for the polling interval
                try {
                    Thread.sleep(pollingInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("pollForMatch", "User cancelled join");
            //Send this to server!!
            LeaveResult leaveResult = cloud.leaveGame(roomId, userid);
            if (leaveResult != null && leaveResult.getStatus().equals("yes")){
                //successfully left room
            } else {
                Log.e("pollForMatch", leaveResult == null ? "Null Response" : leaveResult.getMessage());
            }

        }).start();
    }
}