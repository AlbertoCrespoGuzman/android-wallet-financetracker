package ta.na.mao.activities.fragments.FinancialControl;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.ReportElement;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.adapters.ReportChartsAdapter;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;


public class FragmentFinancialControlReport extends Fragment {
    View view;
    List<String> items = new ArrayList<>();
    DecimalFormat newFormat = new DecimalFormat("0.00");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loadTabs();
        view = inflater.inflate(R.layout.fragment_financial_control_reports, container, false);
        FragmentFinancialControlReport.ReportPagerAdapter adapter = new FragmentFinancialControlReport.ReportPagerAdapter();
        adapter.addAll(items);

        ViewPager viewPager = view.findViewById(R.id.fragment_financialcontrol_report_viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentItem());

        RecyclerTabLayout recyclerTabLayout = view.findViewById(R.id.recycler_tab_layout);
        recyclerTabLayout.setUpWithViewPager(viewPager);
        return view;

    }

    public void loadTabs() {

        boolean isFirst = true;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int firstYear = 2016;

        for (int i = firstYear; i < firstYear + 10; i++) {

            items.add( "1" + "." + i);
            items.add( "2" + "." + i);
        }

    }
    int getCurrentItem(){
        int itemPosition = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        for(int i = 0; i < items.size(); i++) {
            if(Integer.parseInt(items.get(i).split(Pattern.quote("."))[1]) == c.get(Calendar.YEAR)){
                int period = Integer.parseInt(items.get(i).split(Pattern.quote("."))[0]);
                int currentMonth = c.get(Calendar.MONTH) + 1;
                if( period == 1 && currentMonth < 7 ){
                    itemPosition = i;
                    break;
                }else if(period == 2 && currentMonth >= 7){
                    itemPosition = i;
                    break;
                }
            }
        }

        return itemPosition;
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
                ((FinancialControlMainActivity) getActivity()).changeTopBarTitle(Defaultdata.TopBarTitle.REPORT);
            }
        }
    }

    public class ReportPagerAdapter extends PagerAdapter implements StatementRecyclerAdapter.ItemClickListener {

        private List<String> items = new ArrayList<>();
        DatabaseManager db;
        String[] months1 = {"Jan","Fev", "Mar", "Abr","Mai","Jun"};
        String[] months2 = {"Jul","Ago", "Set", "Out","Nov","Dez"};
        public ReportPagerAdapter(/* List<String> items */) {
            db = new DatabaseManager(getActivity());
            //     this.items = items;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.fragment_financialcontrol_report_content, container, false);




            int period = Integer.parseInt(items.get(position).split(Pattern.quote("."))[0]);
            int year = Integer.parseInt(items.get(position).split(Pattern.quote("."))[1]);



            List<List<Transaction>> transactions = loadTransactionsByPeriod(period, year);
            List<List<Installment>> installments = loadInstallmentssByPeriod(period, year);
            List<List<Goal>> goals = loadGoalsByPeriod(period, year);


            TextView titleText = view.findViewById(R.id.fragment_financialcontrol_report_content_title2);
            titleText.setText(getPeriodText(period, year));

            RecyclerView recycler = view.findViewById(R.id.fragment_financialcontrol_report_content_recycler);

            List<ReportElement> reportElements = new ArrayList<>();

            // GERAL RECEITA

            ReportElement reportElementGeralReceita = new ReportElement(transactions, installments,
                    goals, true, getActivity().getResources().getStringArray(R.array.goal_income_categories_array)
                    [getActivity().getResources().getStringArray(R.array.goal_income_categories_array).length -1]
                    ,period,getActivity().getResources().getStringArray(R.array.goal_income_categories_array).length -1
                    );
            reportElements.add(reportElementGeralReceita);

            // GERAL GASTOS

            ReportElement reportElementGeralGastos = new ReportElement(transactions, installments,
                    goals, false, getActivity().getResources().getStringArray(R.array.goal_outgo_categories_array)
                    [getActivity().getResources().getStringArray(R.array.goal_outgo_categories_array).length -1]
                    ,period,getActivity().getResources().getStringArray(R.array.goal_outgo_categories_array).length -1
            );
            reportElements.add(reportElementGeralGastos);

            for(int i = 1 ; i < getActivity().getResources().getStringArray(R.array.goal_income_categories_array).length - 1;i++){
                ReportElement reportElement = new ReportElement(transactions, installments,
                        goals, true, getActivity().getResources().getStringArray(R.array.goal_income_categories_array)
                        [i],period,i
                );
                reportElements.add(reportElement);
            }
            for(int i = 1 ; i < getActivity().getResources().getStringArray(R.array.goal_outgo_categories_array).length - 1;i++){
                ReportElement reportElement = new ReportElement(transactions, installments,
                        goals, false, getActivity().getResources().getStringArray(R.array.goal_outgo_categories_array)
                        [i],period,i
                );
                reportElements.add(reportElement);
            }

            ReportChartsAdapter adapter = new ReportChartsAdapter(getActivity(),reportElements);
            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

            adapter.setClickListener(this);
            recycler.setAdapter(adapter);


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
            return items.get(position);
        }

        public void addAll(List<String> items) {
            this.items = new ArrayList<>(items);
        }

        @Override
        public void onItemClick(View view, int position) {

        }
        public void loadCombinedChart(boolean income, List<List<Transaction>> transactions, List<List<Installment>> installments,
                                      List<List<Goal>> goals, CombinedChart chart, TextView title, int goal_category, int period){
            // ADDING TRANSACTIONS WITH INSTALLMENTS BY MONTH

            List<Double> statementsByMonth =  new ArrayList<>();

            for(int i =0; i < transactions.size(); i++){
                Double sum = 0.0;
                for(int j=0; j < transactions.get(i).size(); j++){
                    if(transactions.get(i).get(j).isIncome() == income){
                        if(goal_category == 5){
                            if (transactions.get(i).get(j).isInstallment()) {
                                sum += transactions.get(i).get(j).getEntrance_payment();
                            } else {
                                sum += transactions.get(i).get(j).getValue();
                            }
                        }else{
                            if(transactions.get(i).get(j).getCategory() == goal_category){
                                if (transactions.get(i).get(j).isInstallment()) {
                                    sum += transactions.get(i).get(j).getEntrance_payment();
                                } else {
                                    sum += transactions.get(i).get(j).getValue();
                                }
                            }
                        }

                    }

                }
                statementsByMonth.add(sum);
            }
            for(int i =0; i < installments.size(); i++){
                Double sum = 0.0;
                for(int j=0; j < installments.get(i).size(); j++){
                    if(installments.get(i).get(j).isIncome() == income){
                        if(goal_category == 5){
                                sum += installments.get(i).get(j).getValue();
                        }else{
                            if(installments.get(i).get(j).getTransaction().getCategory() == goal_category){
                                    sum += installments.get(i).get(j).getValue();
                            }
                        }

                    }

                }
                statementsByMonth.set(i,statementsByMonth.get(i) + sum);
            }


            chart.getDescription().setEnabled(false);
            chart.setBackgroundColor(Color.WHITE);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.setHighlightFullBarEnabled(false);

            chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                    CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE,
                    CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE,
                    CombinedChart.DrawOrder.SCATTER
            });

            Legend l = chart.getLegend();
            l.setWordWrapEnabled(true);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setAxisMinimum(0f);
            xAxis.setGranularity(1f);
            if(period == 1 ){
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return months1[(int) value % months1.length];
                    }
                });
            }else{
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return months2[(int) value % months2.length];
                    }
                });
            }


            CombinedData data = new CombinedData();

            data.setData(generateLineData(goals, goal_category, income));
            data.setData(generateBarData(statementsByMonth, income));

          //  data.setValueTypeface(tfLight);

            xAxis.setAxisMaximum(data.getXMax() + 0.25f);

            chart.setData(data);



            chart.invalidate();

        }
        public BarData generateBarData(List<Double> data, boolean income){
            ArrayList<BarEntry> entries1 = new ArrayList<>();
            for (int index = 0; index < data.size(); index++) {
                entries1.add(new BarEntry(0 + index, data.get(index).floatValue()));

            }

            BarDataSet set1 = new BarDataSet(entries1, income ?
                    getActivity().getResources().getString(R.string.income_text) :
                    getActivity().getResources().getString(R.string.outgo_text));
            if(income){
                set1.setColor(Color.rgb(60, 220, 78));
                set1.setValueTextColor(Color.rgb(60, 220, 78));
            }else{
                set1.setColor(Color.parseColor("#c0360d"));
                set1.setValueTextColor(Color.parseColor("#c0360d"));
            }

            set1.setValueTextSize(10f);
          //  set1.setAxisDependency(YAxis.AxisDependency.LEFT);


            float groupSpace = 0.06f;
            float barSpace = 0.02f; // x2 dataset
            float barWidth = 0.50f; // x2 dataset
            // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

            BarData d = new BarData(set1);
            d.setBarWidth(barWidth);

            // make this BarData object grouped
//            d.groupBars(0, groupSpace, barSpace); // start at x = 0

            return d;


        }
        public LineData generateLineData(List<List<Goal>> goals, int category, boolean income){

            LineData d = new LineData();

            ArrayList<Entry> entries = new ArrayList<>();

            for (int index = 0; index < goals.size(); index++){
                boolean hasGoal = false;
                for( int i =0; i< goals.get(index).size(); i++){
                    if(goals.get(index).get(i).isIncome() == income &&
                            goals.get(index).get(i).getCategory() == category){
                        entries.add(new Entry(index , (float)goals.get(index).get(i).getValue()));
                        hasGoal = true;
                        break;
                    }
                }
                if (!hasGoal){
                    entries.add(new Entry(index , 0f));
                }

            }
            LineDataSet set = new LineDataSet(entries, getActivity().getResources().getString(R.string.goal_legend_text));
            set.setColor(Color.rgb(240, 238, 70));
            set.setLineWidth(2.5f);
            set.setCircleColor(Color.rgb(240, 238, 70));
            set.setCircleRadius(5f);
            set.setFillColor(Color.rgb(240, 238, 70));
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setDrawValues(true);
            set.setValueTextSize(10f);
            set.setValueTextColor(Color.rgb(240, 238, 70));

            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            d.addDataSet(set);


            return d;
        }
        public void loadBarChart(List<Double> dataList, BarChart barchart, TextView title) {
            if (!dataList.isEmpty() && dataList.size() == 4 && (dataList.get(0) != 0.0 || dataList.get(1) != 0.0
                    || dataList.get(2) != 0.0 || dataList.get(3) != 0.0)) {
                List<BarEntry> entries1 = new ArrayList<>();
                List<BarEntry> entries2 = new ArrayList<>();
                List<BarEntry> entries3 = new ArrayList<>();
                List<BarEntry> entries4 = new ArrayList<>();

                entries1.add(new BarEntry(0f, dataList.get(0).floatValue()));
                entries2.add(new BarEntry(1f, dataList.get(1).floatValue()));
                entries3.add(new BarEntry(2f, dataList.get(2).floatValue()));
                entries4.add(new BarEntry(3f, dataList.get(3).floatValue()));

                Legend l = barchart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.VERTICAL);
                l.setDrawInside(true);
                //     l.setTypeface(tfLight);
                l.setYOffset(0f);
                l.setXOffset(10f);
                l.setYEntrySpace(0f);
                l.setTextSize(8f);

                XAxis xAxis = barchart.getXAxis();
                //    xAxis.setTypeface(tfLight);
                xAxis.setGranularity(1f);
                xAxis.setCenterAxisLabels(true);
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return String.valueOf((int) value);
                    }
                });

                YAxis leftAxis = barchart.getAxisLeft();
                //      leftAxis.setTypeface(tfLight);
                leftAxis.setValueFormatter(new LargeValueFormatter());
                leftAxis.setDrawGridLines(false);
                leftAxis.setSpaceTop(35f);
                leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                barchart.getAxisRight().setEnabled(false);
                BarDataSet set1 = new BarDataSet(entries1, getActivity()
                        .getResources().getStringArray(R.array.maincategory_array)[1].split(Pattern.quote("("))[0]);
                set1.setColor(Color.BLACK);
                BarDataSet set2 = new BarDataSet(entries2, getActivity()
                        .getResources().getStringArray(R.array.maincategory_array)[2].split(Pattern.quote("("))[0]);
                set2.setColor(Color.RED);
                BarDataSet set3 = new BarDataSet(entries3, getActivity()
                        .getResources().getStringArray(R.array.maincategory_array)[3].split(Pattern.quote("("))[0]);
                set3.setColor(Color.GREEN);
                BarDataSet set4 = new BarDataSet(entries4, getActivity()
                        .getResources().getStringArray(R.array.maincategory_array)[4].split(Pattern.quote("("))[0]);
                set4.setColor(Color.CYAN);

                BarData data = new BarData(set1, set2, set3, set4);
                data.setBarWidth(0.4f); // set custom bar width
                barchart.setData(data);

                barchart.setFitBars(true); // make the x-axis fit exactly all bars
                barchart.setNoDataText("no data");
                //      barchart.setDrawGridBackground(true);
                barchart.animateXY(500, 500);
                barchart.invalidate();
            } else {
                title.setText(title.getText().toString() + ": " + getActivity().getResources().getString(R.string.no_data));
                barchart.setNoDataText(getActivity().getResources().getString(R.string.no_data));
            }
        }

        public List<Double> loadDataByCategories(boolean income, List<Transaction> transactions, List<Installment> installments) {
            List<Double> data = new ArrayList<>();
            Double category1 = 0.0;
            Double category2 = 0.0;
            Double category3 = 0.0;
            Double category4 = 0.0;

            for (Transaction transaction : transactions) {
                if (transaction.isInstallment() == income) {
                    switch (transaction.getCategory()) {
                        case 1:
                            if (transaction.isInstallment()) {
                                category1 += transaction.getEntrance_payment();
                            } else {
                                category1 += transaction.getValue();
                            }
                            break;
                        case 2:
                            if (transaction.isInstallment()) {
                                category2 += transaction.getEntrance_payment();
                            } else {
                                category2 += transaction.getValue();
                            }
                            break;
                        case 3:
                            if (transaction.isInstallment()) {
                                category3 += transaction.getEntrance_payment();
                            } else {
                                category3 += transaction.getValue();
                            }
                            break;
                        case 4:
                            if (transaction.isInstallment()) {
                                category4 += transaction.getEntrance_payment();
                            } else {
                                category4 += transaction.getValue();
                            }
                            break;
                    }
                }
            }

            for (Installment installment : installments) {
                if (installment.isInstallment() == income) {
                    switch (installment.getTransaction().getCategory()) {
                        case 1:
                            category1 += installment.getValue();
                            break;
                        case 2:
                            category2 += installment.getValue();
                            break;
                        case 3:
                            category3 += installment.getValue();
                            break;
                        case 4:
                            category4 += installment.getValue();
                            break;
                    }
                }
            }

            data.add(category1);
            data.add(category2);
            data.add(category3);
            data.add(category4);


            return data;
        }

        public String getMainBalanceByMonth(Date date, List<Transaction> transactions, List<Installment> installments) {
            double balance = 0.0;
            String balanceText = getActivity().getResources().getString(R.string.balance_text);
            Locale localeBR = new Locale("pt", "BR");

            SimpleDateFormat fmt = new SimpleDateFormat("' ' MMMM", localeBR);

            balanceText += fmt.format(date);

            for (Transaction transaction : transactions) {
                if (transaction.isIncome()) {
                    if (!transaction.isInstallment()) {
                        balance += transaction.getValue();
                    } else {
                        balance += transaction.getEntrance_payment();
                    }
                } else {
                    if (!transaction.isInstallment()) {
                        balance -= transaction.getValue();
                    } else {
                        balance -= transaction.getEntrance_payment();
                    }
                }
            }

            for (Installment installment : installments) {
                if (installment.isIncome()) {
                    balance += installment.getValue();
                } else {
                    balance -= installment.getValue();
                }
            }

            balanceText += ": R$ " + newFormat.format(balance);

            return balanceText;

        }

        List<List<Transaction>> loadTransactionsByPeriod( int period, int year){
            List<List<Transaction>> transactions = new ArrayList<>();

            if(period == 1){
                for(int i=0; i < 6; i++){
                    Calendar c = Calendar.getInstance();
                    c.set(year, i, 1);
                    transactions.add(db.findTransactionsByDateOneMonth(c.getTime()));
                }
            }else{
                for(int i=6; i < 12; i++){
                    Calendar c = Calendar.getInstance();
                    c.set(year, i, 1);
                    transactions.add(db.findTransactionsByDateOneMonth(c.getTime()));
                }
            }


            return transactions;

        }
        List<List<Installment>> loadInstallmentssByPeriod( int period, int year){
            List<List<Installment>> installments = new ArrayList<>();

            if(period == 1){
                for(int i=0; i < 6; i++){
                    Calendar c = Calendar.getInstance();
                    c.set(year, i, 1);
                    installments.add(db.findInstallmentByDateOneMonth(c.getTime()));
                }
            }else{
                for(int i=6; i < 12; i++){
                    Calendar c = Calendar.getInstance();
                    c.set(year, i, 1);
                    installments.add(db.findInstallmentByDateOneMonth(c.getTime()));
                }
            }

            return installments;
        }
        List<List<Goal>> loadGoalsByPeriod( int period, int year){
            List<List<Goal>> goals = new ArrayList<>();

            if(period == 1){
                for(int i=0; i < 6; i++){
                    Calendar c = Calendar.getInstance();
                    c.set(year, i, 1);
                    goals.add(db.findGoalsByDateOneMonth(c.getTime()));
                }
            }else{
                for(int i=6; i < 12; i++){
                    Calendar c = Calendar.getInstance();
                    c.set(year, i, 1);
                    goals.add(db.findGoalsByDateOneMonth(c.getTime()));
                }
            }

            return goals;
        }

        String getPeriodText(int period, int year){
            String text = "";
            Locale localeBR = new Locale("pt", "BR");
            SimpleDateFormat fmt = new SimpleDateFormat("' ' MMMM", localeBR);

            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();

            if(period  == 1){
                c1.set(year, 0, 1);
                c2.set(year, 6, 1);
                c2.add(Calendar.DAY_OF_MONTH, -1);

            }else{
                c1.set(year, 6, 1);
                c2.set(year + 1, 0, 1);
                c2.add(Calendar.DAY_OF_MONTH, -1);
            }
            String firstdate = StringUtils.capitalize(fmt.format(c1.getTime()));
            String lastdate = StringUtils.capitalize(fmt.format(c2.getTime()));

            text = firstdate  + " " +
                    getActivity().getResources().getString(R.string.report_title_part2)
                    + lastdate;


            return text;
        }
    }
}