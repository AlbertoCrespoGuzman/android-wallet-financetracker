package ta.na.mao.activities.fragments.FinancialControl;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.ChartElement;
import ta.na.mao.database.models.DatePager;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;
import ta.na.mao.utils.charts.MyMarkerView;


public class FragmentFinancialControlCharts extends Fragment {
    View view;
    List<DatePager> items = new ArrayList<>();
    DecimalFormat newFormat = new DecimalFormat("0.00");
    private DecimalFormat mFormat = new DecimalFormat("###,###,##0.00");
    boolean filter = true;
    Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loadTabs();
        view = inflater.inflate(R.layout.fragment_financial_controll_statements, container, false);
        FragmentFinancialControlCharts.DemoYearsPagerAdapter adapter = new FragmentFinancialControlCharts.DemoYearsPagerAdapter(items);
        adapter.addAll(items);

        ViewPager viewPager = view.findViewById(R.id.fragment_financialcontrol_statement_viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentItem());

        RecyclerTabLayout recyclerTabLayout = view.findViewById(R.id.recycler_tab_layout);
        recyclerTabLayout.setUpWithViewPager(viewPager);
        activity = getActivity();
        return view;

    }

    public void loadTabs(){

        Calendar c = Calendar.getInstance();
        c.set(2017, 7, 1);
        for(int i=0; i< 100 ; i++){
            c.add(Calendar.MONTH, + 1);
            items.add(new DatePager(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)));
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
    public void onResume(){
        super.onResume();
        if(getActivity() instanceof FinancialControlMainActivity){
            if(getActivity() != null){
                ((FinancialControlMainActivity) getActivity()).changeTopBarTitle(Defaultdata.TopBarTitle.STATS);
            }
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        //    getActivity().setTitle("Menu 1");
    }


    public class DemoYearsPagerAdapter extends PagerAdapter implements StatementRecyclerAdapter.ItemClickListener  {

        private List<DatePager> items = new ArrayList<>();
        DatabaseManager db;
        public DemoYearsPagerAdapter( List<DatePager> items ) {
            db = new DatabaseManager(getActivity());
            //     this.items = items;

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.fragment_financialcontrol_charts_content, container, false);

//            TextView text = view.findViewById(R.id.title);
//            text.setText(items.get(position));


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
                        loadCharts(view, positionFinal);
                        filterLayout.setBackground(
                                getContext().getResources().getDrawable(R.drawable.filter_future_checked_layout));
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_checked));
                        }else{
                            filterImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_checkbox_checked_png));
                        }
                    }else{
                        loadCharts(view, positionFinal);
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




            loadCharts(view, position);

            container.addView(view);
            return view;
        }
        void loadCharts(View view, int position){
            Calendar c = Calendar.getInstance();

            int month = items.get(position).getMonth();
            int year = items.get(position).getYear();

            month = month == 1 ? 12 : month - 1;
            year = month == 12 ? year - 1  : year;



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

            TextView balanceText1 = view.findViewById(R.id.fragment_financialcontrol_charts_content_balance_text_1);
            TextView balanceText2 = view.findViewById(R.id.fragment_financialcontrol_charts_content_balance_text_2);
            balanceText1.setText(Utils.getMonth(c.getTime(), getContext()));
            balanceText2.setText(Utils.getBalanceText( transactions, installments, getContext(), mFormat));

            PieChart balanceChart = view.findViewById(R.id.fragment_financialcontrol_charts_content_balance_chart);
            List<ChartElement> balanceChartElements = loadDataByIncomesOutgo(transactions, installments);

            Utils.loadPieChartBalance(balanceChart, getActivity(),
                    balanceChartElements.get(0).getValue(),
                    balanceChartElements.get(1).getValue());

            TextView outGoText = view.findViewById(R.id.fragment_financialcontrol_charts_content_outgo_title);
            TextView incomeText = view.findViewById(R.id.fragment_financialcontrol_charts_content_income_title);

            TextView outGoZeroText = view.findViewById(R.id.fragment_financialcontrol_charts_content_outgo_value_zero);
            TextView incomeZeroText = view.findViewById(R.id.fragment_financialcontrol_charts_content_income_value_zero);
            BarChart outgoChart = view.findViewById(R.id.fragment_financialcontrol_charts_content_outgo_chart);
            BarChart incomeChart = view.findViewById(R.id.fragment_financialcontrol_charts_content_income_chart);

            PieChart outgoPieChart = view.findViewById(R.id.fragment_financialcontrol_charts_content_outgo_pie_chart);
            PieChart incomePieChart = view.findViewById(R.id.fragment_financialcontrol_charts_content_income_pie_chart);

            List<ChartElement> dataOutGo = loadDataByCategories(false, transactions, installments);
            List<ChartElement> dataIncome = loadDataByCategories(true, transactions, installments);


            loadBarChart(dataOutGo, outgoChart, outgoPieChart, outGoText, false, outGoZeroText);
            loadBarChart(dataIncome, incomeChart, incomePieChart, incomeText, true, incomeZeroText);

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
        public void loadBarChart( List<ChartElement> dataList, BarChart barchart,
                                  PieChart pieChart, TextView title, boolean income, TextView zeroValues){

            Typeface font= Typeface.createFromAsset(getContext().getAssets(), "fonts/title.otf");


            String[] allColors = getActivity().getResources().getStringArray(R.array.chart_colors);

            String zeroCategoriesText = "";

            boolean hasData = false;
            for(ChartElement chartElement : dataList){
                if(chartElement.getValue() != 0.0){
                    hasData = true;
                    break;
                }
            }
            ArrayList<Integer> colors = new ArrayList<>();

            for(String color : allColors){
                colors.add(Color.parseColor(color));
            }

            if(hasData) {

                /*
    ---------------------------------                    LEGEND AND DESGIN BAR CHART !!!!!!!
                 */

           /*     Legend l = barchart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.VERTICAL);
                l.setDrawInside(false);

                //     l.setTypeface(tfLight);
                l.setYOffset(0f);
                l.setXOffset(10f);
                l.setYEntrySpace(0f);
                l.setTextSize(8f);
*/
                Double totalValue = 0.0;
                for(ChartElement chartElement : dataList){
                    totalValue += chartElement.getValue();
                }
                if(income){
                    title.setText("Receitas: R$ " + mFormat.format(totalValue));
                }else{
                    title.setText("Gastos: R$ " + mFormat.format(totalValue));
                }


                int numberCategories = 0;

                for(ChartElement chartElement : dataList){
                    if(chartElement.getValue() > 0){
                        numberCategories ++;
                    }
                }
                if(numberCategories > 4) {
                    Legend l = barchart.getLegend();
                    l.setWordWrapEnabled(true);
                    l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    l.setDrawInside(false);
                    l.setTypeface(font);
                    l.setTextSize(12);
                    l.setWordWrapEnabled(true);
                    l.setYEntrySpace(0f);
                    if(numberCategories > 5 && numberCategories < 10){
                        l.setYOffset(-0.4f);
                    }else if(numberCategories > 10){
                        l.setYOffset(-0.2f);
                    }


                    XAxis xAxis = barchart.getXAxis();
                    //    xAxis.setTypeface(tfLight);
                    xAxis.setGranularity(1f);
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return "R$ " + String.valueOf((int) value);
                        }
                    });

                    YAxis leftAxis = barchart.getAxisLeft();
                    //      leftAxis.setTypeface(tfLight);
                    leftAxis.setValueFormatter(new LargeValueFormatter());
                    leftAxis.setDrawGridLines(false);
                    leftAxis.setSpaceTop(0f);
                    leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
                    float maxValue = 0f;
                    for (ChartElement chartElement : dataList) {
                        if (chartElement.getValue() > maxValue) maxValue = ((Double)chartElement.getValue()).floatValue();
                    }
                    leftAxis.setAxisMaximum(((Double)(maxValue * 1.1)).floatValue());
                    barchart.getDescription().setEnabled(false);
                    barchart.getAxisRight().setEnabled(false);
                    barchart.getAxisLeft().setEnabled(false);
                    barchart.getXAxis().setEnabled(false);

                /*
   ---------------------------------                     DATA BAR CHART !!!!!!!
                 */

                    List<BarDataSet> barDataSets = new ArrayList<>();


                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getValue() > 0) {
                            List<BarEntry> entries = new ArrayList<>();
                            entries.add(new BarEntry(i + 0f, ((Double)dataList.get(i).getValue()).floatValue()));
                            BarDataSet set = new BarDataSet(entries, dataList.get(i).getLabel());
                                set.setColor(Color.parseColor(allColors[i]));
                            //    set.setColors(colors);
                                barDataSets.add(set);

                        }
                    }

                    BarData data = null;

                    switch (barDataSets.size()) {
                        case 1:
                            data = new BarData(barDataSets.get(0));
                            break;
                        case 2:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1));
                            break;
                        case 3:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2));
                            break;
                        case 4:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3));
                            break;
                        case 5:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4));
                            break;
                        case 6:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5));
                            break;
                        case 7:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6));
                            break;
                        case 8:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6),
                                    barDataSets.get(7));
                            break;
                        case 9:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6),
                                    barDataSets.get(7), barDataSets.get(8));
                            break;
                        case 10:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6),
                                    barDataSets.get(7), barDataSets.get(8), barDataSets.get(9));
                            break;
                        case 11:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6),
                                    barDataSets.get(7), barDataSets.get(8), barDataSets.get(9), barDataSets.get(10));
                            break;
                        case 12:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6),
                                    barDataSets.get(7), barDataSets.get(8), barDataSets.get(9), barDataSets.get(10),
                                    barDataSets.get(11));
                            break;
                        case 13:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6),
                                    barDataSets.get(7), barDataSets.get(8), barDataSets.get(9), barDataSets.get(10),
                                    barDataSets.get(11), barDataSets.get(12));
                            break;
                        case 14:
                            data = new BarData(barDataSets.get(0), barDataSets.get(1), barDataSets.get(2),
                                    barDataSets.get(3), barDataSets.get(4), barDataSets.get(5), barDataSets.get(6),
                                    barDataSets.get(7), barDataSets.get(8), barDataSets.get(9), barDataSets.get(10),
                                    barDataSets.get(11), barDataSets.get(12), barDataSets.get(13));
                            break;
                    }

                    if (dataList.size() == 1 || dataList.size() == 2) {
                        data.setBarWidth(0.3f);
                    } else {
                        data.setBarWidth(1f); // set custom bar width
                    }
                    data.setValueFormatter(new IValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler){
                            return "R$ " + mFormat.format(value);
                        }
                    });
                    data.setValueTypeface(font);
                    data.setValueTextSize(13);
                    barchart.setData(data);

                    barchart.setFitBars(true); // make the x-axis fit exactly all bars
                    barchart.setNoDataText("sem dados");
                    //      barchart.setDrawGridBackground(true);
                    barchart.animateXY(1400, 1400);
                    //       barchart.setViewPortOffsets(-40f, 0f, 0f, 0f);
                    barchart.setViewPortOffsets(-20,30,-20,90);
                    barchart.setVisibility(View.VISIBLE);
                    barchart.setExtraBottomOffset(-1f);
                    pieChart.setVisibility(View.GONE);


                    barchart.setMarker(new MyMarkerView(activity, R.layout.marker_chart_popup));
                    barchart.setHighlightPerTapEnabled(true);

                    final BarChart barchartFinal = barchart;
                    barchart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {

                        @Override
                        public void onValueSelected(Entry e, Highlight h) {

                            barchartFinal.getMarker().refreshContent(e, h);
                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });

                    barchart.setClickable(true);

                    barchart.invalidate();


                }else{


                    Legend l = pieChart.getLegend();
                    l.setWordWrapEnabled(true);
                    l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                    l.setOrientation(Legend.LegendOrientation.VERTICAL);
                    l.setDrawInside(false);
                    l.setTypeface(font);
                    l.setTextSize(15);
                    l.setYOffset(-21);
                    //    xAxis.setTypeface(tfLight);




                    pieChart.getDescription().setEnabled(false);



                    float maxValue = 0f;
                    for (ChartElement chartElement: dataList) {
                        if (chartElement.getValue() > maxValue) maxValue = ((Double)chartElement.getValue()).floatValue();
                    }

                    pieChart.getDescription().setEnabled(false);

                /*
   ---------------------------------                     DATA BAR CHART !!!!!!!
                 */

                    List<PieEntry> entries = new ArrayList<>();

                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getValue() > 0) {
                                entries.add(new PieEntry(((Double)dataList.get(i).getValue()).floatValue()
                                        , dataList.get(i).getLabel()));
                        }
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setSliceSpace(0f);
                    dataSet.setSelectionShift(0f);


                    dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                    dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


                    dataSet.setValueTextColor(Color.BLACK);

                    dataSet.setColors(colors);
                    dataSet.setValueTextColor(Color.BLACK);
                    //dataSet.setValueFormatter(new PieChartFormat());
                    dataSet.setValueFormatter(new PercentFormatter());
                    dataSet.setValueTypeface(font);
                    dataSet.setValueTextSize(15);
                    dataSet.setValueLineWidth(0);
                    PieData pieData = new PieData(dataSet);



                    pieData.setValueTextSize(15);
                    pieData.setValueTypeface(font);
                    pieData.setValueTextColor(Color.BLACK);
                    pieChart.setData(pieData);

                    pieChart.setNoDataText("sem dados");
                    //      barchart.setDrawGridBackground(true);
                    pieChart.animateXY(1000, 1000);
                    //       barchart.setViewPortOffsets(-40f, 0f, 0f, 0f);
                    pieChart.setDrawEntryLabels(false);
                    pieChart.setUsePercentValues(true);
                    pieChart.setEntryLabelColor(Color.BLACK);
                    pieChart.setVisibility(View.VISIBLE);
                    pieChart.setExtraBottomOffset(30);



                    pieChart.setHoleColor(getContext().getResources().getColor(R.color.colorPrimary));
                    pieChart.setMarker(new MyMarkerView(activity, R.layout.marker_chart_popup));
                    pieChart.setHighlightPerTapEnabled(true);

                    final PieChart pieChartFinal = pieChart;
                    pieChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {

                        @Override
                        public void onValueSelected(Entry e, Highlight h) {

                            pieChartFinal.getMarker().refreshContent(e, h);
                        }

                        @Override
                        public void onNothingSelected() {

                        }
                    });

                    pieChart.setClickable(true);

                    pieChart.invalidate();
                }
                List<Integer> categoriesNotZero = new ArrayList<>();

                for(ChartElement chartElement : dataList){
                    categoriesNotZero.add(chartElement.getCategory());
                }

                List<String> allCategories = new LinkedList<String>(Arrays.asList(income ? getActivity().getResources().getStringArray(R.array.statement_income_categories_array) :
                        getActivity().getResources().getStringArray(R.array.statement_outgo_categories_array)));
                Log.e("allCategories","allCategories = " + allCategories.toString());
                allCategories.remove(0);

                for(int i=0; i < allCategories.size(); i++){
                    allCategories.set(i,(allCategories.get(i).split(Pattern.quote("("))[0]));
                }
                for(ChartElement chartElement : dataList){
                    allCategories.remove(chartElement.getLabel());
                }
                for(int i=0; i < allCategories.size();i++){
                   zeroCategoriesText +=  (i == allCategories.size() - 1 ?
                           ((allCategories.size() != 1) ?"e " +allCategories.get(i) :allCategories.get(i)) : allCategories.get(i) + ", ");
                }
                if(zeroCategoriesText == null || zeroCategoriesText.equals("") || zeroCategoriesText.length() < 2){
                    zeroValues.setText("");
                }else{
                    zeroValues.setText("*Os valores de " + zeroCategoriesText + " estão zerados. Por isso não estão exibidos no gráfico acima.");
                }



            }else{
                title.setText(title.getText().toString() + ": " + getActivity().getResources().getString(R.string.no_data) );
                barchart.setNoDataText(getActivity().getResources().getString(R.string.no_data));
            }
        }
        public List<ChartElement> loadDataByIncomesOutgo(List<Transaction> transactions, List<Installment> installments){

            List<ChartElement> chartElements = new ArrayList<>();

            ChartElement chartElementIncome = new ChartElement(
                    getContext().getResources().getString(R.string.income_text),0, 0);
            ChartElement chartElementOutgo = new ChartElement(
                    getContext().getResources().getString(R.string.outgo_text),1, 0);

            for(Transaction transaction: transactions){
                if(transaction.isInstallment()){
                    if(transaction.isIncome()){
                        chartElementIncome.setValue(chartElementIncome.getValue() + transaction.getEntrance_payment());
                    }else{
                        chartElementOutgo.setValue(chartElementOutgo.getValue() + transaction.getEntrance_payment());
                    }
                }else{
                    if(transaction.isIncome()){
                        chartElementIncome.setValue(chartElementIncome.getValue() + transaction.getValue());
                    }else{
                        chartElementOutgo.setValue(chartElementOutgo.getValue() + transaction.getValue());
                    }
                }
             }

             for(Installment installment : installments){
                if(installment.isIncome()){
                    chartElementIncome.setValue(chartElementIncome.getValue() + installment.getValue());
                }else{
                    chartElementOutgo.setValue(chartElementOutgo.getValue() + installment.getValue());
                }
             }

             chartElements.add(chartElementIncome);
             chartElements.add(chartElementOutgo);

             return chartElements;
        }
        public List<ChartElement> loadDataByCategories(boolean income, List<Transaction> transactions, List<Installment> installments){


            List<ChartElement> chartElements = new ArrayList<>();

            for(Transaction transaction : transactions){
                if(transaction.isIncome() == income){
                    boolean categoryAlreadyExists = false;
                    for(ChartElement chartElement : chartElements){
                        if(transaction.getCategory() == chartElement.getCategory()){
                            if(transaction.isInstallment()) {
                                chartElement.setValue(chartElement.getValue() + transaction.getEntrance_payment());
                            }else{
                                chartElement.setValue(chartElement.getValue() + transaction.getValue());
                            }
                            categoryAlreadyExists = true;
                        }
                    }
                    if(!categoryAlreadyExists){
                        if(transaction.isInstallment()) {
                            chartElements.add(new ChartElement(transaction.getCategoryText(getActivity()),
                                    transaction.getCategory(), transaction.getEntrance_payment()));
                        }else{
                            chartElements.add(new ChartElement(transaction.getCategoryText(getActivity()),
                                    transaction.getCategory(), transaction.getValue()));
                        }
                    }
                }
            }

            for(Installment installment : installments){
                if(installment.isIncome() == income){
                    boolean categoryAlreadyExists = false;
                    for(ChartElement chartElement : chartElements){
                        if(installment.getTransaction().getCategory() == chartElement.getCategory()){
                            chartElement.setValue(chartElement.getValue() + installment.getValue());
                            categoryAlreadyExists = true;
                        }
                    }
                    if(!categoryAlreadyExists){
                        chartElements.add(new ChartElement(installment.getTransaction().getCategoryText(getActivity()),
                                installment.getTransaction().getCategory(), installment.getValue()));
                    }
                }
            }

            return chartElements;
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

            balanceText += ": R$ " + mFormat.format(balance);

            return balanceText;

        }
    }


}