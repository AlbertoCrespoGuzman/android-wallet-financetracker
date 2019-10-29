package ta.na.mao.activities;
// emulator -use-system-libs -avd mini
//emulator -gpu off -use-system-libs -avd mini

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import ta.na.mao.R;
import ta.na.mao.communications.SvcApi;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Blocker;
import ta.na.mao.utils.Utils;


/**
 * Created by alberto on 2017/12/05.
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 300;
    SvcApi svcAth;
    DatabaseManager db;
    ImageView image;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
      //  getSupportActionBar().hide();

        db = new DatabaseManager(SplashActivity.this);
        checkBlocker();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                if(!db.ExistsUser()){

                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }else{
/* HAS TOKEN*/     if(db.findUser().isLogged() && db.findUser().getToken() != null && !db.findUser().getToken().equals("")){
    /*HAS USERDETAILS */if(db.ExistsUserDetails()){
                            Log.e("SplashActivity", " -------------- SplashActivity 1 ");
         /*CONTINUE OK*/    Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            finish();
                        }else{
        /*CHECK CONNECTION*/if(Utils.isNetworkAvailable(getApplicationContext())){
                                Utils.getData(SplashActivity.this);
                           }else{
                                Log.e("SplashActivity", " -------------- SplashActivity 1 ");
/* NO INTERNET - CONTINUE*/     Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        }
                    }else{

/* NO TOKEN - GO LOGIN*/Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }

                }

            }
        }, SPLASH_DISPLAY_LENGTH);

    }
    /*
    private void getUserDetails(){
        dialog = Utils.showDialog(dialog, SplashActivity.this, R.string.loading);
        svcAth = Svc.initAuth(SplashActivity.this);

        Call<UserDetails> call = svcAth.getUserDetails();
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                final Response responsefinal =  response;
                final UserDetails userDetails = response.body();

                dialog = Utils.hideDialog(dialog);
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            saveAndContinue(userDetails);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.bad_credentials, Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAndContinue(UserDetails userDetails){

        if(userDetails.isEmpty()){
            Intent loginIntent = new Intent(SplashActivity.this, UserDetailsActivity.class);
            startActivity(loginIntent);
            finish();
        }else{
            db.saveUserDetails(userDetails);

            Intent loginIntent = new Intent(SplashActivity.this, FinancialControlMainActivity.class);
            startActivity(loginIntent);
            finish();
        }

    }
*/

    private void checkBlocker(){
        if(!db.ExistsBlocker()){
            Blocker blocker = new Blocker();
            blocker.setLast_update(new Date());
            blocker.setBlocked(false);
            db.saveBlocker(blocker);
        }else{
            Blocker blocker = db.findBlocker();
            if(blocker.isBlocked() && new Date().getTime() - blocker.getLast_update().getTime() > 1000 * 60){
                blocker.setBlocked(false);
                blocker.setLast_update(new Date());
                db.saveBlocker(blocker);
            }
        }
    }
}