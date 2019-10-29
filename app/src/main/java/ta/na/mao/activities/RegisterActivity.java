package ta.na.mao.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ta.na.mao.R;
import ta.na.mao.communications.Svc;
import ta.na.mao.communications.SvcApi;
import ta.na.mao.communications.http.auth.AndroiduserLogin;
import ta.na.mao.communications.http.auth.ResponseMessage;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.User;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.dialogs.ProgressCustomDialog;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alberto on 2017/12/05.
 */

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailInput, passwordInput, passwordconfirmInput;
    Button submitButton;
    SvcApi svc, svcAth;
    ProgressCustomDialog dialog;
    DatabaseManager db;
    ConstraintLayout layout;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    LoginButton loginButtonFacebookTrue;
    Button loginButtonFacebookCustom;
    Bundle bFacebookData;
    String idfacebook, namefacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_register);
        }else{
            setContentView(R.layout.activity_register_old);
        }



        emailInput = findViewById(R.id.activity_register_input_email);
        passwordInput = findViewById(R.id.activity_register_input_password);
        passwordconfirmInput = findViewById(R.id.activity_register_input_password_confirm);
        submitButton = findViewById(R.id.activity_register_input_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter()) {
                    // get device_id && device_name
                    registerUser(buildRegistrationModel());
                    // connect Register
                }
            }
        });
        callbackManager = CallbackManager.Factory.create();
        loginButtonFacebookTrue = findViewById(R.id.register_button_facebook);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            loginButtonFacebookCustom = findViewById(R.id.login_button);
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
        }else{
           if(loginButtonFacebookCustom != null) loginButtonFacebookCustom.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        svc = Svc.initRegisterLogin();
        db = new DatabaseManager(RegisterActivity.this);
    }

    private User buildRegistrationModel() {

        User au = new User();

        au.setUsername(emailInput.getText().toString());
        au.setPassword(passwordInput.getText().toString());
        au.setPassword_confirmation(passwordconfirmInput.getText().toString());
        au.setDevice_id(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        au.setDevice_name(android.os.Build.MODEL);

        return au;
    }

    private boolean filter() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String passwordconfirm = passwordconfirmInput.getText().toString();

        if (!email.contains("@") || !email.contains(".")) {
            emailInput.setError(getResources().getString(R.string.email_error));
            return false;
        } else if (!password.equals(passwordconfirm)) {
            passwordInput.setError(getResources().getString(R.string.passwords_doesnt_match));
            return false;
        } else if (password.length() < 6) {
            passwordInput.setError(getResources().getString(R.string.password_short_error));
            return false;
        }
        return true;
    }


    private void registerUser(User au){
        dialog = Utils.showCustomDialog(dialog, RegisterActivity.this,
                getResources().getString(R.string.loading), getResources().getString(R.string.registering));

        final User au_final = au;
        Call<List<ResponseMessage>> call = svc.registerAndroiduser(au);
        call.enqueue(new Callback<List<ResponseMessage>>() {
            @Override
            public void onResponse(Call<List<ResponseMessage>> call,
                                   Response<List<ResponseMessage>> response) {
                dialog = Utils.hideCustomDialog(dialog);
                final List<ResponseMessage> aur = response.body();
                final Response<List<ResponseMessage>> responsefinal = response;
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showSuccessMessagesToast(getApplicationContext(), aur);
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                } else {
                    try {
                        Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(responsefinal.code() == 500|| responsefinal.code() == 503){
                        Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ResponseMessage>> call, Throwable t) {
                t.printStackTrace();
                dialog = Utils.hideCustomDialog(dialog);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failure_connection), Toast.LENGTH_SHORT).show();
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
        dialog = Utils.showCustomDialog(dialog, RegisterActivity.this,
                getResources().getString(R.string.loading), getResources().getString(R.string.registering));

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
        dialog = Utils.showCustomDialog(dialog, RegisterActivity.this,
                getResources().getString(R.string.loading), getResources().getString(R.string.registering));

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
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                startActivity(new Intent(RegisterActivity.this, UserDetailsActivity.class));
                            }else{
                                startActivity(new Intent(RegisterActivity.this, UserDetailsOldActivity.class));
                            }
                            finish();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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

