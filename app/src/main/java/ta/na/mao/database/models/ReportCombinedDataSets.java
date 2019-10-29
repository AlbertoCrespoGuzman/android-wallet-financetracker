package ta.na.mao.database.models;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ta.na.mao.R;

public class ReportCombinedDataSets {
    BarData barData;
    LineData lineData;
    String[] months;
    Context context;
    boolean income;
    int category;

    public ReportCombinedDataSets(Context context, boolean income, int category, String[] months){
        this.context = context;
        this.income = income;
        this.category = category;
        this.months = months;
    }

    public ReportCombinedDataSets(BarData barData, LineData lineData, String[] months){
        this.barData = barData;
        this.lineData = lineData;
        this.months = months;

    }

    public BarData getBarData() {
        return barData;
    }

    public void setBarData(BarData barData) {
        this.barData = barData;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
    }

    public String[] getMonths() {
        return months;
    }

    public void setMonths(String[] months) {
        this.months = months;
    }

    @Override
    public String toString() {
        return "ReportCombinedDataSets{" +
                "barData=" + barData +
                ", lineData=" + lineData +
                '}';
    }
    public void printDetails(){
        removeZeroColumns();
    }

    public void removeZeroColumns(){
        List<Boolean> hasToRemove = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();

        hasToRemove.addAll(Arrays.asList(false, false, false, false, false, false));

        for(int i=0; i < getBarData().getDataSets().get(0).getEntryCount(); i++ ){
            if(getBarData().getDataSets().
                    get(0).getEntriesForXValue(i).get(0).getY() == 0.0){
                hasToRemove.set(i, true);
            }else{
                hasToRemove.set(i, false);
                break;
            }
        }
        for(int i=0; i < getLineData().getDataSets().get(0).getEntryCount(); i++ ){
            if(getLineData().getDataSets().
                    get(0).getEntriesForXValue(i).get(0).getY() == 0.0 && hasToRemove.get(i)){
                hasToRemove.set(i, true);
            }else{
                hasToRemove.set(i, false);
            }
        }

        BarData barDataRemoved = new BarData();
        LineData lineDataRemoved = new LineData();


        for(int i = 0; i < hasToRemove.size(); i++){
            if(hasToRemove.get(i)){
                getBarData().getDataSets().get(0).removeFirst();
                getLineData().getDataSets().get(0).removeFirst();
                String[] modifiedArray = Arrays.copyOfRange(months, 1, months.length);
                months = null;
                months = modifiedArray;
            }else{
                break;
            }
        }
        /*
        barData = barDataRemoved;
        lineData = lineDataRemoved;

        barData.addDataSet(barDataRemoved.getDataSetByIndex(0));
        lineData.addDataSet(lineDataRemoved.getDataSetByIndex(0));

        */
        List<Double> dataForBar = new ArrayList<>();
        for(int i=0; i < getBarData().getDataSets().get(0).getEntryCount(); i++){
            dataForBar.add(((Float)getBarData().getDataSets().get(0).getEntryForIndex(i).getY()).doubleValue());
        }
        setBarData(generateBarData(dataForBar));

        List<Double> dataForLine = new ArrayList<>();
        for(int i=0; i < getLineData().getDataSets().get(0).getEntryCount(); i++){
            dataForLine.add(((Float)getLineData().getDataSets().get(0).getEntryForIndex(i).getY()).doubleValue());
        }
        setLineData(generateLineData(dataForLine));

    }
    public BarData generateBarData(List<Double> data){
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
        float barWidth = 0.50f;
        if(data.size() == 1){
            barWidth = 0.1f;
        }

        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);

        return d;
    }
    public LineData generateLineData(List<Double> data){

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < data.size(); index++) {
            entries.add(new Entry(0 + index, data.get(index).floatValue()));
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
