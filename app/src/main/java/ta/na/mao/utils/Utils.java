package ta.na.mao.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.activities.MainActivity;
import ta.na.mao.activities.SplashActivity;
import ta.na.mao.activities.UserDetailsActivity;
import ta.na.mao.activities.UserDetailsOldActivity;
import ta.na.mao.communications.Svc;
import ta.na.mao.communications.SvcApi;
import ta.na.mao.communications.http.auth.ResponseMessage;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.StatementElement;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.database.models.UserDetails;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.utils.adapters.PopupMenuAdapter;
import ta.na.mao.utils.charts.MyMarkerView;
import ta.na.mao.utils.dialogs.ProgressCustomDialog;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {

    public static String  getBalanceText(List<Transaction> transactions, List<Installment> installments
            , Context context, DecimalFormat newFormat){
        double balance = 0.0;
        String balanceText = context.getResources().getString(R.string.currenry_br) + " ";


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

        balanceText += newFormat.format(balance);

        return balanceText;

    }

    public static String getMonth(Date date, Context context){
        String month = context.getResources().getString(R.string.currency_text);

        Locale localeBR = new Locale("pt", "BR");

        SimpleDateFormat fmt = new SimpleDateFormat("' ' MMMM", localeBR);

        month += fmt.format(date) + ": ";

        return month;

    }
    public static List<StatementElement> sortStatementElements(List<Transaction> transactions, List<Installment> installments){
        List<StatementElement> statementElements = new ArrayList<>();

        for(Transaction transaction : transactions){
            statementElements.add(new StatementElement(transaction, null));
        }
        for(Installment installment : installments){
            statementElements.add(new StatementElement(null, installment));
        }


        Collections.sort(statementElements, new Comparator<StatementElement>() {
            @Override
            public int compare(final StatementElement o1, final StatementElement o2) {
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });

        return statementElements;
    }
    public static String getDate3LettersMonthFormatNoYear(Date date){
        Locale localeBR = new Locale("pt", "BR");

        SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM", localeBR);

        String dateText = fmt.format(date);
        String month = dateText.split(Pattern.quote(" "))[1];

        month = month.substring(0,3);


        return dateText.split(Pattern.quote(" "))[0] +
                " " + month;
    }
    public static String getDate3LettersMonthFormat(Date date){
        Locale localeBR = new Locale("pt", "BR");

        SimpleDateFormat fmt;

        fmt  = new SimpleDateFormat("dd MMMM", localeBR);

        String dateText = fmt.format(date);
        String month = dateText.split(Pattern.quote(" "))[1];

        month = month.substring(0,3);


            return dateText.split(Pattern.quote(" "))[0] +
                    " " + month;

    }
    public static String getDate3LettersMonthAndYear(int month, int year){


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        Date date = calendar.getTime();

        Locale localeBR = new Locale("pt", "BR");

        SimpleDateFormat fmt;

        fmt  = new SimpleDateFormat("MMMM yyyy", localeBR);

        String dateText = fmt.format(date);
        String monthS = dateText.split(Pattern.quote(" "))[0];

        monthS = monthS.substring(0,3);


        return  monthS + " " + dateText.split(Pattern.quote(" "))[1];


    }
    public static void loadPieChartBalance(PieChart pieChart, Context context, Double income, Double outgo){
        Typeface font= Typeface.createFromAsset(context.getAssets(), "fonts/title.otf");


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


        List<PieEntry> entries = new ArrayList<>();


         entries.add(new PieEntry((income).floatValue()
                        , context.getResources().getString(R.string.income_text_2)));
        entries.add(new PieEntry((outgo).floatValue()
                , context.getResources().getString(R.string.outgo_text_2)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


        dataSet.setValueTextColor(Color.BLACK);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(context.getResources().getColor(R.color.income_green));
        colors.add(context.getResources().getColor(R.color.outgo_red));

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
        pieChart.setExtraBottomOffset(25);


        pieChart.setHoleColor(context.getResources().getColor(R.color.colorPrimary));


        pieChart.setMarker(new MyMarkerView(context, R.layout.marker_chart_popup));
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
    public static void loadPieChartBalanceViewAdapter(PieChart pieChart, Context context, Double income, Double outgo){
        Typeface font= Typeface.createFromAsset(context.getAssets(), "fonts/title.otf");


        Legend l = pieChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTypeface(font);
        l.setTextSize(11);
        l.setYOffset(-10);

        //    xAxis.setTypeface(tfLight);




        pieChart.getDescription().setEnabled(false);


        List<PieEntry> entries = new ArrayList<>();

        if(income != 0 || outgo != 0){
            entries.add(new PieEntry((income).floatValue()
                    , context.getResources().getString(R.string.income_text_2)));
            entries.add(new PieEntry((outgo).floatValue()
                    , context.getResources().getString(R.string.outgo_text_2)));
        }


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


        dataSet.setValueTextColor(Color.BLACK);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(context.getResources().getColor(R.color.income_green));
        colors.add(context.getResources().getColor(R.color.outgo_red));

        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        //dataSet.setValueFormatter(new PieChartFormat());
        dataSet.setValueFormatter(new PercentFormatter());
        dataSet.setValueTypeface(font);
        dataSet.setValueTextSize(12);
        dataSet.setValueLineWidth(0);
        PieData pieData = new PieData(dataSet);


        pieData.setValueTextSize(12);
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
        pieChart.setExtraBottomOffset(25);


        pieChart.setHoleColor(context.getResources().getColor(R.color.white));


        pieChart.setMarker(new MyMarkerView(context, R.layout.marker_chart_popup));
        pieChart.setHighlightPerTapEnabled(true);

        final PieChart pieChartFinal = pieChart;
        /*
        pieChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {

                pieChartFinal.getMarker().refreshContent(e, h);
            }

            @Override
            public void onNothingSelected() {

            }
        });
            */
        pieChart.setClickable(false);
        pieChart.setEnabled(false);
        pieChart.setTouchEnabled(false);
        pieChart.invalidate();
    }
    public static Double getTotalIncomeOutgo(Context context, boolean income){
        Double balance = 0.0;
        DatabaseManager db = new DatabaseManager(context);
        List<Transaction> transactions = db.findTransactionBeforeDate(new Date());
        List<Installment> installments = db.findInstallmentsPaidBeforeDate(new Date());

        for(Transaction transaction :transactions){
            if(transaction.isIncome() == income){
                if(transaction.isInstallment()){
                    balance += transaction.getEntrance_payment();
                }else{
                    balance += transaction.getValue();
                }
            }
        }

        for(Installment installment : installments){
            if(installment.isIncome() == income){
                balance += installment.getPayment();
            }
        }

        return  balance;
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
    public static String formatingToCurrency(Double value){
        DecimalFormat mFormat;

        mFormat = new DecimalFormat("###,###,##0.00");

        return  mFormat.format(value);
    }
    public static void getData(Activity context){
        getUserDetails(context);
    }

    public static void getUserDetails(final Activity activity){
        SvcApi svcAth;
        svcAth = Svc.initAuth(activity);

        Call<UserDetails> call = svcAth.getUserDetails();
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                final Response responsefinal =  response;
                final UserDetails userDetails = response.body();

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseManager db = new DatabaseManager(activity);
                            if(userDetails.getId() == 0) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    activity.startActivity(new Intent(activity, UserDetailsActivity.class));
                                }else{
                                    activity.startActivity(new Intent(activity, UserDetailsOldActivity.class));
                                }
                            }else{
                                Log.e("getUseDetails", "getUserDetails!!");
                                db.saveUserDetailsFromServer(userDetails);
                                getTransactions(activity);
                            }
                        }
                    });
                } else {
                    getTransactions(activity);
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                t.printStackTrace();
                getTransactions(activity);
            }
        });
    }
    public static void getTransactions(final Activity activity){
        Log.e("getTransactions", "getTransactions");
        SvcApi svcAth;
        svcAth = Svc.initAuth(activity);

        Call<List<Transaction>> call = svcAth.getTransactions();
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                final Response responsefinal =  response;
                final List<Transaction> transactions = response.body();

                if (response.isSuccessful()) {
                    Log.e("response","response successfull");
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseManager db = new DatabaseManager(activity);
                            if(transactions !=  null || !transactions.isEmpty()) {
                                for(Transaction transaction : transactions) {
                                    db.saveTransactionFromServer(transaction);
                                }
                            }
                            getGoals(activity);
                        }
                    });
                } else {
                    Log.e("response","response NOT successfull");
                    getGoals(activity);
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                t.printStackTrace();
                getGoals(activity);
            }
        });
    }
    public static void getGoals(final Activity activity){
        SvcApi svcAth;
        svcAth = Svc.initAuth(activity);

        Call<List<Goal>> call = svcAth.getGoals();
        call.enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                final Response responsefinal =  response;
                final List<Goal> goals = response.body();

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseManager db = new DatabaseManager(activity);
                            if(goals !=  null || !goals.isEmpty()) {
                                for(Goal goal : goals) {
                                    db.saveGoalFromServer(goal);
                                }
                            }
                            getServicePrices(activity);
                        }
                    });
                } else {
                    getServicePrices(activity);
                }
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                t.printStackTrace();
                getServicePrices(activity);
            }
        });
    }
    public static void getServicePrices(final Activity activity){
        SvcApi svcAth;
        svcAth = Svc.initAuth(activity);

        Call<List<ServicePrice>> call = svcAth.getServiceprices();
        call.enqueue(new Callback<List<ServicePrice>>() {
            @Override
            public void onResponse(Call<List<ServicePrice>> call, Response<List<ServicePrice>> response) {
                final Response responsefinal =  response;
                final List<ServicePrice> goals = response.body();

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseManager db = new DatabaseManager(activity);
                            if(goals !=  null || !goals.isEmpty()) {
                                for(ServicePrice servicePrice : goals) {
                                    db.saveServicepriceFromServer(servicePrice);
                                }
                            }
                            getProductPrices(activity);
                        }
                    });
                } else {
                    getProductPrices(activity);
                }
            }

            @Override
            public void onFailure(Call<List<ServicePrice>> call, Throwable t) {
                t.printStackTrace();
                getProductPrices(activity);
            }
        });
    }
    public static void getProductPrices(final Activity activity){
        SvcApi svcAth;
        svcAth = Svc.initAuth(activity);

        Call<List<ProductPrice>> call = svcAth.getProductPrices();
        call.enqueue(new Callback<List<ProductPrice>>() {
            @Override
            public void onResponse(Call<List<ProductPrice>> call, Response<List<ProductPrice>> response) {
                final Response responsefinal =  response;
                final List<ProductPrice> goals = response.body();

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseManager db = new DatabaseManager(activity);
                            if(goals !=  null || !goals.isEmpty()) {
                                for(ProductPrice goal : goals) {
                                    db.saveProductpriceFromServer(goal);
                                }
                            }
                            activity.startActivity(new Intent(activity, MainActivity.class));
                        }
                    });
                } else {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Call<List<ProductPrice>> call, Throwable t) {
                t.printStackTrace();
                activity.startActivity(new Intent(activity, MainActivity.class));
            }
        });
    }
    // INTERNET CONECTION
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public static ResponseMessage getErrors(String JsonArrayErrors){
        try {
            return (ResponseMessage) new ObjectMapper()
                    .readValue(JsonArrayErrors, ResponseMessage.class);
        }catch (Exception e){
            return new ResponseMessage();
        }

    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch(Exception e){e.printStackTrace();}
    }
    public static ProgressCustomDialog showCustomDialog(ProgressCustomDialog dialog, Activity activity, String title,
                                                        String text){
        if(dialog == null){

            dialog = new ProgressCustomDialog(activity, title, text);
            dialog.show();
        }
        return dialog;
    }
    public static ProgressCustomDialog hideCustomDialog(ProgressCustomDialog dialog){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
        return dialog;
    }
    public static ProgressDialog showDialog(ProgressDialog dialog, Context context, int text){
        if(dialog == null){

            dialog = ProgressDialog.show(context, "",
                    context.getResources().getString(text), true);
        }
        return dialog;
    }
    public static ProgressDialog hideDialog(ProgressDialog dialog){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
        return dialog;
    }
    public static void showSuccessMessagesToast(Context context, List<ResponseMessage> responseArrayMessages){

        for(ResponseMessage re : responseArrayMessages){
            Toast.makeText(context, re.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public static void showErrorsMessagesToast(Context context, ResponseMessage error){


            Log.e("re.getMessage()", error.getMessage());
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

    }

    public static int measureContentWidth(PopupMenuAdapter listAdapter, Context context) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final PopupMenuAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(context);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }
        maxWidth += 90;
        return maxWidth;
    }
    public static int measureContentHeight(PopupMenuAdapter listAdapter, Context context) {
        ViewGroup mMeasureParent = null;
        int sumHeight = 0;
        View itemView = null;
        int itemType = 0;

        final PopupMenuAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(context);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemHeight = itemView.getMeasuredHeight();

            sumHeight += itemHeight;
        }

        return sumHeight;
    }
    public static void removeEditTextValueIfZero(EditText valueInput){
        if( valueInput.getText() != null && valueInput.getText().toString() != null
                && valueInput.getText().toString().length() > 0){
            try{
                if(Double.parseDouble(valueInput.getText().toString()) == 0.0){
                    valueInput.setText("");
                    Log.e("utils", "removeEditTextValueIfZero -> removed");
                }
            }catch (Exception e){ e .printStackTrace();}
        }
    }

}
