package ta.na.mao.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import ta.na.mao.R;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Blocker;
import ta.na.mao.database.models.ViewPagerModel;
import ta.na.mao.utils.adapters.mainactivityviewpager.ViewPagerMainActivityAdapter;
import ta.na.mao.utils.services.GoalNotificationService;
import ta.na.mao.utils.services.GoalNotificationServiceOld;
import ta.na.mao.utils.services.NetworkSchedulerService;
import ta.na.mao.utils.services.NetworkSchedulerServiceOld;

public class MainActivity extends AppCompatActivity {


    ViewPagerMainActivityAdapter viewPagerMainActivityAdapter;
    MaterialDialog logOutDialog;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_main_viewpager);
        viewPagerMainActivityAdapter = new ViewPagerMainActivityAdapter(this);
        viewPager.setAdapter(viewPagerMainActivityAdapter);
        viewPager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        viewPager.setPadding(120, 0, 120, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        viewPager.setPageMargin(20);


    }
    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scheduleNetworkCheckingJob();
        }else{
            scheduleNetworkCheckingJobOld();
        }
    }



    public  void loadLogoutDialog(){
        logOutDialog = new MaterialDialog(this);
        logOutDialog.title(R.string.logout_dialog_title, getResources().getString(R.string.logout_dialog_title));
        logOutDialog.message(R.string.logout_dialog_message, getResources().getString(R.string.logout_dialog_message));
        logOutDialog.positiveButton(R.string.logout_positive_button,
                getResources().getString(R.string.logout_positive_button), new Function1<MaterialDialog, Unit>() {
                    @Override
                    public Unit invoke(MaterialDialog materialDialog) {
                        logOut();
                        return null;
                    }
                });
        logOutDialog.negativeButton(R.string.logout_negative_button,
                getResources().getString(R.string.logout_negative_button), null);
        logOutDialog.show();
    }
    void logOut(){
        db  = new DatabaseManager(MainActivity.this);
        db.removeUser();
        db.removeUserDetails();
        db.removeGoals();
        db.removeTasks();
        db.removeTransactions();
        db.removeInstallments();
        startActivity(new Intent( MainActivity.this, SplashActivity.class));
        finish();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleNetworkCheckingJob() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Log.e("schedulingJob", "SchedulingJob");
            JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                    .setRequiresCharging(false)
                    .setMinimumLatency(1000)
                    .setOverrideDeadline(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();

            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(myJob);

            JobInfo goalNotificationsJob = new JobInfo.Builder(1, new ComponentName(this, GoalNotificationService.class))
                    .setRequiresCharging(false)
                    .setMinimumLatency(1000)
                    .setOverrideDeadline(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();

            JobScheduler goalNotificationsJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            goalNotificationsJobScheduler.schedule(goalNotificationsJob);
        } else{
            Intent serviceIntent = new Intent(this, GoalNotificationServiceOld.class);
            startService(serviceIntent);
            Intent serviceIntent2 = new Intent(this, NetworkSchedulerServiceOld.class);
            startService(serviceIntent2);
        }


    }
    public void scheduleNetworkCheckingJobOld(){
        Log.e("sche!!!!", "scheduleNetworkCheckingJobOld");
        Intent serviceIntent = new Intent(this, GoalNotificationServiceOld.class);
        startService(serviceIntent);
        if(isConnected(getApplicationContext())){
            DatabaseManager db = new DatabaseManager(getApplicationContext());
            if(db.findBlocker().isBlocked()){
                Log.e("Timer", "is blocked!");
                NetworkSchedulerServiceOld.checkBlocker(getApplicationContext());

            }else{
                Log.e("Timer", "NOT Blocked, doing job");
                Blocker blocker = db.findBlocker();
                blocker.setBlocked(true);
                blocker.setLast_update(new Date());
                db.saveBlocker(blocker);

                NetworkSchedulerServiceOld.updateData(getApplicationContext());

            }
        }
    }
}
