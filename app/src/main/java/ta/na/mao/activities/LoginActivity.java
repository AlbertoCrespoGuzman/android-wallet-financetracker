package ta.na.mao.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ta.na.mao.R;
import ta.na.mao.communications.Svc;
import ta.na.mao.communications.SvcApi;
import ta.na.mao.communications.http.auth.AndroiduserLogin;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.User;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.dialogs.ForgotPasswordDialog;
import ta.na.mao.utils.dialogs.ProgressCustomDialog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;



import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alberto on 2017/12/05.
 */

public class LoginActivity extends AppCompatActivity {

    Button loginButton,registerButton;
    EditText emailInput, passwordInput;
    DatabaseManager db;
    SvcApi svc, svcAth;
    ProgressCustomDialog dialog;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    Button loginButtonFacebookCustom;
    LoginButton loginButtonFacebookTrue;
    TextView forgotPassword;
    Bundle bFacebookData;
    String idfacebook, namefacebook;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_login);
            loginButtonFacebookTrue = findViewById(R.id.login_button_facebook);
        }else{
            setContentView(R.layout.activity_login_old);

        }

        db = new DatabaseManager(LoginActivity.this);
        loginButton = findViewById(R.id.activity_login_login_button);
        loginButtonFacebookCustom = findViewById(R.id.login_button);

        registerButton = findViewById(R.id.activity_login_register_button);
        emailInput = findViewById(R.id.activity_login_input_email);
        passwordInput = findViewById(R.id.activity_login_input_password);
        forgotPassword = findViewById(R.id.activity_login_forgot_password);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterInput()){
                    Utils.hideKeyboard(LoginActivity.this);
                    loginAndroiduser(createLoginForm());
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            loginButtonFacebookCustom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginButtonFacebookTrue.performClick();
                }
            });
            loginButtonFacebookTrue.setReadPermissions(Arrays.asList(EMAIL));
            loginButtonFacebookTrue.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            bFacebookData = getFacebookData(object);
                            idfacebook = bFacebookData.getString("id");
                            namefacebook = bFacebookData.getString("name");
                            AndroiduserLogin au = new AndroiduserLogin();
                            au.setUsername(idfacebook);
                            au.setPassword(idfacebook + namefacebook);
                            loginUsingFacebook(au);
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, name"); // Parámetros que pedimos a facebook
                    request.setParameters(parameters);
                    request.executeAsync();
                }
                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                }
            });
        } else{
            if(loginButtonFacebookCustom != null)
            loginButtonFacebookCustom.setVisibility(View.INVISIBLE);
        }

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(LoginActivity.this);
                forgotPasswordDialog.show();
            }
        });

    }
    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("id", id);
            if (object.has("name"))
                bundle.putString("name", object.getString("name"));


            return bundle;
        }
        catch(JSONException e) {
            Log.d("facebook","Error parsing JSON");
        }
        return null;
    }

    private void loginUsingFacebook(AndroiduserLogin au){
        dialog = Utils.showCustomDialog(dialog, LoginActivity.this,
                getResources().getString(R.string.loading), getResources().getString(R.string.logging));
        final AndroiduserLogin au_final = au;

        Call<Void> call = svc.loginUsingFacebook(au);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final Response responsefinal =  response;

                dialog = Utils.hideCustomDialog(dialog);
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginAndroiduser(au_final);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.bad_credentials, Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loginAndroiduser(AndroiduserLogin au){
        dialog = Utils.showCustomDialog(dialog, LoginActivity.this,
                getResources().getString(R.string.loading), getResources().getString(R.string.logging));

        final AndroiduserLogin au_final = au;
        Call<Void> call = svc.loginAndroiduser(au);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final Response responsefinal =  response;

                dialog = Utils.hideCustomDialog(dialog);
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateAndSaveAndroiduser(au_final, responsefinal.headers().get("Authorization"));
                            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.bad_credentials, Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dialog = Utils.hideCustomDialog(dialog);
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onResume(){
        super.onResume();
        svc = Svc.initRegisterLogin();
        svcAth = Svc.initAuth(LoginActivity.this);
        db = new DatabaseManager(LoginActivity.this);
    }

    public AndroiduserLogin createLoginForm(){
        AndroiduserLogin aul = new AndroiduserLogin();

        aul.setUsername(emailInput.getText().toString());
        aul.setPassword(passwordInput.getText().toString());

        return aul;
    }

    private boolean filterInput(){
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(!email.contains("@") || !email.contains(".")){
            emailInput.setError(getResources().getString(R.string.email_error));
            return false;
        }else{
            if(password.length() < 6){
                passwordInput.setError(getResources().getString(R.string.password_short_error));
                return false;
            }
        }

        return true;
    }


    private void updateAndSaveAndroiduser(AndroiduserLogin aufinal, String token){

        User au = db.findUser();
        if(au == null){
            au = new User();
        }
        au.setUsername(aufinal.getUsername());
        au.setPassword(aufinal.getPassword());
        au.setToken(token);
        au.setLogged(true);


        db.saveUser(au);
    }


}
