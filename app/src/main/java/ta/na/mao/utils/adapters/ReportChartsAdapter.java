package ta.na.mao.utils.adapters;


    import android.content.Context;
    import android.graphics.Color;
    import android.graphics.Typeface;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import com.github.mikephil.charting.charts.BarChart;
    import com.github.mikephil.charting.charts.CombinedChart;
    import com.github.mikephil.charting.charts.PieChart;
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
    import com.github.mikephil.charting.formatter.IValueFormatter;
    import com.github.mikephil.charting.highlight.Highlight;
    import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
    import com.github.mikephil.charting.utils.ViewPortHandler;

    import java.text.DecimalFormat;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.List;

    import androidx.recyclerview.widget.RecyclerView;
    import ta.na.mao.R;
    import ta.na.mao.database.manager.DatabaseManager;
    import ta.na.mao.database.models.Goal;
    import ta.na.mao.database.models.Installment;
    import ta.na.mao.database.models.ReportCombinedDataSets;
    import ta.na.mao.database.models.ReportElement;
    import ta.na.mao.database.models.Transaction;
    import ta.na.mao.utils.charts.MyMarkerView;

public class ReportChartsAdapter extends RecyclerView.Adapter<ReportChartsAdapter.ViewHolder> {

    private List<ReportElement> reportElements = new ArrayList<>();
    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    SimpleDateFormat format;
    Context context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("0.00");
    DatabaseManager db;
    String[] months1 = {"Jan","Fev", "Mar", "Abr","Mai","Jun"};
    String[] months2 = {"Jul","Ago", "Set", "Out","Nov","Dez"};
    private DecimalFormat mFormat = new DecimalFormat("###,###,##0.00");


