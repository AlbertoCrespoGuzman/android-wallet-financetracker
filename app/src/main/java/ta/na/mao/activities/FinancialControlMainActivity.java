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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import ta.na.mao.R;
import ta.na.mao.activities.fragments.EmptyFragment;
import ta.na.mao.activities.fragments.FragmentFinancialControlIncomeOutgoForm;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlIncomeOutgoMenu;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlCharts;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlGoals;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlReport;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlStatement;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Blocker;
import ta.na.mao.database.models.Installment;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.dialogs.DialogInstallmentPaid;
import ta.na.mao.utils.services.GoalNotificationService;
import ta.na.mao.utils.services.GoalNotificationServiceOld;
import ta.na.mao.utils.services.NetworkSchedulerService;
import ta.na.mao.utils.services.NetworkSchedulerServiceOld;

public class FinancialControlMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {


    BottomNavigationView bottomNavigationView;
    MaterialDialog logOutDialog;
    DatabaseManager db;
    FirebaseJobDispatcher dispatcher;
    ImageView sideBarButton;
    DrawerLayout drawer;
    TextView userNameDrawer, userEmailDrawer;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.e("onCreate", "onCreate FinancialControlMainActivity");
            setContentView(R.layout.activity_main_financial_control);
            toolbar = findViewById(R.id.toolbar);
            toolbarTitle = findViewById(R.id.toolbar_title);
            toolbar.setBackgroundColor(getResources().getColor(R.color.tool_bar_color));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        }else{
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_png));
        }


            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View navigationHeader = navigationView.getHeaderView(0);

            userNameDrawer = navigationHeader.findViewById(R.id.nav_header_main_user_name);
            userEmailDrawer = navigationHeader.findViewById(R.id.nav_header_main_user_email);

            db = new DatabaseManager(getApplicationContext());
            if(db.findUserDetails() != null){
                userNameDrawer.setText(db.findUserDetails().getName());
                userEmailDrawer.setText(db.findUserDetails().getEmail());
            }

            //add this line to display menu1 when the activity is loaded
            displaySelectedScreen(R.id.nav_menu1);
            bottomNavigationView = findViewById(R.id.bottom_navigation);


            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment fragment = null;
                    Log.e("bottomNavigationView", "onNavigationItemSelected -> getItemId = " + item.getItemId());

                    switch (item.getItemId()) {
                        case R.id.nav_financialcontrol_incomeoutgo:
                            fragment = new FragmentFinancialControlIncomeOutgoMenu();
                            break;
                        case R.id.nav_financialcontrol_statement:
                            fragment = new FragmentFinancialControlStatement();
                            break;
                        case R.id.nav_financialcontrol_charts:
                            fragment = new FragmentFinancialControlCharts();
                            break;
                        case R.id.nav_financialcontrol_report:
                            fragment = new FragmentFinancialControlReport();
                            break;
                        case R.id.nav_financialcontrol_goals:
                            fragment = new FragmentFinancialControlGoals();
                            break;


                    }
                    if (fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();
                    }

                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);

                    return true;
                }
            });



    }

        @Override
        public void onBackPressed() {

            Log.e("onBackPressed", "onBackPressed FinancialControlMainActivity");
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    @Override
    protected void onStart() {
        super.onStart();

        Log.e("onStart", "onStart FinancialControlMainActivity");
        // Start service and provide it a way to communicate with this class.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scheduleNetworkCheckingJob();
        }else{
            scheduleNetworkCheckingJobOld();
        }
    }
    @Override
    protected void onStop() {
        Log.e("FinancialControl", "OnStop");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            stopService(new Intent(this, NetworkSchedulerService.class));
        }
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onResume(){
        super.onResume();

        Log.e("onResume", "onResume FinancialControlMainActivity");
        checkIfInstallmentHasBeenPaid();
        Log.e("onResume","onResume bottomNavigationView null? " + (bottomNavigationView == null));
        if(bottomNavigationView != null){
            Log.e("bottomNavigationView", "bottomNavigationView getSelectedItemId = " + bottomNavigationView.getSelectedItemId());
            bottomNavigationView.setSelectedItemId(bottomNavigationView.getSelectedItemId());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void changeTopBarTitle(Defaultdata.TopBarTitle title){
        switch (title){
            case INCOME_OUTGO:
                toolbarTitle.setText(getResources().getString(R.string.income_outgo_title));
                break;
            case STATEMENT:
                toolbarTitle.setText(getResources().getString(R.string.statement_title));
                break;
            case STATS:
                toolbarTitle.setText(getResources().getString(R.string.stats_title));
                break;
            case REPORT:
                toolbarTitle.setText(getResources().getString(R.string.report_title));
                break;
            case GOALS:
                toolbarTitle.setText(getResources().getString(R.string.goals_title));
                break;
        }

    }
    private void displaySelectedScreen(int itemId) {

        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_menu1:
                fragment = new FragmentFinancialControlIncomeOutgoMenu();
                if(bottomNavigationView != null) bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_menu2:
                Intent CalculatorActivity = new Intent(FinancialControlMainActivity.this, CalculatorMainActivity.class);
                startActivity(CalculatorActivity);
                finish();
                break;
            case R.id.nav_menu3:
                fragment = new EmptyFragment();
                //if(bottomNavigationView != null) bottomNavigationView.setVisibility(View.GONE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Intent userDetails = new Intent(FinancialControlMainActivity.this, UserDetailsActivity.class);
                    userDetails.putExtra(Defaultdata.BACK_BUTTON_ENABLED, true);
                    startActivity(userDetails);
                    finish();
                }else{
                    Intent userDetails = new Intent(FinancialControlMainActivity.this, UserDetailsOldActivity.class);
                    userDetails.putExtra(Defaultdata.BACK_BUTTON_ENABLED, true);
                    startActivity(userDetails);
                    finish();
                }

                break;
            case R.id.nav_menu4:
                fragment = new EmptyFragment();
                if(bottomNavigationView != null) bottomNavigationView.setVisibility(View.GONE);
                loadLogoutDialog();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


    }
    public void changeToFragmentIncomeOutGoForm(boolean income){

        bottomNavigationView.setSelectedItemId(R.id.nav_financialcontrol_incomeoutgo);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FragmentFinancialControlIncomeOutgoForm();
        Bundle bundle = new Bundle();
        bundle.putBoolean("income", income);
        fragment.setArguments(bundle);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
    public void changeToFragmentIncomeOutGoForm(boolean income, long transaction_local_id){

        bottomNavigationView.setSelectedItemId(R.id.nav_financialcontrol_incomeoutgo);
        Log.e("MAINACTIVITY", "changeToFragmentIncomeOutGoForm"+ income + " " + transaction_local_id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FragmentFinancialControlIncomeOutgoForm();
        Bundle bundle = new Bundle();
        bundle.putBoolean("income", income);
        bundle.putLong("transaction_local_id", transaction_local_id);
        fragment.setArguments(bundle);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
    public void changeToFragmentIncomeOutgoMenu(){
        bottomNavigationView.setSelectedItemId(R.id.nav_financialcontrol_incomeoutgo);
        /*
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FragmentFinancialControlIncomeOutgoMenu();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();*/
    }
    public void changeToFragmentGoalsMenu(int month, int year){
        bottomNavigationView.setSelectedItemId(R.id.nav_financialcontrol_goals);
        /*
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FragmentFinancialControlGoals();
        Bundle bundle = new Bundle();
        bundle.putInt("month", month);
        bundle.putInt("year", year);
        fragment.setArguments(bundle);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
        */
    }
    public void changeToFragmentStatement(int month, int year){

                bottomNavigationView.setSelectedItemId(R.id.nav_financialcontrol_statement);
        /*
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FragmentFinancialControlStatement();
        Bundle bundle = new Bundle();
        bundle.putInt("month", month);
        bundle.putInt("year", year);
        fragment.setArguments(bundle);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
        */
    }
    /*public void changeToDialogGoalForm(int month, int year){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new GoalFormDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("month", month);
        bundle.putInt("year", year);

        fragment.setArguments(bundle);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();


    } */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                getResources().getString(R.string.logout_negative_button), new Function1<MaterialDialog, Unit>() {
                    @Override
                    public Unit invoke(MaterialDialog materialDialog) {
                        startActivity(new Intent( FinancialControlMainActivity.this, MainActivity.class));
                        finish();
                        return null;
                    }
                });
        logOutDialog.show();
    }
    void logOut(){
        db  = new DatabaseManager(FinancialControlMainActivity.this);
        db.removeUser();
        db.removeUserDetails();
        db.removeGoals();
        db.removeTasks();
        db.removeTransactions();
        db.removeInstallments();
        startActivity(new Intent( FinancialControlMainActivity.this, SplashActivity.class));
        finish();
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
    public void checkIfInstallmentHasBeenPaid(){
        DatabaseManager db = new DatabaseManager(getApplicationContext());

        List<Installment> installments = db.findInstallmentBeforeAndEqual(new Date());

        for(Installment installment : installments){
                    DialogInstallmentPaid dialogInstallmentPaid = new DialogInstallmentPaid(FinancialControlMainActivity.this, installment);
                    dialogInstallmentPaid.show();
            }
    }
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}