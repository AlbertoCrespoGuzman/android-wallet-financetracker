package ta.na.mao.activities.fragments.FinancialControl;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.DatePager;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.adapters.GoalsRecyclerAdapter;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;
import ta.na.mao.utils.dialogs.GoalFormDialog;

public class FragmentFinancialControlGoals  extends Fragment {
    View view;
    List<DatePager> items = new ArrayList<>();
    DecimalFormat newFormat = new DecimalFormat("0.00");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loadTabs();
        view = inflater.inflate(R.layout.fragment_financial_controll_statements, container, false);
        FragmentFinancialControlGoals.DemoYearsPagerAdapter adapter = new FragmentFinancialControlGoals.DemoYearsPagerAdapter();
        adapter.addAll(items);

        ViewPager viewPager = view.findViewById(R.id.fragment_financialcontrol_statement_viewpager);
        viewPager.setAdapter(adapter);
        int month = 0;
        int year = 0;
        if(getArguments()!= null){
            month = getArguments().getInt("month", 0);
            year = getArguments().getInt("year", 0);
        }
        viewPager.setCurrentItem(getCurrentItem(month, year));

        RecyclerTabLayout recyclerTabLayout = view.findViewById(R.id.recycler_tab_layout);
        recyclerTabLayout.setUpWithViewPager(viewPager);
        return view;

    }

    public void loadTabs(){

        Calendar c = Calendar.getInstance();
        c.set(2017, 7, 1);
        for(int i=0; i< 100 ; i++){
            c.add(Calendar.MONTH, + 1);
            items.add(new DatePager((c.get(Calendar.MONTH) + 1) , c.get(Calendar.YEAR)));
        }

    }
    int getCurrentItem(int month, int year){
        int currentPosition = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        if(month == 0 && year == 0){
            month = c.get(Calendar.MONTH) + 1;
            year = c.get(Calendar.YEAR);
        }

        for(int i=0; i < items.size(); i++){
            int currentMonth = items.get(i).getMonth();
            int currentYear = items.get(i).getYear();

            if(currentMonth == month && currentYear == year){
                currentPosition = i;
                break;
            }
        }
        return currentPosition;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        //    getActivity().setTitle("Menu 1");
    }

    @Override
    public void onResume(){
        super.onResume();
        if(getActivity() instanceof FinancialControlMainActivity){
            if(getActivity() != null){
                ((FinancialControlMainActivity) getActivity()).changeTopBarTitle(Defaultdata.TopBarTitle.GOALS);
            }
        }
    }
    public class DemoYearsPagerAdapter extends PagerAdapter  implements StatementRecyclerAdapter.ItemClickListener
                                                                        ,  GoalFormDialog.DialogClickListener{

        private List<DatePager> items = new ArrayList<>();
        DatabaseManager db;
        DemoYearsPagerAdapter demoYearsPagerAdapter;
        public DemoYearsPagerAdapter(/* List<String> items */) {
            db = new DatabaseManager(getActivity());
            //     this.items = items;
            demoYearsPagerAdapter = this;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.fragment_financialcontrol_goals_content, container, false);
            }else{
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.fragment_financialcontrol_goals_content_old, container, false);
            }

//            TextView text = view.findViewById(R.id.title);
//            text.setText(items.get(position));
            Calendar c = Calendar.getInstance();

            int month = items.get(position).getMonth();
            int year = items.get(position).getYear();

            month = month == 1 ? 12 : month - 1;
            year = month == 12 ? year - 1  : year;

            final int monthfinal = month;
            final int yearfinal = year;

            Log.e("month","" + month);
            Log.e("year","" + year);

            c.set(year, month,  1);

            final List<Goal> goals = db.findGoalsByDateOneMonth(c.getTime());

            Button addGoalsButton = view.findViewById(R.id.fragment_financialcontrol_goal_content_add_button);
            addGoalsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((FinancialControlMainActivity)getActivity()).changeToDialogGoalForm(monthfinal, yearfinal);
                    GoalFormDialog goalFormDialog = new GoalFormDialog(getActivity(), monthfinal, yearfinal, demoYearsPagerAdapter, 0, goals);
                    goalFormDialog.show();
                }
            });


            RecyclerView recycler = view.findViewById(R.id.fragment_financialcontrol_statement_content_recyclerview);

            GoalsRecyclerAdapter adapter = new GoalsRecyclerAdapter(getActivity(),goals);
            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

            adapter.setClickListener(this);
            recycler.setAdapter(adapter);

            Log.e("getting now", items.get(position) + " | instatiateItem  goals " + goals.size());

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public String getPageTitle(int position) {
            return items.get(position).getName();
        }

        public void addAll(List<DatePager> items) {
            this.items = new ArrayList<>(items);
        }

        @Override
        public void onItemClick(View view, int position) {

        }
        public String getBalanceText(Date date, List<Transaction> transactions, List<Installment> installments){
            double balance = 0.0;
            String balanceText = getActivity().getResources().getString(R.string.balance_text);
            Locale localeBR = new Locale("pt", "BR");

            SimpleDateFormat fmt = new SimpleDateFormat("' ' MMMM", localeBR);

            balanceText += fmt.format(date);

            for(Transaction transaction: transactions){
                if(transaction.isIncome()){
                    if(!transaction.isInstallment()){
                        balance += transaction.getValue();
                    }else{
                        balance += transaction.getEntrance_payment();
                    }
                }else{
                    if(!transaction.isInstallment()){
                        balance -= transaction.getValue();
                    }else{
                        balance -= transaction.getEntrance_payment();
                    }
                }
            }

            for(Installment installment: installments){
                if(installment.isIncome()){
                    balance += installment.getValue();
                }else{
                    balance -= installment.getValue();
                }
            }

            balanceText += ": R$ " + newFormat.format(balance);

            return balanceText;

        }

        @Override
        public void onButtonGoalFormDialogClick(boolean accepted) {

        }
    }
}