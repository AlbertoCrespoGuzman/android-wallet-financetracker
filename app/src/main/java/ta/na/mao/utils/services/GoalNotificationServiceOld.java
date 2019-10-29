package ta.na.mao.utils.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.core.app.NotificationCompat;
import ta.na.mao.R;
import ta.na.mao.activities.MainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.utils.Defaultdata;

public class GoalNotificationServiceOld extends IntentService {
    final static String CASHBACK_INFO = "cashback_info";

    static DecimalFormat mFormat = new DecimalFormat("###,###,##0.00");
    static DecimalFormat twoDecimalsFormat = new DecimalFormat("###,###,##0.0");

    public GoalNotificationServiceOld() {
        super("Cashback IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        checkForGoalsReached(getApplicationContext());
    }

    static public void checkForGoalsReached(Context context){
        Log.e("NotificationService","checkForGoalsReached");
        DatabaseManager db = new DatabaseManager(context);

        List<Goal> goals = db.findGoalsByDateOneMonth(new Date());

        for(Goal goal : goals){
            if(!goal.isNotificated() && checkOneGoalReached(goal, context)){
                makeGoalReachedNotification(goal, context);
            }
        }
        for(Goal goal : goals){
            if(/* !goal.isNotificated() && */ !goal.isPreviewNotificated() && checkPreviewNotified(goal, context)){
                makeGoalPreviewNotification(goal, context);
            }
        }

    } static public boolean checkPreviewNotified(Goal goal, Context context){
        boolean previewNotified = false;

        Calendar notificationDate = Calendar.getInstance();
        notificationDate.setTime(goal.getNotificationdate());

        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(new Date());
        Log.e("checkNotification", "notificationDate.get(Calendar.DAY_OF_MONTH) = " + notificationDate.get(Calendar.DAY_OF_MONTH));
        Log.e("checkNotification", "nowDate.get(Calendar.DAY_OF_MONTH) = " + nowDate.get(Calendar.DAY_OF_MONTH));
        if(notificationDate.get(Calendar.DAY_OF_MONTH) <= nowDate.get(Calendar.DAY_OF_MONTH)) {
            previewNotified = true;
        }

        return previewNotified;
    }

    static public void makeGoalPreviewNotification(Goal goal, Context context){
        Double pertentageReached = getPercentageReached(goal, context);
        if(pertentageReached > 100){
            pertentageReached = 100.0;
        }
        Log.e("ReachedNotification","makeGoalPreviewNotification goal " + goal.toString());
        NotificationManager notifManager;

        String id = context.getString(R.string.preview_notification_channel_id); // default_channel_id
        String title = context.getString(R.string.app_name); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String aMessage = context.getResources().getString(R.string.goal_reached_text_1)
                + " " + twoDecimalsFormat.format(pertentageReached) + "% " + ( goal.isIncome() ?
                context.getResources().getString(R.string.goal_reached_text_2_income)
                : context.getResources().getString(R.string.goal_reached_text_2_outgo))
                +  " na categoria " + goal.getCategoryText(context);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("changeFragment", Defaultdata.MAINACTIVITY_CHANGETO_GOALS);

            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(context.getResources().getString(R.string.goal_preview_reached))                            // required
                    .setSmallIcon(R.drawable.ic_tanahand_png)   // required
                    .setContentText(goal.getCategoryText(context)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage));
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(context.getResources().getString(R.string.goal_preview_reached))                            // required
                    .setSmallIcon(R.drawable.ic_tanahand_png)   // required
                    .setContentText( goal.getCategoryText(context)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage));
        }
        Notification notification = builder.build();
        notifManager.notify(Defaultdata.GOAL_PREVIEW_REACHED_NOTIFICATION, notification);


        goal.setPreviewNotificated(true);
        DatabaseManager db = new DatabaseManager(context);
        db.saveGoalFromLocal(goal);
    }
    static public Double getPercentageReached(Goal goal, Context context){
        Double percentage = 0.0;
        List<Transaction> transactions;
        List<Installment> installments;
        List<String> categories;
        DatabaseManager db = new DatabaseManager(context);



        if(goal.isIncome()){
            categories = Arrays.asList(context.getResources()
                    .getStringArray(R.array.goal_income_categories_array));
        }else{
            categories = Arrays.asList(context.getResources()
                    .getStringArray(R.array.goal_outgo_categories_array));
        }

        // checking if category is "MAIN" (geral)
        if(goal.getCategory() == categories.size() -1){
            Log.e("GoalNotificationService", "goal Category -> MAIN (GERAL)");
            transactions = db.findTransactionsByDateOneMonthToCurrentDay(new Date());
            installments = db.findInstallmentByDateOneMonthToCurrentDay(new Date());
        }else{
            transactions = db.findTransactionsByDateOneMonthAndCategoryAndIncomeToCurrentDay(new Date(), goal.getCategory(), goal.isIncome());
            installments = db.findInstallmentsByDateOneMonthAndCategoryAndIncomeToCurrentDay(new Date(), goal.getCategory(), goal.isIncome());
        }

        percentage = (sumPaid(transactions, installments, context) / goal.getValue()) * 100;


        return percentage;
    }
    static  public void makeGoalReachedNotification(Goal goal,Context context){
        Log.e("ReachedNotification","makeGoalReachedNotification goal " + goal.toString());
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("changeFragment", Defaultdata.MAINACTIVITY_CHANGETO_GOALS);
        NotificationManager notifManager;

        String id = context.getString(R.string.preview_notification_channel_id); // default_channel_id
        String title = context.getString(R.string.app_name); // Default Channel
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String aMessage = context.getResources().getString(R.string.goal_reached_text_1)
                + " R$ " + mFormat.format(goal.getValue()) + " " + ( goal.isIncome() ?
                context.getResources().getString(R.string.goal_reached_text_2_income)
                : context.getResources().getString(R.string.goal_reached_text_2_outgo))
                +  " na categoria " + goal.getCategoryText(context);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("changeFragment", Defaultdata.MAINACTIVITY_CHANGETO_GOALS);

            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(context.getResources().getString(R.string.goal_max_reached))                            // required
                    .setSmallIcon(R.drawable.ic_tanahand_png)   // required
                    .setContentText(goal.getCategoryText(context)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage));
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(context.getResources().getString(R.string.goal_preview_reached))                            // required
                    .setSmallIcon(R.drawable.ic_tanahand_png)   // required
                    .setContentText(goal.getCategoryText(context)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage));
        }
        Notification notification = builder.build();
        notifManager.notify(Defaultdata.GOAL_REACHED_NOTIFICATION, notification);





        goal.setNotificated(true);
        DatabaseManager db = new DatabaseManager(context);
        db.saveGoalFromLocal(goal);
    }
    static public boolean checkOneGoalReached(Goal goal, Context context){
        Log.e("NotifiService", "checkOneGoalReadched " + goal.toString());
        boolean goalReached = false;

        DatabaseManager db = new DatabaseManager(context);

        List<String> categories = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();
        List<Installment> installments = new ArrayList<>();

        if(goal.isIncome()){
            categories = Arrays.asList(context.getResources()
                    .getStringArray(R.array.goal_income_categories_array));
        }else{
            categories = Arrays.asList(context.getResources()
                    .getStringArray(R.array.goal_outgo_categories_array));
        }

        // checking if category is "MAIN" (geral)
        if(goal.getCategory() == categories.size() -1){
            Log.e("GoalNotificationService", "goal Category -> MAIN (GERAL)");
            transactions = db.findTransactionsByDateOneMonth(new Date());
            installments = db.findInstallmentByDateOneMonth(new Date());
        }else{
            transactions = db.findTransactionsByDateOneMonthAndCategoryAndIncome(new Date(), goal.getCategory(), goal.isIncome());
            installments = db.findInstallmentsByDateOneMonthAndCategoryAndIncome(new Date(), goal.getCategory(), goal.isIncome());
        }

        if(sumPaid(transactions, installments, context) >= goal.getValue()){
            goalReached = true;
        }
        Log.e("NotifiService", "goalReached " + goalReached);
        return goalReached;
    }

    static  public Double sumPaid(List<Transaction> transactions, List<Installment> installments, Context context){
        Double sum = 0.0;

        for(Transaction transaction : transactions){
            if(transaction.isInstallment()){
                sum += transaction.getEntrance_payment();
            }else{
                sum += transaction.getValue();
            }
        }

        for(Installment installment : installments){
            sum += installment.getPayment();
        }
        Log.e("NotifiService", "sumPaid " + sum);
        return sum;
    }
}