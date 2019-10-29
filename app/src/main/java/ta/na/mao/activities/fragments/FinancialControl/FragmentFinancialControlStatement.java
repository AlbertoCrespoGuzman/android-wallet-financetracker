package ta.na.mao.activities.fragments.FinancialControl;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nshmura.recyclertablayout.RecyclerTabLayout;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.DatePager;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class FragmentFinancialControlStatement extends Fragment {
    View view;
    List<DatePager> items = new ArrayList<>();
    DecimalFormat newFormat = new DecimalFormat("0.00");
    boolean filter = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loadTabs();
        view = inflater.inflate(R.layout.fragment_financial_controll_statements, container, false);
        DemoYearsPagerAdapter adapter = new DemoYearsPagerAdapter(items);
        adapter.addAll(items);

        ViewPager viewPager = view.findViewById(R.id.fragment_financialcontrol_statement_viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentItem());

        RecyclerTabLayout recyclerTabLayout = view.findViewById(R.id.recycler_tab_layout);
        recyclerTabLayout.setUpWithViewPager(viewPager);
        return view;

    }

    public void loadTabs(){

        Calendar c = Calendar.getInstance();
        c.set(2017, 7, 1);
        for(int i=0; i< 100 ; i++){
            c.add(Calendar.MONTH, + 1);
            items.add(new DatePager((c.get(Calendar.MONTH) + 1),c.get(Calendar.YEAR)));
        }

    }
    int getCurrentItem(){
        int currentPosition = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);

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
                ((FinancialControlMainActivity) getActivity()).changeTopBarTitle(Defaultdata.TopBarTitle.STATEMENT);
            }
        }
    }

    public class DemoYearsPagerAdapter extends PagerAdapter  implements StatementRecyclerAdapter.ItemClickListener  {

        private List<DatePager> items = new ArrayList<>();
        DatabaseManager db;

        public DemoYearsPagerAdapter( List<DatePager> items ) {
            db = new DatabaseManager(getActivity());
            this.items = items;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.fragment_financialcontrol_statement_content, container, false);

            final RelativeLayout filterLayout = view.findViewById(R.id.filter_future_layout);
            final ImageView filterImage = view.findViewById(R.id.filter_future_checker_image);
            final int positionFinal = position;


            if(filter){
                filterLayout.setBackground(
                        getContext().getResources().getDrawable(R.drawable.filter_future_checked_layout));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_checked));
                }else{
                    filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_checked_png));
                }

            }else{
                filterLayout.setBackground(
                        getContext().getResources().getDrawable(R.drawable.filter_future_notchecked_layout));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_not_checked));
                }else{
                    filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_not_checked_png));
                }
            }
            TransitionManager.beginDelayedTransition(filterLayout);

            filterLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filter= !filter;
                    if(filter){
                        loadStatementsLayout(view, positionFinal);
                        filterLayout.setBackground(
                                getContext().getResources().getDrawable(R.drawable.filter_future_checked_layout));
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_checked));
                        }else{
                            filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_checked_png));
                        }
                    }else{
                        loadStatementsLayout(view, positionFinal);
                        filterLayout.setBackground(
                                getContext().getResources().getDrawable(R.drawable.filter_future_notchecked_layout));
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_not_checked));
                        }else{
                            filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_not_checked_png));
                        }
                    }
                    TransitionManager.beginDelayedTransition(filterLayout);
                }
            });


            loadStatementsLayout(view, position);

            container.addView(view);
            return view;
        }
        void loadStatementsLayout(View view, int position){
            Calendar c = Calendar.getInstance();

            int month = items.get(position).getMonth();
            int year = items.get(position).getYear();

            month = month == 1 ? 12 : month - 1;
            year = month == 12 ? year - 1  : year;

            Log.e("month","" + month);
            Log.e("year","" + year);

            c.set(year, month,  1);

            List<Transaction> transactions = db.findTransactionsByDateOneMonth(c.getTime());
            List<Installment> installments = db.findInstallmentByDateOneMonth(c.getTime());

            //FILTERING TRANSACTIONS WITH ENTRANCE VALUE = 0.0
            List<Transaction> transactionsTemp = transactions;

            for(int i=0; i< transactionsTemp.size(); i++){
                if(transactionsTemp.get(i).isInstallment() && transactionsTemp.get(i).getEntrance_payment() == 0.0){
                    transactions.remove(i);
                }
            }

            if(!filter){
                List<Installment> installmentstemp = new ArrayList<>();

                for(int i=0; i< installments.size(); i++){

                    if(!installments.get(i).isPaid()){
                        installmentstemp.add(installments.get(i));
                    }
                }
                installments.removeAll(installmentstemp);

                transactionsTemp = transactions;

                for(int i=0; i< transactionsTemp.size(); i++){
                    if(transactionsTemp.get(i).getDate().compareTo(new Date()) > 0){
                        transactions.remove(i);
                    }
                }
            }

            TextView balanceText1 = view.findViewById(R.id.fragment_financialcontrol_statement_content_balance_text_1);
            TextView balanceText2 = view.findViewById(R.id.fragment_financialcontrol_statement_content_balance_text_2);
            balanceText1.setText(Utils.getMonth(c.getTime(), getContext()));
            balanceText2.setText(Utils.getBalanceText( transactions, installments, getContext(), newFormat));
            RecyclerView recycler = view.findViewById(R.id.fragment_financialcontrol_statement_content_recyclerview);

            StatementRecyclerAdapter adapter = new StatementRecyclerAdapter(getActivity(), Utils.sortStatementElements(transactions, installments));
            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

            adapter.setClickListener(this);
            recycler.setAdapter(adapter);

            TransitionManager.beginDelayedTransition(recycler);

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


    }
}