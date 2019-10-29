package ta.na.mao.utils.adapters.mainactivityviewpager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.activities.MainActivity;
import ta.na.mao.activities.SplashActivity;
import ta.na.mao.activities.UserDetailsActivity;
import ta.na.mao.activities.UserDetailsOldActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.database.models.ViewPagerModel;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.adapters.mainactivityviewpager.CalculatorViewPagerAdapter;

public class ViewPagerMainActivityAdapter  extends PagerAdapter{

    private Activity mContext;
    DatabaseManager db;
    MaterialDialog logOutDialog;

    public ViewPagerMainActivityAdapter(Activity context) {
        mContext = context;
        db = new DatabaseManager(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = null;
        LinearLayout buttonLayout = null;
        final int positionFinal  = position;


        switch(positionFinal){
            case 0:
                layout = (ViewGroup) inflater.inflate(R.layout.view_page_main_activity_profile, collection, false);
                buttonLayout = layout.findViewById(R.id.view_page_main_activity_profile_layout);

                buttonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            Intent userDetails = new Intent(((MainActivity)(mContext)), UserDetailsActivity.class);
                            userDetails.putExtra(Defaultdata.BACK_BUTTON_ENABLED, true);
                            ((MainActivity)(mContext)).startActivity(userDetails);
                        }else{
                            Intent userDetails = new Intent(((MainActivity)(mContext)), UserDetailsOldActivity.class);
                            userDetails.putExtra(Defaultdata.BACK_BUTTON_ENABLED, true);
                            ((MainActivity)(mContext)).startActivity(userDetails);
                        }
                    }
                });
                break;
            case 1:
                layout = (ViewGroup) inflater.inflate(R.layout.view_page_main_activity_balance, collection, false);

                PieChart chart = layout.findViewById(R.id.fragment_financialcontrol_incomeoutgo_menu_chart);
                TextView balanceText = layout.findViewById(R.id.fragment_financialcontrol_incomeoutgo_menu_balance);

                balanceText.setText("R$ " + Utils.formatingToCurrency(getTotalBalance(mContext)));
                Utils.loadPieChartBalanceViewAdapter(chart, mContext,
                        Utils.getTotalIncomeOutgo(mContext, true),
                        Utils.getTotalIncomeOutgo(mContext, false));
                buttonLayout = layout.findViewById(R.id.view_page_main_activity_profile_layout);

                buttonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent userDetails = new Intent(((MainActivity)(mContext)), FinancialControlMainActivity.class);
                            userDetails.putExtra(Defaultdata.BACK_BUTTON_ENABLED, true);
                            ((MainActivity)(mContext)).startActivity(userDetails);
                    }
                });
                break;
            case 2:
                layout = (ViewGroup) inflater.inflate(R.layout.view_page_main_activity_calculator, collection, false);
                buttonLayout = layout.findViewById(R.id.view_page_main_activity_profile_layout);

                RecyclerView calculatorRecycler = layout.findViewById(R.id.activity_calculator_main_recyclerview);

                List<Object> calculatorElements = new ArrayList<>();
                calculatorElements.addAll(db.findAllServicePricesFinished());
                calculatorElements.addAll(db.findAllProductPricesFinished());

                CalculatorViewPagerAdapter adapter = new CalculatorViewPagerAdapter(mContext,
                        calculatorElements);
                calculatorRecycler.setLayoutManager(new LinearLayoutManager(mContext));

                calculatorRecycler.setAdapter(adapter);


                buttonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent calculatorMainActivity = new Intent(((MainActivity)(mContext)), CalculatorMainActivity.class);

                        ((MainActivity)(mContext)).startActivity(calculatorMainActivity);
                    }
                });
                break;

            case 3:
                layout = (ViewGroup) inflater.inflate(R.layout.view_page_main_activity_logout, collection, false);
                buttonLayout = layout.findViewById(R.id.view_page_main_activity_profile_layout);

                buttonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadLogoutDialog();
                    }
                });
                break;
        }

        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return "";
    }


    public static Double getTotalBalance(Context context){
        Double balance = 0.0;
        DatabaseManager db = new DatabaseManager(context);
        List<Transaction> transactions = db.findTransactionBeforeDate(new Date());
        List<Installment> installments = db.findInstallmentsPaidBeforeDate(new Date());

        for(Transaction transaction :transactions){
            if(transaction.isIncome()){
                if(transaction.isInstallment()){
                    balance += transaction.getEntrance_payment();
                }else{
                    balance += transaction.getValue();
                }
            }else{
                if(transaction.isInstallment()){
                    balance -= transaction.getEntrance_payment();
                }else{
                    balance -= transaction.getValue();
                }
            }
        }

        for(Installment installment : installments){
            if(installment.isIncome()){
                balance += installment.getPayment();
            }else{
                balance -= installment.getPayment();
            }
        }

        return  balance;
    }

    public  void loadLogoutDialog(){
        logOutDialog = new MaterialDialog(mContext);
        logOutDialog.title(R.string.logout_dialog_title, mContext.getResources().getString(R.string.logout_dialog_title));
        logOutDialog.message(R.string.logout_dialog_message, mContext.getResources().getString(R.string.logout_dialog_message));
        logOutDialog.positiveButton(R.string.logout_positive_button,
                mContext.getResources().getString(R.string.logout_positive_button), new Function1<MaterialDialog, Unit>() {
                    @Override
                    public Unit invoke(MaterialDialog materialDialog) {
                        logOut();
                        return null;
                    }
                });
        logOutDialog.negativeButton(R.string.logout_negative_button,
                mContext.getResources().getString(R.string.logout_negative_button), null);
        logOutDialog.show();
    }
    void logOut(){
        db.removeUser();
        db.removeUserDetails();
        db.removeGoals();
        db.removeTasks();
        db.removeTransactions();
        db.removeInstallments();
        mContext.startActivity(new Intent( mContext, SplashActivity.class));
    }
}
