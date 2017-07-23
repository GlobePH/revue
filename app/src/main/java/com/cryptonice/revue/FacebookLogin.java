package com.cryptonice.revue;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class FacebookLogin extends AppCompatActivity {

    LoginButton login_button;
    CallbackManager callback_manager;
    String first_name, last_name, user_id;
    private static final String url = "jdbc:mysql://192.168.162.134:3306/revue";
    private static final String user = "admin";
    private static final String pass = "admin";
    Connection connection;

    public void saveToDatabase() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO user_info VALUES('" + user_id + "', '" + first_name + "', '" + last_name + "');");
            connection.close();
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, 1);
            finish();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.setApplicationId("275260519547756");
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);
        if (AccessToken.getCurrentAccessToken() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, 1);
            finish();
        }
        setContentView(R.layout.activity_facebook_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        login_button = (LoginButton) findViewById(R.id.btn_facebook_login);
        login_button.setReadPermissions("email");

        callback_manager = CallbackManager.Factory.create();
        login_button.registerCallback(callback_manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken new_token = loginResult.getAccessToken();
                user_id = new_token.getUserId();
                GraphRequest request = GraphRequest.newMeRequest(new_token,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    first_name = response.getJSONObject().getString("first_name");
                                    last_name = response.getJSONObject().getString("last_name");
                                    saveToDatabase();
                                } catch (Exception ex) {

                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(FacebookLogin.this, "Facebook login cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookLogin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        TextView txt_login_name = (TextView) findViewById(R.id.txt_login_name);
        Typeface font_coves_bold = Typeface.createFromAsset(getAssets(), "coves_bold.otf");
        txt_login_name.setTypeface(font_coves_bold);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callback_manager.onActivityResult(requestCode, resultCode, data);
    }
}
