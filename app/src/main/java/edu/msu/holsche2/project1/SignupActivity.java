package edu.msu.holsche2.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.msu.holsche2.project1.Cloud.Cloud;

public class SignupActivity extends AppCompatActivity {

    // Class variables and constants
    /**
     * The text input for the username
     */
    private EditText inputUsername;

    /**
     * The text input for the password
     */
    private EditText inputPassword;

    /**
     * The text input for the password
     */
    private EditText inputRepassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Assign the text edits to their class members
        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);
        inputRepassword = findViewById(R.id.repassword);

        // Assign onClickListener to the sign up button
        Button signUpButton = findViewById(R.id.submitUser);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpOnClick(v);
            }
        });
    }

    /**
     * Actually save the user signup information to the cloud
     * @param user username to save under
     * @param password password to save
     */
    private void saveUser(final String user, final String password) {

        /*
         * Create a thread to save the user signup information to the cloud
         */
        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean ok = cloud.saveUserToCloud(user, password);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if(!ok) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.saving_user_fail,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    R.string.saving_user_success,
                                    Toast.LENGTH_SHORT).show();
                            // Start MainActivity after a successful sign up
                            Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }

                    }

                });


            }

        }).start();

    }
    public void signUpOnClick(View view) {
        String user = inputUsername.getText().toString().trim();
        String pw = inputPassword.getText().toString().trim();
        String repw = inputRepassword.getText().toString().trim();

        // Check if fields are empty
        if(user.isEmpty() || pw.isEmpty() || repw.isEmpty()) {
            Toast.makeText(this, R.string.empty_field, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pw.equals(repw)) {
            Toast.makeText(this, R.string.wrong_field, Toast.LENGTH_SHORT).show();
            return;
        }

        saveUser(user, pw);
    }

}
