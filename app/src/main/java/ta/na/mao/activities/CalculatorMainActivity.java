package ta.na.mao.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
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
import ta.na.mao.activities.fragments.Calculator.CalculatorMenuFragment;
import ta.na.mao.activities.fragments.Calculator.ProductCost.ProductCostMainFragment;
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceFormFragment;
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceMainFragment;
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceFormFragment;
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceMainFragment;
import ta.na.mao.activities.fragments.EmptyFragment;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlCharts;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlGoals;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlIncomeOutgoMenu;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlReport;
import ta.na.mao.activities.fragments.FinancialControl.FragmentFinancialControlStatement;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.utils.Defaultdata;

public class CalculatorMainActivity  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


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
        setContentView(R.layout.activity_main_calculator_main);
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


        boolean changeToServicePriceMainFragment = getIntent().getBooleanExtra(getApplicationContext().getResources().getString(
                R.string.calculator_main_activity_change_to_service_price_main_fragment), false
                );
        boolean changeToProductPriceMainFragment = getIntent().getBooleanExtra(getApplicationContext().getResources().getString(
                R.string.calculator_main_activity_change_to_product_price_main_fragment), false
        );
        boolean changeToProductCostMainFragment = getIntent().getBooleanExtra(getApplicationContext().getResources().getString(
                R.string.calculator_main_activity_change_to_product_cost_main_fragment), false
        );
        if(changeToServicePriceMainFragment){
            changeToFragmentServicePriceMain();
        }else if(changeToProductPriceMainFragment){
            changeToFragmentProductPriceMain();
        }else if(changeToProductCostMainFragment){
            changeToFragmentProductCostMain();
        }else{
            changeToFragmentCalculatorMenu();
        }




    }
    public void changeToFragmentServicePriceMain(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ServicePriceMainFragment();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }
    public void changeToFragmentProductPriceMain(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ProductPriceMainFragment();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }
    public void changeToFragmentProductCostMain(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ProductCostMainFragment();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }
    public void changeToFragmentCalculatorMenu(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new CalculatorMenuFragment();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }
    public void changeToFragmentServiceForm(long local_id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ServicePriceFormFragment();

        Bundle bundle = new Bundle();
        bundle.putLong("local_id", local_id);

        fragment.setArguments(bundle);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }
    public void changeToFragmentProductForm(long local_id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ProductPriceFormFragment();

        Bundle bundle = new Bundle();
        bundle.putLong("local_id", local_id);

        fragment.setArguments(bundle);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

    }
    private void displaySelectedScreen(int itemId) {

        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_menu1:
                Intent financialControlMainActivity = new Intent(CalculatorMainActivity.this, FinancialControlMainActivity.class);
                startActivity(financialControlMainActivity);
                finish();
                break;
            case R.id.nav_menu2:
                Intent CalculatorActivity = new Intent(CalculatorMainActivity.this, CalculatorMainActivity.class);
                startActivity(CalculatorActivity);
                finish();
                break;
            case R.id.nav_menu3:
                fragment = new EmptyFragment();
                //if(bottomNavigationView != null) bottomNavigationView.setVisibility(View.GONE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Intent userDetails = new Intent(CalculatorMainActivity.this, UserDetailsActivity.class);
                    userDetails.putExtra(Defaultdata.BACK_BUTTON_ENABLED, true);
                    startActivity(userDetails);
                    finish();
                }else{
                    Intent userDetails = new Intent(CalculatorMainActivity.this, UserDetailsOldActivity.class);
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
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        Log.e("CalculatorMain","onNavigationItemSelected --> " + item.getItemId());
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
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
                        startActivity(new Intent( CalculatorMainActivity.this, MainActivity.class));
                        finish();
                        return null;
                    }
                });
        logOutDialog.show();
    }
    void logOut(){
        db  = new DatabaseManager(CalculatorMainActivity.this);
        db.removeUser();
        db.removeUserDetails();
        db.removeGoals();
        db.removeTasks();
        db.removeTransactions();
        db.removeInstallments();
        db.removeServicePrices();
        db.removeProductPrices();
        startActivity(new Intent( CalculatorMainActivity.this, SplashActivity.class));
        finish();
    }
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}
