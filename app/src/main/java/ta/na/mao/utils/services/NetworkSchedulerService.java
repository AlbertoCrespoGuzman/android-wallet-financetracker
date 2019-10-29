package ta.na.mao.utils.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.RequiresApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ta.na.mao.communications.Svc;
import ta.na.mao.communications.SvcApi;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Blocker;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Task;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.database.models.UserDetails;
import ta.na.mao.database.models.productprice.FixedVariableCost;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.productprice.Raw;
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.database.models.serviceprice.VariableCost;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.receivers.ConnectivityReceiver;

import static ta.na.mao.utils.Defaultdata.MY_CONNECTIVITY_CHANGE;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();

    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "ServicePrice created");
        mConnectivityReceiver = new ConnectivityReceiver(this);
    }



    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver);

        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        IntentFilter filter = new IntentFilter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e("onStartJob","using registerNetworkCallback");
            createChangeConnectivityMonitor();
            filter.addAction(MY_CONNECTIVITY_CHANGE);
        } else {
            Log.e("onStartJob","using old broadcast receiver");
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }

        registerReceiver( mConnectivityReceiver, filter);

        return true;
    }
    @RequiresApi(Build.VERSION_CODES.N)

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    private void createChangeConnectivityMonitor() {
        final Intent intent = new Intent(MY_CONNECTIVITY_CHANGE);
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder().build(),
                    new ConnectivityManager.NetworkCallback() {
                        /**
                         * @param network
                         */
                        @Override
                        public void onAvailable(Network network) {
                            Log.e("onAvailable", "On available network");
                            sendBroadcast(intent);
                        }

                        /**
                         * @param network
                         */
                        @Override
                        public void onLost(Network network) {
                            Log.e("onLost", "On not available network");
                            sendBroadcast(intent);
                        }
                    });
        }
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        String message = isConnected ? "Good! Connected to Internet" : "Sorry! Not connected to internet";

        Log.e("JobService", message);

        final long milisecondsDelay = getRandomNumberInRange(10,10000);
        Log.e("Timer","Starting Timer in "  + milisecondsDelay + " mili");


        if(isConnected){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("Timer","STARTED Timer in "  + milisecondsDelay + " mili");
                    DatabaseManager db = new DatabaseManager(getApplicationContext());
                    if(db.findBlocker().isBlocked()){
                        Log.e("Timer", "is blocked!");
                        checkBlocker(getApplicationContext());

                    }else{
                        Log.e("Timer", "NOT Blocked, doing job");
                        Blocker blocker = db.findBlocker();
                        blocker.setBlocked(true);
                        blocker.setLast_update(new Date());
                        db.saveBlocker(blocker);

                        updateData(getApplicationContext());

                    }
                }
            }, milisecondsDelay);

        }

    }
    static public void checkBlocker(Context context){
        DatabaseManager db = new DatabaseManager(context);
        if(!db.ExistsBlocker()){
            Blocker blocker = new Blocker();
            blocker.setLast_update(new Date());
            blocker.setBlocked(false);
            db.saveBlocker(blocker);
        }else{
            Blocker blocker = db.findBlocker();
            if(blocker.isBlocked() && new Date().getTime() - blocker.getLast_update().getTime() > 1000 * 60){
                Log.e("checkBlocker", "because blocked... unblocking");
                blocker.setBlocked(false);
                blocker.setLast_update(new Date());
                db.saveBlocker(blocker);
            }
        }
    }
    static public void updateData(Context context){
        DatabaseManager db = new DatabaseManager(context);

        List<UserDetails> userDetails = db.findUserDetailsNotUpdated();
        List<Transaction> transactions = db.findTransactionsNotUpdated();
        List<Goal> goals = db.findGoalsNotUpdated();
        List<Task> tasks =  db.findTasksNotUpdated();

        if((transactions == null || transactions.isEmpty()) && (goals == null || goals.isEmpty())
                && (tasks == null || tasks.isEmpty()) && (userDetails == null || userDetails.isEmpty())){



            List<Installment> installments = db.findInstallmentsNotUpdated();
            if(installments.isEmpty()){
                Blocker blocker = db.findBlocker();
                blocker.setTransaction(false);
                blocker.setGoal(false);
                blocker.setTask(false);
                blocker.setInstallment(false);
                blocker.setBlocked(false);
                blocker.setUserdetails(false);
                db.saveBlocker(blocker);
            }else{
                Blocker blocker = db.findBlocker();
                blocker.setTransaction(false);
                blocker.setGoal(false);
                blocker.setTask(false);
                blocker.setInstallment(!installments.isEmpty());
                db.saveBlocker(blocker);

                sendInstallments(installments, context); Log.e("SENDInstallments", "size " + installments.size());
            }


        }else{
            Blocker blocker = db.findBlocker();
            blocker.setTransaction(!transactions.isEmpty());
            blocker.setGoal(!goals.isEmpty());
            blocker.setTask(!tasks.isEmpty());
            blocker.setUserdetails(!userDetails.isEmpty());
            blocker.setInstallment(false);
            db.saveBlocker(blocker);

            if(!userDetails.isEmpty()) sendUserDetails(userDetails, context); Log.e("userDetails", "sending " + userDetails.size() + " userDetails");
            if(!transactions.isEmpty()) sendTransactions(transactions, context); Log.e("transactions", "sending " + transactions.size() + " transactions");
            if(!goals.isEmpty())  sendGoals(goals, context);  Log.e("goals", "sending " + goals.size() + " goals");
            if(!tasks.isEmpty())  sendTasks(tasks, context);  Log.e("tasks", "sending " + tasks.size() + " tasks");
        }

        /*
                SERVICE PRICE
         */


        List<ServicePrice> servicePrices = db.findServicePricesNotUpdated();

        if(servicePrices == null || servicePrices.isEmpty()){
          /*  List<FixedCost> fixedCosts = db.findFixedCostServicePriceNotUpdated();
            List<LabourCost> labourCosts = db.findLabourCostServicePriceNotUpdated();
            List<LabourTax> labourTaxes = db.findLabourTaxServicePriceNotUpdated();
            List<VariableCost> variableCosts = db.findVariableCostServicePriceNotUpdated();

            if(fixedCosts.isEmpty() && labourCosts.isEmpty() && labourTaxes.isEmpty() && variableCosts.isEmpty()){
                Blocker blocker = db.findBlocker();
                blocker.setFixedcostServiceprice(false);
                blocker.setLabourcostServiceprice(false);
                blocker.setLabourtaxServiceprice(false);
                blocker.setVariablecostServiceprice(false);
                db.saveBlocker(blocker);
            }else{
                Blocker blocker = db.findBlocker();
                blocker.setServiceprice(false);
                blocker.setFixedcostServiceprice(!fixedCosts.isEmpty());
                blocker.setLabourcostServiceprice(!labourCosts.isEmpty());
                blocker.setLabourtaxServiceprice(!labourTaxes.isEmpty());
                blocker.setVariablecostServiceprice(!variableCosts.isEmpty());
                db.saveBlocker(blocker);

                if(!fixedCosts.isEmpty()) sendFixedCostsServicePrice(fixedCosts, context); Log.e("SENDfixedCosts", "size " + fixedCosts.size());
                if(!labourCosts.isEmpty()) sendLabourCostsServicePrice(labourCosts, context); Log.e("SENDlabourCosts", "size " + labourCosts.size());
                if(!labourTaxes.isEmpty()) sendLabourTaxesServicePrice(labourTaxes, context); Log.e("SENDlabourTaxes", "size " + labourTaxes.size());
                if(!variableCosts.isEmpty()) sendVariableCostsServicePrice(variableCosts, context); Log.e("SENDvariableCosts", "size " + variableCosts.size());

            } */
        }else{
            Blocker blocker = db.findBlocker();
            blocker.setServiceprice(!servicePrices.isEmpty());
            blocker.setFixedcostServiceprice(false);
            blocker.setLabourcostServiceprice(false);
            blocker.setLabourtaxServiceprice(false);
            blocker.setVariablecostServiceprice(false);
            db.saveBlocker(blocker);

            if(!servicePrices.isEmpty()) sendServicePrices(servicePrices, context); Log.e("sendServicePrices", "sending " + servicePrices.size() + " servicePrices");

        }

        List<ProductPrice> productPrices = db.findProductPricesNotUpdated();

        if(productPrices == null || productPrices.isEmpty()){
/*
            List<FixedVariableCost> fixedVariableCosts = db.findFixedVariableCostProductPriceNotUpdated();
            List<Raw> raws = db.findRawProductPriceNotUpdated();

            if(fixedVariableCosts.isEmpty() && raws.isEmpty()){
                Blocker blocker  = db.findBlocker();
                blocker.setRawProductPrice(false);
                blocker.setFixedcostServiceprice(false);
                db.saveBlocker(blocker);
            }else{
                Blocker blocker = db.findBlocker();
                blocker.setProductprice(false);
                blocker.setRawProductPrice(!raws.isEmpty());
                blocker.setFixedvariablecostProductPrice(!fixedVariableCosts.isEmpty());
                db.saveBlocker(blocker);

                if(!raws.isEmpty()) sendRawsProductPrice(raws, context);
                if(!fixedVariableCosts.isEmpty()) sendFixedVariableCostsProductPrice(fixedVariableCosts, context);

            }*/
        }else{
            Blocker blocker = db.findBlocker();
            blocker.setProductprice(!productPrices.isEmpty());
            blocker.setRawProductPrice(false);
            blocker.setFixedvariablecostProductPrice(false);
            db.saveBlocker(blocker);

            if(!productPrices.isEmpty()) sendProductPrices(productPrices, context); Log.e("sendProductPrices", "sending " + productPrices.size() + " productPrices");

        }

    }
    static public  void sendUserDetails(List<UserDetails> userDetails,final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final UserDetails userDetailsOne = userDetails.get(0);
        final List<UserDetails> userDetails1 = userDetails;
        final long userdetails_local_id = userDetailsOne.getLocal_id();

        Log.e("INSIDE", "INSIDE sendUserDetails -> sending: " +userDetailsOne.toString());


        Call<UserDetails> call = svcAuth.postUserDetails(userDetailsOne);

        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("UserDetails REVEIVED", responsefinal.body().toString());
                            UserDetails userDetailsOne1 = (UserDetails) responsefinal.body();
                            userDetailsOne1.setLocal_id(userdetails_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveUserDetailsFromServer(userDetailsOne1);

                            userDetails1.remove(0);
                            if(!userDetails1.isEmpty()){
                                sendUserDetails(userDetails1,context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.USERDETAILS, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    static public void sendTransactions(List<Transaction> transactions, final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final Transaction transaction = transactions.get(0);
        final List<Transaction> transactions1 = transactions;
        final long transaction_local_id = transaction.getLocal_id();

        Log.e("INSIDE", "INSIDE sendTransactions -> sending: " +transaction.toString());


        Call<Transaction> call = svcAuth.postTransaction(transaction);

        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("Transaction REVEIVED", responsefinal.body().toString());
                            Transaction transaction1 = (Transaction) responsefinal.body();
                            transaction1.setLocal_id(transaction_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveTransactionFromServer(transaction1);

                            transactions1.remove(0);
                            if(!transactions1.isEmpty()){
                                sendTransactions(transactions1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.TRANSACTION, context);
                            }

                        }
                    });
                } else {
                   unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                    //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                t.printStackTrace();
            //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    static public void sendInstallments(List<Installment> installments,final Context context){
        SvcApi svcAuth = Svc.initAuth(context);


        final Installment installment = installments.get(0);
        final List<Installment> installments1 = installments;
        final long installment_local_id = installment.getLocal_id();
        Call<Installment> call = svcAuth.postInstallment(installment);

        call.enqueue(new Callback<Installment>() {
            @Override
            public void onResponse(Call<Installment> call, Response<Installment> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Log.e("Installment REVEIVED", responsefinal.body().toString());
                            Installment installment1 = (Installment) responsefinal.body();
                            installment1.setLocal_id(installment_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveInstallmentFromServer(installment1);

                            installments1.remove(0);
                            if(!installments1.isEmpty()){
                                sendInstallments(installments1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.INSTALLMENT, context);
                            }

                        }
                    });
                }else{
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                }
            }

            @Override
            public void onFailure(Call<Installment> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    static public void sendGoals(List<Goal> goals,final Context context){
        SvcApi svcAuth = Svc.initAuth(context);


        final Goal goal = goals.get(0);
        final List<Goal> goals1 = goals;
        final long goal_local_id = goal.getLocal_id();
        if(svcAuth == null) Log.e("sendGoals","sendGoals svcAuth NULLLL");
        if(goal == null) Log.e("sendGoals","sendGoals goal NULLLL");
        Call<Goal> call = svcAuth.postGoal(goal);

        call.enqueue(new Callback<Goal>() {
            @Override
            public void onResponse(Call<Goal> call, Response<Goal> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Log.e("Goal REVEIVED", responsefinal.body().toString());
                            Goal goal1 = (Goal) responsefinal.body();
                            goal1.setLocal_id(goal_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveGoalFromServer(goal1);

                            goals1.remove(0);
                            if(!goals1.isEmpty()){
                                sendGoals(goals1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.GOAL, context);
                            }

                        }
                    });
                }else{
                    if(response.code() == 401){
                        goals1.remove(0);
                        if(!goals1.isEmpty()){
                            sendGoals(goals1, context);
                        //    Utils.getData(getApplicationContext());
                        }else{
                            unblockBlocker(Defaultdata.Blocker.GOAL, context);
                        }
                    }
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                }
            }

            @Override
            public void onFailure(Call<Goal> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    static public void sendTasks(List<Task> tasks, Context context){
        if(tasks != null && !tasks.isEmpty()){
            Task task = tasks.get(0);
            if(task.isDelete() && !task.isUpdated()){
                if(task.isTransaction()){
                    deleteTransactionInServer(tasks, context);
                }else if(task.isInstallment()){
                    deleteInstallmentInServer(tasks, context);
                }else if(task.isGoal()){
                    deleteGoalInServer(tasks, context);
                }else if(task.isServiceprice()){
                    deleteServicePriceInServer(tasks, context);
                }else if(task.isFixedcostServicePrice()){
                    deleteFixedCostServicePriceInServer(tasks, context);
                }else if(task.isLabourcostServicePrice()){
                    deleteLabourCostServicePriceInServer(tasks, context);
                }else if(task.isLabourtaxServicePrice()){
                    deleteLabourTaxServicePriceInServer(tasks, context);
                }else if(task.isVariablecostServicePrice()){
                    deleteVariableCostServicePriceInServer(tasks, context);
                }else if(task.isProductprice()){
                    deleteProductPriceInServer(tasks, context);
                }else if(task.isRawProductPrice()){
                    deleteRawProductPriceInServer(tasks, context);
                }else if(task.isFixedVariableCostProductPrice()){
                    deleteFixedVariableCostProductPriceInServer(tasks, context);
                }
            }
        }else{
            unblockBlocker(Defaultdata.Blocker.TASK, context);
        }
    }
    static public void deleteTransactionInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getTransaction_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteTransaction(task.getTransaction_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteTransaction", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteInstallmentInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getInstallment_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteInstallment(task.getInstallment_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteInstallment", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK,context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteGoalInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getGoal_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteGoal(task.getGoal_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteGoal", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1, context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    /*
            SERVICE PRICE
     */

    static public void deleteServicePriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getServiceprice_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteServicePrice(task.getServiceprice_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteServicePr", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteFixedCostServicePriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getFixedcost_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteFixedCostServicePrice(task.getFixedcost_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteFixedCostSP", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteLabourCostServicePriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getLabourcost_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteLabourCostServicePrice(task.getLabourcost_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteLabourCostSP", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteLabourTaxServicePriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getLabourtax_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteLabourTaxServicePrice(task.getLabourtax_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteLabourTaxSP", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteVariableCostServicePriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getVariablecost_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteVariableCostServicePrice(task.getVariablecost_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteVaribleCSP", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    /*
            SERVICE PRICE
     */

    static public void deleteProductPriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getProductprice_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteProductPrice(task.getProductprice_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteProductP", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteRawProductPriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getRawproductprice_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteRawProductPrice(task.getRawproductprice_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteRawPP", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void deleteFixedVariableCostProductPriceInServer(List<Task> tasks,final  Context context){
        final Task task = tasks.get(0);
        final List<Task> tasks1 = tasks;
        if(task.getFixedvariablecostproductprice_id() != 0){
            SvcApi svcAuth = Svc.initAuth(context);


            Call<Boolean> call = svcAuth.deleteFixedVariableCostProductPrice(
                    task.getFixedvariablecostproductprice_id());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    final Response responsefinal =  response;

                    if (response.isSuccessful()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                Log.e("DeleteFixedVarPP", responsefinal.body().toString());
                                boolean deleted = (Boolean) responsefinal.body();
                                DatabaseManager db = new DatabaseManager(context);
                                task.setUpdated(deleted);
                                db.saveTask(task);

                                tasks1.remove(0);
                                if(!tasks1.isEmpty()){
                                    sendTasks(tasks1,context);
                                }else{
                                    unblockBlocker(Defaultdata.Blocker.TASK, context);
                                }

                            }
                        });
                    }else{
                        unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    static public void unblockBlocker(Defaultdata.Blocker finished, Context context){

        DatabaseManager db = new DatabaseManager(context);

        Blocker blocker = db.findBlocker();

        switch (finished) {
            case USERDETAILS:
                blocker.setUserdetails(false);
                break;
            case TRANSACTION:
                blocker.setTransaction(false);
                break;
            case INSTALLMENT:
                blocker.setInstallment(false);
                break;
            case GOAL:
                blocker.setGoal(false);
                break;
            case TASK:
                blocker.setTask(false);
                break;
            case SERVICEPRICE:
                blocker.setTask(false);
                break;
            case FIXEDCOST_SERVICEPRICE:
                blocker.setFixedcostServiceprice(false);
                break;
            case LABOURCOST_SEVICEPRICE:
                blocker.setLabourcostServiceprice(false);
                break;
            case LABOURTAX_SERVICEPRICE:
                blocker.setLabourtaxServiceprice(false);
                break;
            case VARIABLECOST_SERVICEPRICE:
                blocker.setVariablecostServiceprice(false);
                break;
            case PRODUCTPRICE:
                blocker.setProductprice(false);
                break;
            case RAW_PRODUCTPRICE:
                blocker.setRawProductPrice(false);
                break;
            case FIXEDVARIABLECOST_PRODUCTPRICE:
                blocker.setFixedvariablecostProductPrice(false);
                break;
            case FORCE_UNBLOCK:
                blocker.setBlocked(false);
        }

        blocker.setLast_update(new Date());
        if(!blocker.isUserdetails() && !blocker.isTransaction() &&
                !blocker.isInstallment() && !blocker.isGoal() && !blocker.isTask()
        && !blocker.isServiceprice() && !blocker.isFixedcostServiceprice() && !blocker.isLabourcostServiceprice()
        && !blocker.isLabourtaxServiceprice() && !blocker.isVariablecostServiceprice() && !blocker.isProductprice()
        && !blocker.isRawProductPrice() && !blocker.isFixedvariablecostProductPrice()) {
            blocker.setBlocked(false);
        }

        db.saveBlocker(blocker);

    }
/*
                    SERVICE PRICES
 */
    static public void sendServicePrices(List<ServicePrice> servicePrices, final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final ServicePrice servicePrice = servicePrices.get(0);
        final List<ServicePrice> servicePrices1 = servicePrices;
        final long servicePrice_local_id = servicePrice.getLocal_id();

        Log.e("INSIDE", "INSIDE sendServicePrices -> sending: " +servicePrice.toString());


        Call<ServicePrice> call = svcAuth.postServicePrice(servicePrice);

        call.enqueue(new Callback<ServicePrice>() {
            @Override
            public void onResponse(Call<ServicePrice> call, Response<ServicePrice> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("ServicePrice REVEIVED", responsefinal.body().toString());
                            ServicePrice servicePrice1 = (ServicePrice) responsefinal.body();
                            servicePrice1.setLocal_id(servicePrice_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveServicepriceFromServer(servicePrice1);

                            servicePrices1.remove(0);
                            if(!servicePrices1.isEmpty()){
                                sendServicePrices(servicePrices1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.SERVICEPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<ServicePrice> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    static public void sendFixedCostsServicePrice(List<FixedCost> fixedCosts, final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final FixedCost fixedCost = fixedCosts.get(0);
        final List<FixedCost> fixedCosts1 = fixedCosts;
        final long fixedCost_local_id = fixedCost.getLocal_id();

        Log.e("INSIDE", "INSIDE sendFixedCostsServicePrice -> sending: " +fixedCost.toString());


        Call<FixedCost> call = svcAuth.postFixedCostServicePrice(fixedCost);

        call.enqueue(new Callback<FixedCost>() {
            @Override
            public void onResponse(Call<FixedCost> call, Response<FixedCost> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("FixedCost REVEIVED", responsefinal.body().toString());
                            FixedCost fixedCost1 = (FixedCost) responsefinal.body();
                            fixedCost1.setLocal_id(fixedCost_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveFixedCostServicePriceFromServer(fixedCost1);

                            fixedCosts1.remove(0);
                            if(!fixedCosts1.isEmpty()){
                                sendFixedCostsServicePrice(fixedCosts1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.FIXEDCOST_SERVICEPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<FixedCost> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    static public void sendLabourCostsServicePrice(List<LabourCost> labourCosts, final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final LabourCost labourCost = labourCosts.get(0);
        final List<LabourCost> labourCosts1 = labourCosts;
        final long labourCost_local_id = labourCost.getLocal_id();

        Log.e("INSIDE", "INSIDE sendLabourCostsServicePrice -> sending: " +labourCost.toString());


        Call<LabourCost> call = svcAuth.postLabourCostServicePrice(labourCost);

        call.enqueue(new Callback<LabourCost>() {
            @Override
            public void onResponse(Call<LabourCost> call, Response<LabourCost> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("labourCost REVEIVED", responsefinal.body().toString());
                            LabourCost labourCost1 = (LabourCost) responsefinal.body();
                            labourCost1.setLocal_id(labourCost_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveLabourCostServicePriceFromServer(labourCost1);

                            labourCosts1.remove(0);
                            if(!labourCosts1.isEmpty()){
                                sendLabourCostsServicePrice(labourCosts1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.LABOURCOST_SEVICEPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<LabourCost> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    static public void sendLabourTaxesServicePrice(List<LabourTax> labourTaxes, final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final LabourTax labourTax = labourTaxes.get(0);
        final List<LabourTax> labourTaxes1 = labourTaxes;
        final long labourTax_local_id = labourTax.getLocal_id();

        Log.e("INSIDE", "INSIDE sendLabourTaxesServicePrice -> sending: " +labourTax.toString());


        Call<LabourTax> call = svcAuth.postLabourTaxServicePrice(labourTax);

        call.enqueue(new Callback<LabourTax>() {
            @Override
            public void onResponse(Call<LabourTax> call, Response<LabourTax> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("labourTax REVEIVED", responsefinal.body().toString());
                            LabourTax labourTax1 = (LabourTax) responsefinal.body();
                            labourTax1.setLocal_id(labourTax_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveLabourTaxServicePriceFromServer(labourTax1);

                            labourTaxes1.remove(0);
                            if(!labourTaxes1.isEmpty()){
                                sendLabourTaxesServicePrice(labourTaxes1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.LABOURTAX_SERVICEPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<LabourTax> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    static public void sendVariableCostsServicePrice(List<VariableCost> variableCosts, final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final VariableCost variableCost = variableCosts.get(0);
        final List<VariableCost> variableCosts1 = variableCosts;
        final long variableCost_local_id = variableCost.getLocal_id();

        Log.e("INSIDE", "INSIDE sendVariableCostsServicePrice -> sending: " +variableCost.toString());


        Call<VariableCost> call = svcAuth.postVariableCostServicePrice(variableCost);

        call.enqueue(new Callback<VariableCost>() {
            @Override
            public void onResponse(Call<VariableCost> call, Response<VariableCost> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("variableCost REVEIVED", responsefinal.body().toString());
                            VariableCost variableCost1 = (VariableCost) responsefinal.body();
                            variableCost1.setLocal_id(variableCost_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveVariableCostServicePriceFromServer(variableCost1);

                            variableCosts1.remove(0);
                            if(!variableCosts1.isEmpty()){
                                sendVariableCostsServicePrice(variableCosts1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.VARIABLECOST_SERVICEPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<VariableCost> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    /*
                                PRODUCT PRICE
     */
    static public void sendProductPrices(List<ProductPrice> productPrices, final  Context context) {
       try{
        SvcApi svcAuth = Svc.initAuth(context);

        final ProductPrice productPrice = productPrices.get(0);
        final List<ProductPrice> productPrices1 = productPrices;
        final long productPrice_local_id = productPrice.getLocal_id();

        Log.e("INSIDE", "INSIDE sendProductPrices -> sending: " + productPrice.toString());


        Call<ProductPrice> call = svcAuth.postProductPrice(productPrice);

        call.enqueue(new Callback<ProductPrice>() {
            @Override
            public void onResponse(Call<ProductPrice> call, Response<ProductPrice> response) {
                final Response responsefinal = response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("productPrice REVEIVED", responsefinal.body().toString());
                            ProductPrice productPrice1 = (ProductPrice) responsefinal.body();
                            productPrice1.setLocal_id(productPrice_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveProductpriceFromServer(productPrice1);

                            productPrices1.remove(0);
                            if (!productPrices1.isEmpty()) {
                                sendProductPrices(productPrices1, context);
                            } else {
                                unblockBlocker(Defaultdata.Blocker.PRODUCTPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try {

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ProductPrice> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }catch(Exception e){e.printStackTrace();}
    }
    static public void sendRawsProductPrice(List<Raw> raws, final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final Raw raw = raws.get(0);
        final List<Raw> raws1 = raws;
        final long raw_local_id = raw.getLocal_id();

        Log.e("INSIDE", "INSIDE sendRawsProductPrice -> sending: " +raw.toString());


        Call<Raw> call = svcAuth.postRawProductPrice(raw);

        call.enqueue(new Callback<Raw>() {
            @Override
            public void onResponse(Call<Raw> call, Response<Raw> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("raw REVEIVED", responsefinal.body().toString());
                            Raw raw1 = (Raw) responsefinal.body();
                            raw1.setLocal_id(raw_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveRawProductPriceFromServer(raw1);

                            raws1.remove(0);
                            if(!raws1.isEmpty()){
                                sendRawsProductPrice(raws1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.RAW_PRODUCTPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<Raw> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    static public void sendFixedVariableCostsProductPrice(List<FixedVariableCost> fixedVariableCosts,
                                                          final  Context context){
        SvcApi svcAuth = Svc.initAuth(context);

        final FixedVariableCost fixedVariableCost = fixedVariableCosts.get(0);
        final List<FixedVariableCost> fixedVariableCosts1 = fixedVariableCosts;
        final long fixedVariableCost_local_id = fixedVariableCost.getLocal_id();

        Log.e("INSIDE", "INSIDE sendFixedVariableCostsProductPrice -> sending: " +fixedVariableCost.toString());


        Call<FixedVariableCost> call = svcAuth.postFixedVariableCostProductPrice(fixedVariableCost);

        call.enqueue(new Callback<FixedVariableCost>() {
            @Override
            public void onResponse(Call<FixedVariableCost> call, Response<FixedVariableCost> response) {
                final Response responsefinal =  response;

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("FixedVarCost REVEIVED", responsefinal.body().toString());
                            FixedVariableCost fixedVariableCost1 = (FixedVariableCost) responsefinal.body();
                            fixedVariableCost1.setLocal_id(fixedVariableCost_local_id);

                            DatabaseManager db = new DatabaseManager(context);
                            db.saveFixedVariableCostProductPriceFromServer(fixedVariableCost1);

                            fixedVariableCosts1.remove(0);
                            if(!fixedVariableCosts1.isEmpty()){
                                sendFixedVariableCostsProductPrice(fixedVariableCosts1, context);
                            }else{
                                unblockBlocker(Defaultdata.Blocker.FIXEDVARIABLECOST_PRODUCTPRICE, context);
                            }

                        }
                    });
                } else {
                    unblockBlocker(Defaultdata.Blocker.FORCE_UNBLOCK, context);

                    try{

                        //    Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<FixedVariableCost> call, Throwable t) {
                t.printStackTrace();
                //    Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}