    public ReportChartsAdapter(Context context, List<ReportElement> reportElements) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        format = new SimpleDateFormat("dd/MM/yyyy");
        db = new DatabaseManager(context);
        this.reportElements = reportElements;


    }

    @Override
    public ReportChartsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_report_element, parent, false);
        return new ReportChartsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReportChartsAdapter.ViewHolder holder, int position) {
        ReportElement reportElement = reportElements.get(position);

        holder.title.setText((reportElement.isIncome() ? "Receita " : "Gastos ") + reportElement.getLabel());
        loadCombinedChart(reportElement, holder.chart);
    }

    @Override
    public int getItemCount() {
        return reportElements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView title;
        final CombinedChart chart;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.adapter_report_title);
            chart = itemView.findViewById(R.id.adapter_report_chart);
            //    itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        return reportElements.get(id);
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void loadCombinedChart(ReportElement reportElement, final CombinedChart chart){
        // ADDING TRANSACTIONS WITH INSTALLMENTS BY MONTH
        int main_category = 0;

        if(reportElement.isIncome()){
            main_category = context.getResources().getStringArray(R.array.goal_income_categories_array).length -1;
        }else{
            main_category = context.getResources().getStringArray(R.array.goal_outgo_categories_array).length - 1;
        }

        Log.e("loadCombinedChart","reportElement -> " + reportElement.getLabel());
        Log.e("loadCombinedChart","reportElement -> " + reportElement.getGoal_category());


        boolean income = reportElement.isIncome();
        List<List<Transaction>> transactions = reportElement.getTransactions();
        List<List<Installment>> installments = reportElement.getInstallments();
        List<List<Goal>> goals = reportElement.getGoals();
        int goal_category = reportElement.getGoal_category();
        int period = reportElement.getPeriod();

        List<Double> statementsByMonth =  new ArrayList<>();

        for(int i =0; i < transactions.size(); i++){
            Double sum = 0.0;
            for(int j=0; j < transactions.get(i).size(); j++){
                if(transactions.get(i).get(j).isIncome() == income){
                    if(goal_category == main_category){
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
                    if(goal_category == main_category){
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
        Log.e("statementsByMonth","statementsByMonth-> first month = " + statementsByMonth.get(0));

        Typeface font= Typeface.createFromAsset(context.getAssets(), "fonts/title.otf");

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
        l.setTypeface(font);
        l.setTextSize(15);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);






        CombinedData data = new CombinedData();
        final ReportCombinedDataSets reportCombinedDataSets = new
                ReportCombinedDataSets(context, income, goal_category, period == 1 ? months1 : months2);

        reportCombinedDataSets.setBarData(generateBarData(statementsByMonth, income));
        reportCombinedDataSets.setLineData(generateLineData(goals, goal_category, income));

     //   reportCombinedDataSets = removingZeroMonths(reportCombinedDataSets);

        reportCombinedDataSets.printDetails();


            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

            xAxis.setAxisMinimum(-0.25f);
            xAxis.setGranularity(1f);
            data.setData(reportCombinedDataSets.getLineData());
            data.setData(reportCombinedDataSets.getBarData());

            Log.e("data", "data data.getEntryCount() = " + data.getEntryCount());

            if (period == 1) {
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        //  return months1[(int) value % months1.length];
                        if (reportCombinedDataSets.getMonths().length > 0) {
                            return reportCombinedDataSets.getMonths()[(int) value
                                    % reportCombinedDataSets.getMonths().length];
                        } else {
                            return months1[(int) value % months1.length];
                        }

                    }
                });
            } else {
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        //   return months2[(int) value % months2.length];
                        if (reportCombinedDataSets.getMonths().length > 0) {
                            return reportCombinedDataSets.getMonths()[(int) value
                                    % reportCombinedDataSets.getMonths().length];
                        } else {
                            return months2[(int) value % months1.length];
                        }
                    }
                });
            }
            xAxis.setAxisLineColor(context.getResources().getColor(R.color.white));
            xAxis.setTextColor(context.getResources().getColor(R.color.white));
            xAxis.setTypeface(font);
            xAxis.setTextSize(13);
            xAxis.setDrawAxisLine(false);


            data.setValueTypeface(font);
            data.setValueTextSize(13);
            data.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return "R$ " + mFormat.format(value);
                }
            });
            //  data.setValueTypeface(tfLight);
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            leftAxis.setEnabled(true);
            leftAxis.setAxisMaximum(data.getYMax() + ((Double) (data.getYMax() * 0.10)).intValue());
            leftAxis.setTextColor(context.getResources().getColor(R.color.white));
            leftAxis.setAxisLineColor(context.getResources().getColor(R.color.white));
            leftAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return "R$ " + mFormat.format(value);
                }
            });
            xAxis.setAxisMaximum(data.getXMax() + 0.25f);
            xAxis.setTextColor(context.getResources().getColor(R.color.white));
            xAxis.setAxisLineColor(context.getResources().getColor(R.color.white));
            chart.animateXY(1400, 1400);
            if(reportCombinedDataSets.getBarData().getDataSets().get(0).getEntryCount() > 0){
                chart.setData(data);
            }else{
                chart.setNoDataTextColor(R.color.white);
                chart.setNoDataText("Sem dados para este periodo");
                if(chart.getBarData() != null){
                    for(int i=0; i< chart.getBarData().getDataSetCount(); i++){
                        chart.getBarData().removeDataSet(i);
                    }
                }
                if(chart.getLineData()!= null){
                    for(int i=0; i < chart.getLineData().getDataSetCount(); i++){
                        chart.getLineData().removeDataSet(i);
                    }

                }
                xAxis.setEnabled(false);
                leftAxis.setEnabled(false);
                chart.getLegend().setEnabled(false);


            }
            chart.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));


            chart.setMarker(new MyMarkerView(context, R.layout.marker_chart_popup));
            chart.setHighlightPerTapEnabled(true);

            final CombinedChart chartFinal = chart;
            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

                @Override
                public void onValueSelected(Entry e, Highlight h) {

                    chartFinal.getMarker().refreshContent(e, h);
                }

                @Override
                public void onNothingSelected() {

                }
            });

            chart.setExtraTopOffset(5f);
            chart.setClickable(true);

            chart.invalidate();

    }
    ReportCombinedDataSets removingZeroMonths (ReportCombinedDataSets reportCombinedDataSets){


        List<Integer> indexToRemove = new ArrayList<>();

        for(int i=0; i < reportCombinedDataSets.getBarData().getDataSets().get(0).getEntryCount(); i++ ){

            if(reportCombinedDataSets.getBarData().getDataSets().
                    get(0).getEntriesForXValue(i).get(0).getY() == 0.0){
                indexToRemove.add(i);
            }
        }
        for(int i=0;i<indexToRemove.size(); i++){
            reportCombinedDataSets.getBarData().getDataSets().get(0).removeEntryByXValue(0.0f);

            reportCombinedDataSets.getLineData().getDataSets().get(0).removeEntryByXValue(0.0f);
        }
        return reportCombinedDataSets;
    }
    public BarData generateBarData(List<Double> data, boolean income){
        ArrayList<BarEntry> entries1 = new ArrayList<>();

        for (int index = 0; index < data.size(); index++) {
            entries1.add(new BarEntry(0 + index, data.get(index).floatValue()));
        }

        BarDataSet set1 = new BarDataSet(entries1, income ?
                context.getResources().getString(R.string.income_text) :
                context.getResources().getString(R.string.outgo_text));
        if(income){
            set1.setColor(context.getResources().getColor(R.color.income_green));
            set1.setValueTextColor(context.getResources().getColor(R.color.black));
        }else{
            set1.setColor(context.getResources().getColor(R.color.outgo_red));
            set1.setValueTextColor(context.getResources().getColor(R.color.black));
        }

        set1.setValueTextSize(10f);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.50f; // x2 dataset

        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);

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
        LineDataSet set = new LineDataSet(entries, context.getResources().getString(R.string.goal_legend_text));
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setHighlightEnabled(true);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);


        return d;
    }

}