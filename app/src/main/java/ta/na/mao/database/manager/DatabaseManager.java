package ta.na.mao.database.manager;


import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.models.Blocker;
import ta.na.mao.database.models.Goal;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Task;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.database.models.User;
import ta.na.mao.database.models.UserDetails;
import ta.na.mao.database.models.productprice.FixedVariableCost;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.productprice.Raw;
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.database.models.serviceprice.VariableCost;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import android.app.Activity;
import android.content.Context;
import android.util.Log;



public class DatabaseManager {

    private static DatabaseManager instance;
    private DatabaseHelper helper = null;
    Context context;

    public static void init(Context ctx) {

        if (null == instance) {
            instance = new DatabaseManager(ctx);

        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
        this.context =  ctx;
    }

    private DatabaseHelper getHelper() {

        return helper;
    }

    public void closeDB() {
        Log.e("closing HelperDB", "closing HelperDB");
        if (helper != null) {
            helper.close();
        }
    }

    /**
     * @return
     */

    /*
                        BLOCKER
     */


    public CreateOrUpdateStatus saveBlocker(Blocker blocker) {
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getBlockerDao().createOrUpdate(blocker);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("blocker saved ok");
        return i;
    }
    public Blocker findBlocker() {
        List<Blocker> list = null;
        try {
            list = getHelper().getBlockerDao().queryBuilder()
                    .orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            return list.get(0);
        }

    }

    public boolean ExistsBlocker() {
        boolean exist = true;
        List<Blocker> list = null;
        try {
            list = getHelper().getBlockerDao().queryBuilder()
                    .orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (list == null || list.isEmpty()) {
            exist = false;
        }
        return exist;
    }

    public void removeBlocker() {
        Blocker p = findBlocker();

        if (p != null) {
            try {
                getHelper().getBlockerDao().delete(p);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



//SINCRO

    public CreateOrUpdateStatus saveUser(User au) {
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getUserDao().createOrUpdate(au);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("register saved ok");
        return i;
    }
    public User findUser() {
        List<User> list = null;
        try {
            list = getHelper().getUserDao().queryBuilder()
                    .orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            return list.get(0);
        }

    }

    public boolean ExistsUser() {
        boolean exist = true;
        List<User> list = null;
        try {
            list = getHelper().getUserDao().queryBuilder()
                    .orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (list == null || list.isEmpty()) {
            exist = false;
        }
        return exist;
    }
    public boolean UserIslogged(){
        boolean logged = false;
        if(ExistsUser()){
            if(findUser().isLogged()){
                logged = true;
            }
        }
        return logged;
    }
    public void removeUser() {
        User p = findUser();

        if (p != null) {
            try {
                getHelper().getUserDao().delete(p);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public CreateOrUpdateStatus saveUserDetailsFromLocal(UserDetails userDetails) {
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getUserDetailsDao().createOrUpdate(userDetails);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("userDetails saved from local ok");
        startUpdateService();
        return i;
    }
    public CreateOrUpdateStatus saveUserDetailsFromServer(UserDetails userDetails) {
        CreateOrUpdateStatus i = null;
        if(findUser() != null){
            User user = findUser();
            user.setUserid(userDetails.getUserid());
        }
        UserDetails userDetailsSavedLocally = findUserDetailsByServerId(userDetails.getId());

        if(userDetailsSavedLocally != null && userDetailsSavedLocally.getId() != 0){
            userDetails.setLocal_id(userDetailsSavedLocally.getLocal_id());
        }

        try {
            i = getHelper().getUserDetailsDao().createOrUpdate(userDetails);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("userDetails saved from server ok");
        return i;
    }
    public List<UserDetails> findUserDetailsNotUpdated(){
        List<UserDetails> userDetails = new ArrayList<>();

        try{
            userDetails = getHelper().getUserDetailsDao().queryBuilder().where().eq("updated", false).query();
        }catch (SQLException e){e.printStackTrace();}

        return userDetails;
    }
    public UserDetails findUserDetailsByServerId(long server_id){
        List<UserDetails> userDetails = new ArrayList<>();

        try{
            userDetails = getHelper().getUserDetailsDao().queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(userDetails != null && !userDetails.isEmpty()){
            return  userDetails.get(0);
        }else{
            return null;
        }
    }
    public UserDetails findUserDetails() {
        List<UserDetails> list = null;
        try {
            list = getHelper().getUserDetailsDao().queryBuilder()
                    .orderBy("local_id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            return list.get(0);
        }

    }

    public boolean ExistsUserDetails() {
        boolean exist = true;
        List<UserDetails> list = null;
        try {
            list = getHelper().getUserDetailsDao().queryBuilder()
                    .orderBy("local_id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (list == null || list.isEmpty()) {
            exist = false;
        }
        return exist;
    }

    public void removeUserDetails() {
        UserDetails p = findUserDetails();

        if (p != null) {
            try {
                getHelper().getUserDetailsDao().delete(p);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
                TRANSACTIONS
     */

    public CreateOrUpdateStatus saveTransactionFromLocal(Transaction transaction){
        transaction.setUpdated(false);
        Log.e("saveTransFromLocal", transaction.toString());
        CreateOrUpdateStatus a = null;
        try {
            a = getHelper().getTransactionDao().createOrUpdate(transaction);

        } catch (SQLException e) {e.printStackTrace();}

        if(transaction.isInstallment()){
            DecimalFormat newFormat = new DecimalFormat("0.00");
            double totalToPay = transaction.getValue() - transaction.getEntrance_payment();
            double currentToPay = totalToPay;
            double valorParcela = Double.parseDouble(
                    newFormat.format(totalToPay/transaction.getTotalinstallments()).replace(",","."));



            for(int i=0; i< transaction.getTotalinstallments(); i++){
                Installment installment = new Installment();
                installment.setNumber(i + 1);
                installment.setTransactionlocalid(transaction.getLocal_id());
                installment.setPaid(false);
                installment.setIncome(transaction.isIncome());

                Calendar c = Calendar.getInstance();
                c.setTime(transaction.getFirstinstallment());
                if(i == 0){
                    installment.setDate(transaction.getFirstinstallment());
                }else{
                    if(transaction.getFrequencyinstallment() != 30){
                        c.add(Calendar.DATE, transaction.getFrequencyinstallment() * i);
                    }else if(transaction.getFrequencyinstallment() == 30){
                        c.add(Calendar.MONTH, i);
                    }
                    installment.setDate(c.getTime());
                }

                if(valorParcela >= currentToPay){
                    installment.setValue(currentToPay);
                }else{
                    installment.setValue(valorParcela);
                }
                currentToPay -= valorParcela;
                saveInstallmentFromLocal(installment);


            }
        }
        startUpdateService();
        return a;
    }
    public CreateOrUpdateStatus saveTransactionFromServer(Transaction transaction){
        CreateOrUpdateStatus a = null;
        Transaction transactionSavedLocally = findTransactionByServerId(transaction.getId());
        if(transactionSavedLocally != null && transactionSavedLocally.getLocal_id() != 0){
            transaction.setLocal_id(transactionSavedLocally.getLocal_id());
        }
        try {
            a = getHelper().getTransactionDao().createOrUpdate(transaction);
            Log.e("SaveTransFromServer",
                    "My id = " + transaction.getId() + " | My local_id = " + transaction.getLocal_id());

        } catch (SQLException e) {e.printStackTrace();}

        if(transaction.isInstallment()){
            Transaction transactionLocal = findTransactionByLocalid(transaction.getLocal_id());
            if( transactionLocal != null){
                List<Installment> installmentsLocal = transactionLocal.getInstallments();
                for(int i=0; i< transaction.getInstallments().size(); i++){
                    long installment_local_id = 0;
                    for(int j=0; j< installmentsLocal.size(); j++){
                        if(installmentsLocal.get(j).getDate().compareTo(transaction.getInstallments().get(i).getDate()) == 0){
                            installment_local_id = installmentsLocal.get(j).getLocal_id();
                            break;
                        }
                    }
                    Installment installment = transaction.getInstallments().get(i);
                    installment.setLocal_id(installment_local_id);
                    installment.setTransactionlocalid(transaction.getLocal_id());
                    saveInstallmentFromServer(installment);
                }
            }else{
                for(int i=0; i< transaction.getInstallments().size(); i++){
                    Installment installment = transaction.getInstallments().get(i);

                    installment.setTransactionlocalid(transaction.getLocal_id());
                    saveInstallmentFromServer(installment);
                }
            }

        }
        return a;
    }
    public List<Transaction> findTransactionsByDateOneMonthToCurrentDay(Date date){


        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Log.e("get", date.toString());
        List<Transaction> transactions = new ArrayList<>();
        List<Installment> installments = new ArrayList<>();

        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        if(lastDayDate.getTimeInMillis() > new Date().getTime()){
            lastDayDate.setTime(new Date());
        }

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", lastDayString);
        try{
            transactions.addAll(getHelper().getTransactionDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime()).query() );
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i<transactions.size(); i++){
            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("transactionlocalid", transactions.get(i).getLocal_id()).query();
            }catch (Exception e){e.printStackTrace();}
            transactions.get(i).setInstallments(installments);
        }

        Log.e("FIND[]", transactions.toString());
        Log.e("FINDALL", findAllTransactions().toString());
        return transactions;

    }
    public List<Transaction> findTransactionsByDateOneMonth(Date date){


        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Log.e("get", date.toString());
        List<Transaction> transactions = new ArrayList<>();
        List<Installment> installments = new ArrayList<>();

        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);


  //      firstDayDate.add(Calendar.MONTH, -1);
  //      firstDayDate.set(Calendar.DAY_OF_MONTH, -1);
  //      firstDayString = sdf.format(firstDayDate.getTime());
  //      Log.e("inicio in2", firstDayString);

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", lastDayString);
        try{
            transactions.addAll(getHelper().getTransactionDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime()).query() );
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i<transactions.size(); i++){
            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("transactionlocalid", transactions.get(i).getLocal_id()).query();
            }catch (Exception e){e.printStackTrace();}
            transactions.get(i).setInstallments(installments);
        }

        Log.e("FIND[]", transactions.toString());
       Log.e("FINDALL", findAllTransactions().toString());
        return transactions;

    }
    public List<Transaction> findTransactionsBetweenDateAndOneMonth(Date date){


        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Log.e("get", date.toString());
        List<Transaction> transactions = new ArrayList<>();
        List<Installment> installments = new ArrayList<>();

        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", firstDayString);



        try{
            transactions.addAll(getHelper().getTransactionDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),date.getTime()).query() );
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i<transactions.size(); i++){
            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("transactionlocalid", transactions.get(i).getLocal_id()).query();
            }catch (Exception e){e.printStackTrace();}
            transactions.get(i).setInstallments(installments);
        }

        Log.e("FIND[]", transactions.toString());
        Log.e("FINDALL", findAllTransactions().toString());
        return transactions;

    }
    public List<Transaction> findTransactionsByDateOneMonthAndCategoryAndIncomeToCurrentDay(Date date, int category, boolean income){


        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Log.e("get", date.toString());
        List<Transaction> transactions = new ArrayList<>();
        List<Installment> installments = new ArrayList<>();

        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        if(lastDayDate.getTimeInMillis() > new Date().getTime()){
            lastDayDate.setTime(new Date());
        }

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", lastDayString);
        try{
            transactions.addAll(getHelper().getTransactionDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime())
                    .and().eq("income", income)
                    .and().eq("category", category).query() );
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i<transactions.size(); i++){
            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("local_id", transactions.get(i).getLocal_id()).query();
            }catch (Exception e){e.printStackTrace();}
            transactions.get(i).setInstallments(installments);
        }

        Log.e("FIND[]", transactions.toString());
        Log.e("FINDALL", findAllTransactions().toString());
        return transactions;

    }
    public List<Transaction> findTransactionsByDateOneMonthAndCategoryAndIncome(Date date, int category, boolean income){


        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Log.e("get", date.toString());
        List<Transaction> transactions = new ArrayList<>();
        List<Installment> installments = new ArrayList<>();

        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", lastDayString);
        try{
            transactions.addAll(getHelper().getTransactionDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime())
                    .and().eq("income", income)
                            .and().eq("category", category).query() );
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i<transactions.size(); i++){
            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("local_id", transactions.get(i).getLocal_id()).query();
            }catch (Exception e){e.printStackTrace();}
            transactions.get(i).setInstallments(installments);
        }

        Log.e("FIND[]", transactions.toString());
        Log.e("FINDALL", findAllTransactions().toString());
        return transactions;

    }
    public Transaction findTransactionByLocalid(long local_id) {
        List<Transaction> list = null;
        try {
            list = getHelper().getTransactionDao().queryBuilder()
                    .where().eq("local_id", local_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            List<Installment> installments = new ArrayList<>();

            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("transactionlocalid", local_id).query();
                Collections.sort(installments, new Comparator<Installment>() {
                    public int compare(Installment i1, Installment i2) {
                        return i1.getNumber() - i2.getNumber();

                    }
                });
            }catch (SQLException ee){
                ee.printStackTrace();
            }
            list.get(0).setInstallments(installments);
            return list.get(0);
        }

    }
    public Transaction findTransactionByServerId(long server_id) {
        List<Transaction> list = null;
        try {
            list = getHelper().getTransactionDao().queryBuilder()
                    .where().eq("id", server_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            List<Installment> installments = new ArrayList<>();

            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("transactionlocalid", list.get(0).getLocal_id()).query();
            }catch (SQLException ee){
                ee.printStackTrace();
            }
            list.get(0).setInstallments(installments);
            return list.get(0);
        }

    }
    public List<Transaction> findTransactionBeforeDate(Date date){
        List<Transaction> transactions = new ArrayList<>();
        List<Installment> installments = new ArrayList<>();

        try{
            transactions.addAll(getHelper().getTransactionDao().queryBuilder().where().le(
                    "date",date).query() );
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i<transactions.size(); i++){
            try{
                installments = getHelper().getInstallmentDao().queryBuilder().where().eq("local_id", transactions.get(i).getLocal_id()).query();
            }catch (Exception e){e.printStackTrace();}
            transactions.get(i).setInstallments(installments);
        }
        return transactions;
    }
    public List<Transaction> findTransactionsNotUpdated() {
        List<Transaction> list = null;
        try {
            list = getHelper().getTransactionDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{
            for(int i = 0; i < list.size(); i++) {
                List<Installment> installments = new ArrayList<>();

                try {
                    installments = getHelper().getInstallmentDao().queryBuilder().where()
                            .eq("transactionlocalid", list.get(i).getLocal_id()).query();
                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
                list.get(i).setInstallments(installments);
            }
            return list;
        }

    }

public List<Transaction> findTransactionsByPeriodSixMonths(int period, int year){

    List<Transaction> transactions = new ArrayList<>();
    List<Installment> installments = new ArrayList<>();

    Calendar  firstDate = Calendar.getInstance();
    Calendar lastDate = Calendar.getInstance();

    if(period == 1){

        firstDate.set(year, 0, 1);

        lastDate.set(year, 8, 1);
        lastDate.add(Calendar.DAY_OF_MONTH, -1);

    }else{
        firstDate.set(year, 8, 1);

        lastDate.set(year + 1, 0, 1);
        lastDate.add(Calendar.DAY_OF_MONTH, -1);
    }

    Log.e("TRANSACTIONS", "FirstDate by Period[" + period + "] = " + firstDate);
    Log.e("TRANSACTIONS", "LastDate by Period[" + period + "] = " + lastDate);

    try{
        transactions.addAll(getHelper().getTransactionDao().queryBuilder().where().between(
                "date",firstDate.getTime(),lastDate.getTime()).query() );
    }catch (Exception e){e.printStackTrace();}

    for(int i=0; i<transactions.size(); i++){
        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().eq("transactionlocalid", transactions.get(i).getLocal_id()).query();
        }catch (Exception e){e.printStackTrace();}
        transactions.get(i).setInstallments(installments);
    }

    Log.e("FIND BY PERIOD", "period[" + period + "] = size-> "+ transactions.size() + " -> " +transactions.toString());
    Log.e("FINDALL", findAllTransactions().toString());
    return transactions;

}

    public List<Transaction> findAllTransactions(){
        List<Transaction> transactions = new ArrayList<>();
        try{
            transactions = getHelper().getTransactionDao().queryBuilder()
                    .orderBy("local_id", false).query();
        }catch (Exception e){e.printStackTrace();}

        return transactions;


    }
    public void removeTransaction(Transaction transaction){
        boolean ok = true;

        try {
            getHelper().getTransactionDao().delete(transaction);
            Log.e("DataBase","removeTransaction " + transaction.getLocal_id() +
                    " | installments.size() = " + transaction.getInstallments().size());
        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(ok && transaction.isUpdated()){
            Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
            task.setDelete(true);
            task.setTransaction(true);
            task.setTransaction_id(transaction.getId());
            saveTask(task);
        }
        for(Installment installment : transaction.getInstallments()){
            Log.e("DataBase", "going to RemoveInstallment... " + installment.getLocal_id());
            removeInstallment(installment);
        }
        startUpdateService();
    }


    public void removeTransactions() {
        List<Transaction> p = findAllTransactions();

        if (p != null) {
            try {
                getHelper().getTransactionDao().delete(p);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
      //  startUpdateService();
    }

    /*
                INSTALLMENT
     */
    public CreateOrUpdateStatus saveInstallmentFromLocal(Installment installment){
        installment.setUpdated(false);
        Log.e("savingInstallment","savingInstallment from local " +  installment.toString());
        CreateOrUpdateStatus i = null;
        if(!findInstallmentByTransactionLocalIdAndSameInstallmentDate(installment) || installment.getId() != 0){
            try {
                i = getHelper().getInstallmentDao().createOrUpdate(installment);

            } catch (SQLException e) {e.printStackTrace();}
        }else{
            Log.e("saveInstallment", "from Local REPEATED AND NOT COPIED");
        }


      //  startUpdateService();

        return i;
    }
    public CreateOrUpdateStatus saveInstallmentFromLocalUpdatedNow(Installment installment, boolean updatenow){
        installment.setUpdated(false);
        Log.e("savingInstallment","savingInstallment from local " +  installment.toString());
        CreateOrUpdateStatus i = null;
            try {
                i = getHelper().getInstallmentDao().createOrUpdate(installment);

            } catch (SQLException e) {e.printStackTrace();}


        if(updatenow){
            startUpdateService();
        }


        return i;
    }

    public boolean findInstallmentByTransactionLocalIdAndSameInstallmentDate(Installment installment){
        List<Installment> installments = new ArrayList<>();
        try{
            installments =   getHelper().getInstallmentDao().queryBuilder().where()
                    .eq("transactionlocalid", installment.getTransactionlocalid())
                    .and().eq("date", installment.getDate()).query();
        }catch(Exception e){e.printStackTrace();}

        if(installments == null || installments.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    public void saveInstallmentFromServer(Installment installment){
        Transaction transaction = findTransactionByServerId(installment.getTransactionid());
        Installment installmentSavedLocally = findInstallmentByIdServer(installment.getId());

        if(installmentSavedLocally != null && installmentSavedLocally.getLocal_id() != 0){
            installmentSavedLocally.setLocal_id(installmentSavedLocally.getLocal_id());
        }
        if(transaction != null && transaction.getLocal_id() != 0){
            installment.setTransactionlocalid(transaction.getLocal_id());

            try{
                getHelper().getInstallmentDao().createOrUpdate(installment);
            }catch (SQLException e){ e.printStackTrace(); }
        }
    }
    public Installment findInstallmentByIdServer(long server_id){
        List<Installment> installments = new ArrayList<>();

        try{
            installments =  getHelper().getInstallmentDao().queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(installments != null && !installments.isEmpty()){
            return installments.get(0);
        }else{
            return null;
        }
    }

    public List<Installment> findInstallmentBeforeAndEqual(Date date){
        List<Installment> installments = new ArrayList<>();
        Transaction transaction = new Transaction();

        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().le(
                    "date",date).and().eq("paid", false).query();
            Collections.sort(installments, new Comparator<Installment>() {
                public int compare(Installment i1, Installment i2) {
                    return i2.getDate().compareTo(i1.getDate());

                }
            });
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i< installments.size(); i++){
            try{
                transaction = getHelper().getTransactionDao().queryBuilder().where().eq("local_id", installments.get(i).getTransactionlocalid()).queryForFirst();
            }catch (Exception e){e.printStackTrace();}
            installments.get(i).setTransaction(transaction);
            transaction = new Transaction();
        }
        Log.e("INSTALLMENTS","findInstallmentBeforeoreAndEqual " + installments.toString());
        return installments;
    }
    public List<Installment> findInstallmentByDateOneMonthToCurrentDay(Date date){
        List<Installment> installments = new ArrayList<>();
        Transaction transaction = new Transaction();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", "INSTALLMENT" + firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);


        if(lastDayDate.getTimeInMillis() > new Date().getTime()){
            lastDayDate.setTime(new Date());
        }

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", "INSTALLMENT" + lastDayString);
        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime()).query();
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i< installments.size(); i++){
            try{
                transaction = getHelper().getTransactionDao().queryBuilder().where().eq("local_id", installments.get(i).getTransactionlocalid()).queryForFirst();
            }catch (Exception e){e.printStackTrace();}
            installments.get(i).setTransaction(transaction);
            transaction = new Transaction();
        }
        Log.e("INSTALLMENTS", findAllInstallments().toString());
        return installments;
    }
    public List<Installment> findInstallmentByDateOneMonth(Date date){
        List<Installment> installments = new ArrayList<>();
        Transaction transaction = new Transaction();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", "INSTALLMENT" + firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", "INSTALLMENT" + lastDayString);
        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime()).query();
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i< installments.size(); i++){
            try{
                transaction = getHelper().getTransactionDao().queryBuilder().where().eq("local_id", installments.get(i).getTransactionlocalid()).queryForFirst();
            }catch (Exception e){e.printStackTrace();}
            installments.get(i).setTransaction(transaction);
            transaction = new Transaction();
        }
        Log.e("INSTALLMENTS", findAllInstallments().toString());
        return installments;
    }
    public List<Installment> findInstallmentsBetweenDateAndOneMonth(Date date){
        List<Installment> installments = new ArrayList<>();
        Transaction transaction = new Transaction();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", "INSTALLMENT" + firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", "INSTALLMENT" + lastDayString);
        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime()).query();
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i< installments.size(); i++){
            try{
                transaction = getHelper().getTransactionDao().queryBuilder().where().eq("local_id", installments.get(i).getTransactionlocalid()).queryForFirst();
            }catch (Exception e){e.printStackTrace();}
            installments.get(i).setTransaction(transaction);
            transaction = new Transaction();
        }
        Log.e("INSTALLMENTS", findAllInstallments().toString());
        return installments;
    }
    public List<Installment> findInstallmentsByDateOneMonthAndCategoryAndIncomeToCurrentDay(Date date, int category, boolean income){
        List<Installment> installments = new ArrayList<>();
        Transaction transaction = new Transaction();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", "INSTALLMENT" + firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        if(lastDayDate.getTimeInMillis() > new Date().getTime()){
            lastDayDate.setTime(new Date());
        }

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", "INSTALLMENT" + lastDayString);
        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime()).query();
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i< installments.size(); i++){
            try{
                transaction = getHelper().getTransactionDao().queryBuilder().where().eq("local_id", installments.get(i).getTransactionlocalid()).queryForFirst();
            }catch (Exception e){e.printStackTrace();}
            installments.get(i).setTransaction(transaction);
            transaction = new Transaction();
        }
        List<Installment> installmentsByCategory = new ArrayList<>();
        for(Installment installment : installments){
            if(installment != null && installment.getTransaction() != null
                    && installment.getTransaction().getCategory() == category){
                installmentsByCategory.add(installment);
            }
        }
        Log.e("INSTALLMENTS", "installments with category [" + category +"] = "+ installmentsByCategory.toString());
        return installmentsByCategory;
    }
    public List<Installment> findInstallmentsByDateOneMonthAndCategoryAndIncome(Date date, int category, boolean income){
        List<Installment> installments = new ArrayList<>();
        Transaction transaction = new Transaction();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", "INSTALLMENT" + firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", "INSTALLMENT" + lastDayString);
        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().between(
                    "date",firstDayDate.getTime(),lastDayDate.getTime()).query();
        }catch (Exception e){e.printStackTrace();}

        for(int i=0; i< installments.size(); i++){
            try{
                transaction = getHelper().getTransactionDao().queryBuilder().where().eq("local_id", installments.get(i).getTransactionlocalid()).queryForFirst();
            }catch (Exception e){e.printStackTrace();}
            installments.get(i).setTransaction(transaction);
            transaction = new Transaction();
        }
        List<Installment> installmentsByCategory = new ArrayList<>();
        for(Installment installment : installments){
            if(installment != null && installment.getTransaction() != null
                    && installment.getTransaction().getCategory() == category){
                installmentsByCategory.add(installment);
            }
        }
        Log.e("INSTALLMENTS", "installments with category [" + category +"] = "+ installmentsByCategory.toString());
        return installmentsByCategory;
    }

    public List<Installment> findInstallmentsByPeriodSixMonths(int period, int year){

        List<Installment> installments = new ArrayList<>();

        Calendar  firstDate = Calendar.getInstance();
        Calendar lastDate = Calendar.getInstance();

        if(period == 1){

            firstDate.set(year, 0, 1);

            lastDate.set(year, 8, 1);
            lastDate.add(Calendar.DAY_OF_MONTH, -1);

        }else{
            firstDate.set(year, 8, 1);

            lastDate.set(year + 1, 0, 1);
            lastDate.add(Calendar.DAY_OF_MONTH, -1);
        }

        Log.e("INSTALLMENTS", "FirstDate by Period[" + period + "] = " + firstDate);
        Log.e("INSTALLMENTS", "LastDate by Period[" + period + "] = " + lastDate);

        try{
            installments.addAll(getHelper().getInstallmentDao().queryBuilder().where().between(
                    "date",firstDate.getTime(),lastDate.getTime()).query() );
        }catch (Exception e){e.printStackTrace();}



        Log.e("FIND BY PERIOD", "period[" + period + "] = size-> "+ installments.size() + " -> " +installments.toString());
        Log.e("FINDALL", findAllInstallments().toString());
        return installments;

    }
    public List<Installment> findAllInstallments(){
        List<Installment> installments = new ArrayList<>();
        try{
            installments = getHelper().getInstallmentDao().queryBuilder()
                    .orderBy("local_id", false).query();
        }catch (Exception e){e.printStackTrace();}

        return installments;


    }
    public void removeInstallments() {
        List<Installment> p = findAllInstallments();

        if (p != null) {
            for(Installment installment : p){
                removeInstallment(installment);
            }
        }
     //   startUpdateService();
    }
    public void removeInstallmentsByTransactionLocalId( long transaction_local_id){
        List<Installment> installments = new ArrayList<>();

        try{
            installments = getHelper().getInstallmentDao().queryBuilder().where().eq("transactionlocalid", transaction_local_id).query();
        }catch (SQLException e){e.printStackTrace();}
        for(Installment installment : installments){
            removeInstallment(installment);

        }
        startUpdateService();
    }
    public void removeInstallment(Installment installment){
        boolean ok = true;
        try{
            getHelper().getInstallmentDao().delete(installment);
            Log.e("DataBase", "removeInstallment -> local_id = " + installment.getLocal_id());
        }catch (SQLException e){ e.printStackTrace(); }
        /*if(ok && installment.isUpdated()){
            Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0,
                    false, true, false, true, false );
            task.setInstallment_id(installment.getId());
            saveTask(task);
        } */
    }
    public List<Installment> findInstallmentsNotUpdated() {
        List<Installment> list = null;
        try {
            list = getHelper().getInstallmentDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }

    public List<Installment>  findInstallmentsPaidBeforeDate(Date date){
        List<Installment> installments = new ArrayList<>();

        try{
            installments = getHelper().getInstallmentDao().queryBuilder().
                    where().le("date", date).and().
                    eq("paid", true).query();
            for(Installment installment : installments){
                List<Transaction> transactions = getHelper().getTransactionDao().queryBuilder()
                        .where().eq("local_id", installment.getTransactionlocalid()).query();
                if(transactions != null && !transactions.isEmpty()){
                    installment.setTransaction(transactions.get(0));
                }
            }
        }catch (SQLException e){e.printStackTrace();}

        return installments;

    }


    /*

        GOALS

     */

    public List<Goal> findGoalsByDateOneMonth(Date date){
        List<Goal> goals = new ArrayList<>();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //first day of month
        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(date);
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);
        String firstDayString = sdf.format(firstDayDate.getTime());
        Log.e("inicio in", "GOAL " + firstDayString);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        String lastDayString = sdf.format(lastDayDate.getTime());
        Log.e("find fin", "GOAL " + lastDayString);
        try{
            goals = getHelper().getGoalDao().queryBuilder().where().between(
                    "firstdate",firstDayDate.getTime(),lastDayDate.getTime()).query();
        }catch (Exception e){e.printStackTrace();}


        Log.e("GOALS ", findAllGoals().toString());
        return goals;
    }
    public void removeGoals() {
        List<Goal> p = findAllGoals();

        if (p != null) {
            for(Goal goal : p){
                removeGoalForLogout(goal);
            }
        }
    }

    public void removeGoalForLogout(Goal goal) {
        Log.e("removeGoal","removeGoal goal " + goal.toString());
        if(goal != null && goal.getId() != 0) goal = findGoalByLocalid(goal.getLocal_id());
        Log.e("removeGoal", "after found in database = " + goal.toString());
        if (goal != null) {
            boolean ok = true;
            try {
                getHelper().getGoalDao().delete(goal);

            } catch (SQLException e) {
                e.printStackTrace();
                ok = false;
            }

        }
    }
    public void removeGoal(Goal goal) {
        Log.e("removeGoal","removeGoal goal " + goal.toString());
        if(goal != null && goal.getId() != 0) goal = findGoalByLocalid(goal.getLocal_id());
        Log.e("removeGoal", "after found in database = " + goal.toString());
        if (goal != null) {
            boolean ok = true;
            try {
                getHelper().getGoalDao().delete(goal);

            } catch (SQLException e) {
                e.printStackTrace();
                ok = false;
            }
            if(ok && goal.isUpdated()){
                Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
                task.setDelete(true);
                task.setGoal(true);
                task.setGoal_id(goal.getId());
                saveTask(task);
            }
        }
        startUpdateService();
    }


    public List<Goal> findAllGoals(){
        List<Goal> goals = new ArrayList<>();
        try{
            goals = getHelper().getGoalDao().queryBuilder()
                    .orderBy("local_id", false).query();
        }catch (Exception e){e.printStackTrace();}

        return goals;


    }

    public void saveGoalFromLocal(Goal goal){
        goal.setUpdated(false);
        try {
            getHelper().getGoalDao().createOrUpdate(goal);
        } catch (SQLException e) {e.printStackTrace();}
        startUpdateService();
    }
    public void saveGoalFromLocal(Goal goal, boolean notifyagain){
        goal.setUpdated(false);
        goal.setNotificated(!notifyagain);
        goal.setPreviewNotificated(!notifyagain);
        try {
            getHelper().getGoalDao().createOrUpdate(goal);
        } catch (SQLException e) {e.printStackTrace();}
        Log.e("saveGoalFromLocal", "saveGoalFromLocal -> goal " + goal.toString());
        startUpdateService();
    }
    public void saveGoalFromServer(Goal goal){
        Log.e("SAVE GOAL", "saveGoalFromServer " + goal.toString());
        Goal goalSavedLocally = findGoalByServerid(goal.getId());

        if(goalSavedLocally != null && goalSavedLocally.getId() != 0){
            goal.setLocal_id(goalSavedLocally.getLocal_id());
        }
        Log.e("SAVE GOAL", "saveGoalFromServer AFTER LOCAL_ID added = " + goal.toString());
        try {
            getHelper().getGoalDao().createOrUpdate(goal);
        } catch (SQLException e) {e.printStackTrace();}

    }
    public List<Goal> findGoalsByPeriodSixMonths(int period, int year){

        List<Goal> goals = new ArrayList<>();

        Calendar  firstDate = Calendar.getInstance();
        Calendar lastDate = Calendar.getInstance();

        if(period == 1){

            firstDate.set(year, 0, 1);

            lastDate.set(year, 8, 1);
            lastDate.add(Calendar.DAY_OF_MONTH, -1);

        }else{
            firstDate.set(year, 8, 1);

            lastDate.set(year + 1, 0, 1);
            lastDate.add(Calendar.DAY_OF_MONTH, -1);
        }

        Log.e("GOALS", "FirstDate by Period[" + period + "] = " + firstDate);
        Log.e("GOALS", "LastDate by Period[" + period + "] = " + lastDate);

        try{
            goals.addAll(getHelper().getGoalDao().queryBuilder().where().between(
                    "firstdate",firstDate.getTime(),lastDate.getTime()).query() );
        }catch (Exception e){e.printStackTrace();}



        Log.e("FIND BY PERIOD", "period[" + period + "] = size-> "+ goals.size() + " -> " +goals.toString());
        Log.e("FINDALL", findAllInstallments().toString());
        return goals;

    }

    public Goal findGoalByLocalid(long local_id) {
        List<Goal> list = null;
        try {
            list = getHelper().getGoalDao().queryBuilder()
                    .where().eq("local_id", local_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            return list.get(0);
        }

    }
    public Goal findGoalByServerid(long server_id) {
        List<Goal> list = null;
        try {
            list = getHelper().getGoalDao().queryBuilder()
                    .where().eq("id", server_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            return list.get(0);
        }

    }
    public List<Goal> findGoalsNotUpdated() {
        List<Goal> list = null;
        try {
            list = getHelper().getGoalDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }

    /*
                TASK
     */


    public void saveTask(Task task){
        try{
            getHelper().getTaskDao().createOrUpdate(task);
        }catch (SQLException e){e.printStackTrace();}
        Log.e("DataManager", "SavedTask: " + task.toString());
    }
    public List<Task> findTasksNotUpdated() {
        List<Task> list = null;
        try {
            list = getHelper().getTaskDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public List<Task> findAllTasks() {
        List<Task> list = null;
        try {
            list = getHelper().getTaskDao().queryBuilder()
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public void removeTasks(){
        List<Task> p = findAllTasks();

        if (p != null) {
            for(Task task : p){
                removeTask(task);
            }
        }
    }
    public void removeTask(Task task) {
        Log.e("removeTask","removeTask task " + task.toString());

        if (task != null) {
            boolean ok = true;
            try {
                getHelper().getTaskDao().delete(task);

            } catch (SQLException e) {
                e.printStackTrace();
                ok = false;
            }
        }
    }

    /*
                Service Prices
     */

    public long saveServicepriceFromLocal(ServicePrice servicePrice){
        servicePrice.setUpdated(false);

        CreateOrUpdateStatus a = null;
        try {
            a = getHelper().getServicepriceDao().createOrUpdate(servicePrice);

        } catch (SQLException e) {e.printStackTrace();}

        for(LabourCost labourCost : servicePrice.getLabourcosts()){
            labourCost.setServicepricelocalid(servicePrice.getLocal_id());
            saveLabourCostServicePriceFromLocal(labourCost);
        }
        for(LabourTax labourTax : servicePrice.getLabourtaxs()){
            labourTax.setServicepricelocalid(servicePrice.getLocal_id());
            saveLabourTaxServicePriceFromLocal(labourTax);
        }
        for(FixedCost fixedCost : servicePrice.getFixedcosts()){
            fixedCost.setServicepricelocalid(servicePrice.getLocal_id());
            saveFixedCostServicePriceFromLocal(fixedCost);
        }
        for(VariableCost variableCost : servicePrice.getVariablecosts()){
            variableCost.setServicepricelocalid(servicePrice.getLocal_id());
            saveVariableCostServicePriceFromLocal(variableCost);
        }
        if(servicePrice.isFinished()){
            startUpdateService();
        }
        Log.e("saveSerPriFromLocal", "AFTER: " + servicePrice.toString());
        return servicePrice.getLocal_id();
    }

    public CreateOrUpdateStatus saveServicepriceFromServer(ServicePrice servicePrice){
        CreateOrUpdateStatus a = null;
        ServicePrice servicepriceSavedLocally = findServicePriceByServerId(servicePrice.getId());
        if(servicepriceSavedLocally != null && servicepriceSavedLocally.getLocal_id() != 0){
            servicePrice.setLocal_id(servicepriceSavedLocally.getLocal_id());
            List<FixedCost>  fixedCosts = findFixedCostsServicePriceByServicePriceLocalId(servicepriceSavedLocally.getLocal_id());
            List<VariableCost> variableCosts = findVariableCostsServicePriceByServicePriceLocalId(
                    servicepriceSavedLocally.getLocal_id());
            List<LabourCost> labourCosts = findLabourCostsServicePriceByServicePriceLocalId(
                    servicepriceSavedLocally.getLocal_id());
            List<LabourTax> labourTaxes = findLabourTaxsServicePriceByServicePriceLocalId(
                    servicepriceSavedLocally.getLocal_id());
            removeFixedCostsServicePrice(fixedCosts, false);
            removeVariableCostsServicePrice(variableCosts, false);
            removeLabourCostsServicePrice(labourCosts, false);
            removeLabourTaxesServicePrice(labourTaxes, false);
        }
        try {
            a = getHelper().getServicepriceDao().createOrUpdate(servicePrice);
            Log.e("SaveTransFromServer",
                    "My id = " + servicePrice.getId() + " | My local_id = " + servicePrice.getLocal_id() + "| " + servicePrice.toString());
            List<FixedCost>  fixedCosts = findFixedCostsServicePriceByServicePriceLocalId(servicePrice.getLocal_id());
            List<VariableCost> variableCosts = findVariableCostsServicePriceByServicePriceLocalId(
                    servicePrice.getLocal_id());
            List<LabourCost> labourCosts = findLabourCostsServicePriceByServicePriceLocalId(
                    servicePrice.getLocal_id());
            List<LabourTax> labourTaxes = findLabourTaxsServicePriceByServicePriceLocalId(
                    servicePrice.getLocal_id());
            removeFixedCostsServicePrice(fixedCosts, false);
            removeVariableCostsServicePrice(variableCosts, false);
            removeLabourCostsServicePrice(labourCosts, false);
            removeLabourTaxesServicePrice(labourTaxes, false);
        } catch (SQLException e) {e.printStackTrace();}

        for(LabourCost labourCost : servicePrice.getLabourcosts()){
            labourCost.setServicepricelocalid(servicePrice.getLocal_id());
            saveLabourCostServicePriceFromServer(labourCost);
        }
        for(LabourTax labourTax : servicePrice.getLabourtaxs()){
            labourTax.setServicepricelocalid(servicePrice.getLocal_id());
            saveLabourTaxServicePriceFromServer(labourTax);
        }
        for(FixedCost fixedCost : servicePrice.getFixedcosts()){
            fixedCost.setServicepricelocalid(servicePrice.getLocal_id());
            saveFixedCostServicePriceFromServer(fixedCost);
        }
        for(VariableCost variableCost : servicePrice.getVariablecosts()){
            variableCost.setServicepricelocalid(servicePrice.getLocal_id());
            saveVariableCostServicePriceFromServer(variableCost);
        }

        return a;
    }
    public ServicePrice findServicePriceByLocalid(long local_id, boolean updateNow) {
        List<ServicePrice> list = null;
        try {
            list = getHelper().getServicepriceDao().queryBuilder()
                    .where().eq("local_id", local_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            List<FixedCost> fixedCosts = new ArrayList<>();
            List<LabourCost> labourCosts = new ArrayList<>();
            List<LabourTax> labourTaxes = new ArrayList<>();
            List<VariableCost> variableCosts = new ArrayList<>();

            try{
                fixedCosts = getHelper().getFixedCostDao().queryBuilder().where().eq("servicepricelocalid", local_id).query();
                labourCosts = getHelper().getLabourcostDao().queryBuilder().where().eq("servicepricelocalid", local_id).query();
                labourTaxes = getHelper().getLabourTaxDao().queryBuilder().where().eq("servicepricelocalid", local_id).query();
                variableCosts = getHelper().getVariableCostDao().queryBuilder().where().eq("servicepricelocalid", local_id).query();

            }catch (SQLException ee){
                ee.printStackTrace();
            }
            list.get(0).setFixedcosts(fixedCosts);
            list.get(0).setLabourcosts(labourCosts);
            list.get(0).setLabourtaxs(labourTaxes);
            list.get(0).setVariablecosts(variableCosts);
            if(updateNow) {
                list.get(0).updateValues(context);
            }
            return list.get(0);
        }

    }

    public List<ServicePrice> findServicePriceNotFinished(){
        List<ServicePrice> list = null;
        try {
            list = getHelper().getServicepriceDao().queryBuilder()
                    .where().eq("finished", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{
            for(int i = 0; i < list.size(); i++) {
                List<FixedCost> fixedCosts = new ArrayList<>();
                List<LabourCost> labourCosts = new ArrayList<>();
                List<LabourTax> labourTaxes = new ArrayList<>();
                List<VariableCost> variableCosts = new ArrayList<>();

                try{
                    fixedCosts = getHelper().getFixedCostDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();
                    labourCosts = getHelper().getLabourcostDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();
                    labourTaxes = getHelper().getLabourTaxDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();
                    variableCosts = getHelper().getVariableCostDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();

                }catch (SQLException ee){
                    ee.printStackTrace();
                }
                list.get(i).setFixedcosts(fixedCosts);
                list.get(i).setLabourcosts(labourCosts);
                list.get(i).setLabourtaxs(labourTaxes);
                list.get(i).setVariablecosts(variableCosts);
                list.get(i).updateValues(context);
            }
            return list;
        }
    }
    public ServicePrice findServicePriceByServerId(long server_id) {
        List<ServicePrice> list = null;
        try {
            list = getHelper().getServicepriceDao().queryBuilder()
                    .where().eq("id", server_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            List<FixedCost> fixedCosts = new ArrayList<>();
            List<LabourCost> labourCosts = new ArrayList<>();
            List<LabourTax> labourTaxes = new ArrayList<>();
            List<VariableCost> variableCosts = new ArrayList<>();

            try{
                fixedCosts = getHelper().getFixedCostDao().queryBuilder().where()
                        .eq("servicepricelocalid", list.get(0).getLocal_id()).query();
                labourCosts = getHelper().getLabourcostDao().queryBuilder().where()
                        .eq("servicepricelocalid", list.get(0).getLocal_id()).query();
                labourTaxes = getHelper().getLabourTaxDao().queryBuilder().where()
                        .eq("servicepricelocalid", list.get(0).getLocal_id()).query();
                variableCosts = getHelper().getVariableCostDao().queryBuilder().where()
                        .eq("servicepricelocalid", list.get(0).getLocal_id()).query();

            }catch (SQLException ee){
                ee.printStackTrace();
            }
            list.get(0).setFixedcosts(fixedCosts);
            list.get(0).setLabourcosts(labourCosts);
            list.get(0).setLabourtaxs(labourTaxes);
            list.get(0).setVariablecosts(variableCosts);
        //    list.get(0).updateValues(context);
            return list.get(0);
        }

    }

    public List<ServicePrice> findServicePricesNotUpdated() {
        List<ServicePrice> list = null;
        try {
            list = getHelper().getServicepriceDao().queryBuilder()
                    .where().eq("updated", false).
                            and().eq("finished", true).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{
            for(int i = 0; i < list.size(); i++) {
                List<FixedCost> fixedCosts = new ArrayList<>();
                List<LabourCost> labourCosts = new ArrayList<>();
                List<LabourTax> labourTaxes = new ArrayList<>();
                List<VariableCost> variableCosts = new ArrayList<>();

                try{
                    fixedCosts = getHelper().getFixedCostDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();
                    labourCosts = getHelper().getLabourcostDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();
                    labourTaxes = getHelper().getLabourTaxDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();
                    variableCosts = getHelper().getVariableCostDao().queryBuilder().where()
                            .eq("servicepricelocalid", list.get(i).getLocal_id()).query();

                }catch (SQLException ee){
                    ee.printStackTrace();
                }
                list.get(i).setFixedcosts(fixedCosts);
                list.get(i).setLabourcosts(labourCosts);
                list.get(i).setLabourtaxs(labourTaxes);
                list.get(i).setVariablecosts(variableCosts);
                list.get(i).updateValues(context);
            }
            return list;
        }

    }


    public List<ServicePrice> findAllServicePrices(){
        List<ServicePrice> servicePrices = new ArrayList<>();
        try{
            servicePrices = getHelper().getServicepriceDao().queryBuilder()
                    .orderBy("local_id", false).query();
        }catch (Exception e){e.printStackTrace();}

        return servicePrices;
    }
    public List<ServicePrice> findAllServicePricesFinished(){
        List<ServicePrice> servicePrices = new ArrayList<>();
        try{
            servicePrices = getHelper().getServicepriceDao().queryBuilder()
                    .where().eq("finished", true).query();
        }catch (Exception e){e.printStackTrace();}

        return servicePrices;
    }


    public void removeServicePrice(ServicePrice servicePrice){
        boolean ok = true;

        try {
            getHelper().getServicepriceDao().delete(servicePrice);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(ok && servicePrice.isUpdated()){
            Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
            task.setTransaction(true);
            task.setDelete(true);
            task.setServiceprice_id(servicePrice.getId());
            saveTask(task);
        }
        for(FixedCost fixedCost : servicePrice.getFixedcosts()){
            Log.e("DataBase", "going to RemoveFixedCost... " + fixedCost.getLocal_id());
            removeFixedCostServicePrice(fixedCost, false);
        }
        for(LabourCost labourCost : servicePrice.getLabourcosts()){
            Log.e("DataBase", "going to RemoveLabourCost... " + labourCost.getLocal_id());
            removeLabourCostServicePrice(labourCost, false);
        }
        for(LabourTax labourTax : servicePrice.getLabourtaxs()){
            Log.e("DataBase", "going to RemoveLabouTax... " + labourTax.getLocal_id());
            removeLabourTaxServicePrice(labourTax, false);
        }
        for(VariableCost variableCost : servicePrice.getVariablecosts()){
            Log.e("DataBase", "going to RemoveVariableCost... " + variableCost.getLocal_id());
            removeVariableCostServicePrice(variableCost, false);
        }
        startUpdateService();
    }


    public void removeServicePrices() {
        List<ServicePrice> p = findAllServicePrices();

        if (p != null) {
            try {
                getHelper().getServicepriceDao().delete(p);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //  startUpdateService();
    }

    /*
            Service Prices - Fixed Costs
     */
    public List<FixedCost> findFixedCostServicePriceNotUpdated() {
        List<FixedCost> list = null;
        try {
            list = getHelper().getFixedCostDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public CreateOrUpdateStatus saveFixedCostServicePriceFromLocal(FixedCost fixedCost){
        fixedCost.setUpdated(false);
        Log.e("savingFixedCost","savingFixedCost from local " +  fixedCost.toString());
        CreateOrUpdateStatus i = null;

        try {
            i = getHelper().getFixedCostDao().createOrUpdate(fixedCost);

        } catch (SQLException e) {e.printStackTrace();}



        //  startUpdateService();

        return i;
    }
    public CreateOrUpdateStatus saveFixedCostServicePriceFromLocalUpdatedNow(FixedCost fixedCost,
                                                                             boolean updatenow){
        fixedCost.setUpdated(false);
        Log.e("savingFixedCost","savingFixedCost from local " +  fixedCost.toString());
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getFixedCostDao().createOrUpdate(fixedCost);

        } catch (SQLException e) {e.printStackTrace();}


        if(updatenow){
            startUpdateService();
        }


        return i;
    }

    public void saveFixedCostServicePriceFromServer(FixedCost fixedCost){
        ServicePrice servicePrice = findServicePriceByServerId(fixedCost.getServicepriceid());
        FixedCost fixedCostSavedLocally = findFixedCostServicePriceByIdServer(fixedCost.getId());

        if(fixedCostSavedLocally != null && fixedCostSavedLocally.getLocal_id() != 0){
            fixedCost.setLocal_id(fixedCostSavedLocally.getLocal_id());
        }
        if(servicePrice != null && servicePrice.getLocal_id() != 0){
            fixedCost.setServicepricelocalid(servicePrice.getLocal_id());

            try{
                getHelper().getFixedCostDao().createOrUpdate(fixedCost);
            }catch (SQLException e){ e.printStackTrace(); }
        }
    }
    public FixedCost findFixedCostServicePriceByIdServer(long server_id){
        List<FixedCost> fixedCosts = new ArrayList<>();

        try{
            fixedCosts =  getHelper().getFixedCostDao().queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(fixedCosts != null && !fixedCosts.isEmpty()){
            return fixedCosts.get(0);
        }else{
            return null;
        }
    }
    public FixedCost findFixedCostServicePriceByLocalId(long local_id){
        List<FixedCost> fixedCosts = new ArrayList<>();

        try{
            fixedCosts =  getHelper().getFixedCostDao().queryBuilder().where().eq("local_id", local_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(fixedCosts != null && !fixedCosts.isEmpty()){
            return fixedCosts.get(0);
        }else{
            return null;
        }
    }
    public List<FixedCost> findFixedCostsServicePriceByServicePriceLocalId(long serviceprice_localid){
        List<FixedCost> fixedCosts = new ArrayList<>();

        try{
            fixedCosts = getHelper().getFixedCostDao().queryBuilder().where()
                    .eq("servicepricelocalid", serviceprice_localid).query();
        }catch (SQLException e){e.printStackTrace();}

        return fixedCosts;

    }
    public void removeFixedCostServicePrice(FixedCost fixedCost, boolean removeInServer) {

        boolean ok = true;

        try {
            getHelper().getFixedCostDao().delete(fixedCost);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(removeInServer){

            if(ok && fixedCost.isUpdated()){
                Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
                task.setFixedcostServicePrice(true);
                task.setDelete(true);
                task.setFixedcost_id(fixedCost.getId());
                saveTask(task);
            }
            startUpdateService();
        }

    }
    public void removeFixedCostsServicePrice(List<FixedCost> fixedCosts, boolean removeInServer){
        for(FixedCost fixedCost: fixedCosts){
            removeFixedCostServicePrice(fixedCost, removeInServer);
        }
    }



    /*
            Service Prices - Labour Cost
     */
    public List<LabourCost> findLabourCostServicePriceNotUpdated() {
        List<LabourCost> list = null;
        try {
            list = getHelper().getLabourcostDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public CreateOrUpdateStatus saveLabourCostServicePriceFromLocal(LabourCost labourCost){
        labourCost.setUpdated(false);
        Log.e("savinglabourCost","savinglabourCost from local " +  labourCost.toString());
        CreateOrUpdateStatus i = null;

        try {
            i = getHelper().getLabourcostDao().createOrUpdate(labourCost);

        } catch (SQLException e) {e.printStackTrace();}



        //  startUpdateService();

        return i;
    }
    public CreateOrUpdateStatus saveLabourCostServicePriceFromLocalUpdatedNow(LabourCost labourCost,
                                                                             boolean updatenow){
        labourCost.setUpdated(false);
        Log.e("savingLabourCost","savingLabourCost from local " +  labourCost.toString());
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getLabourcostDao().createOrUpdate(labourCost);

        } catch (SQLException e) {e.printStackTrace();}


        if(updatenow){
            startUpdateService();
        }


        return i;
    }

    public void saveLabourCostServicePriceFromServer(LabourCost labourCost){
        ServicePrice servicePrice = findServicePriceByServerId(labourCost.getServicepriceid());
        LabourCost labourCostSavedLocally = findLabourCostServicePriceByIdServer(labourCost.getId());

        if(labourCostSavedLocally != null && labourCostSavedLocally.getLocal_id() != 0){
            labourCost.setLocal_id(labourCostSavedLocally.getLocal_id());
        }
        if(servicePrice != null && servicePrice.getLocal_id() != 0){
            labourCost.setServicepricelocalid(servicePrice.getLocal_id());

            try{
                getHelper().getLabourcostDao().createOrUpdate(labourCost);
            }catch (SQLException e){ e.printStackTrace(); }
        }
    }
    public LabourCost findLabourCostServicePriceByIdServer(long server_id){
        List<LabourCost> labourCosts = new ArrayList<>();

        try{
            labourCosts =  getHelper().getLabourcostDao().queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(labourCosts != null && !labourCosts.isEmpty()){
            return labourCosts.get(0);
        }else{
            return null;
        }
    }
    public LabourCost findLabourCostServicePriceByLocalId(long local_id){
        List<LabourCost> labourCosts = new ArrayList<>();

        try{
            labourCosts =  getHelper().getLabourcostDao().queryBuilder().where().eq("local_id", local_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(labourCosts != null && !labourCosts.isEmpty()){
            return labourCosts.get(0);
        }else{
            return null;
        }
    }
    public List<LabourCost> findLabourCostsServicePriceByServicePriceLocalId(long serviceprice_localid){
        List<LabourCost> labourCosts = new ArrayList<>();

        try{
            labourCosts =  getHelper().getLabourcostDao().queryBuilder().where()
                    .eq("servicepricelocalid", serviceprice_localid).query();
        }catch (SQLException e){e.printStackTrace();}

        return labourCosts;

    }
    public void removeLabourCostServicePrice(LabourCost labourCost, boolean removeInServer) {

        boolean ok = true;

        try {
            getHelper().getLabourcostDao().delete(labourCost);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(removeInServer){

            if(ok && labourCost.isUpdated()){
                Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
                task.setDelete(true);
                task.setLabourcostServicePrice(true);
                task.setLabourcost_id(labourCost.getId());
                saveTask(task);
            }
            startUpdateService();
        }

    }
    public void removeLabourCostsServicePrice(List<LabourCost> labourCosts, boolean removeInServer){
        for(LabourCost labourCost: labourCosts){
            removeLabourCostServicePrice(labourCost, removeInServer);
        }
    }


    /*
            Service Prices - Labour Tax
     */
    public List<LabourTax> findLabourTaxServicePriceNotUpdated() {
        List<LabourTax> list = null;
        try {
            list = getHelper().getLabourTaxDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public CreateOrUpdateStatus saveLabourTaxServicePriceFromLocal(LabourTax labourTax){
        labourTax.setUpdated(false);
        Log.e("savinglabourTax","savinglabourTax from local " +  labourTax.toString());
        CreateOrUpdateStatus i = null;

        try {
            i = getHelper().getLabourTaxDao().createOrUpdate(labourTax);

        } catch (SQLException e) {e.printStackTrace();}



        //  startUpdateService();

        return i;
    }
    public CreateOrUpdateStatus saveLabourTaxServicePriceFromLocalUpdatedNow(LabourTax labourTax,
                                                                             boolean updatenow){
        labourTax.setUpdated(false);
        Log.e("savinglabourTax","savinglabourTax from local " +  labourTax.toString());
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getLabourTaxDao().createOrUpdate(labourTax);

        } catch (SQLException e) {e.printStackTrace();}


        if(updatenow){
            startUpdateService();
        }


        return i;
    }

    public void saveLabourTaxServicePriceFromServer(LabourTax labourTax){
        ServicePrice servicePrice = findServicePriceByServerId(labourTax.getServicepriceid());
        LabourTax labourTaxSavedLocally = findLabourTaxServicePriceByIdServer(labourTax.getId());

        if(labourTaxSavedLocally != null && labourTaxSavedLocally.getLocal_id() != 0){
            labourTax.setLocal_id(labourTaxSavedLocally.getLocal_id());
        }
        if(servicePrice != null && servicePrice.getLocal_id() != 0){
            labourTax.setServicepricelocalid(servicePrice.getLocal_id());

            try{
                getHelper().getLabourTaxDao().createOrUpdate(labourTax);
            }catch (SQLException e){ e.printStackTrace(); }
        }
    }
    public LabourTax findLabourTaxServicePriceByIdServer(long server_id){
        List<LabourTax> labourTaxes = new ArrayList<>();

        try{
            labourTaxes =  getHelper().getLabourTaxDao().queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(labourTaxes != null && !labourTaxes.isEmpty()){
            return labourTaxes.get(0);
        }else{
            return null;
        }
    }
    public LabourTax findLabourTaxServicePriceByLocalId(long local_id){
        List<LabourTax> labourTaxes = new ArrayList<>();

        try{
            labourTaxes =  getHelper().getLabourTaxDao().queryBuilder().where().eq("local_id", local_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(labourTaxes != null && !labourTaxes.isEmpty()){
            return labourTaxes.get(0);
        }else{
            return null;
        }
    }
    public List<LabourTax> findLabourTaxsServicePriceByServicePriceLocalId(long serviceprice_localid){
        List<LabourTax> labourTaxes = new ArrayList<>();

        try{
            labourTaxes = getHelper().getLabourTaxDao().queryBuilder().where()
                    .eq("servicepricelocalid", serviceprice_localid).query();
        }catch (SQLException e){e.printStackTrace();}

        return labourTaxes;

    }
    public void removeLabourTaxServicePrice(LabourTax labourTax, boolean removeInServer) {

        boolean ok = true;

        try {
            getHelper().getLabourTaxDao().delete(labourTax);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(removeInServer){

            if(ok && labourTax.isUpdated()){
                Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
                task.setDelete(true);
                task.setLabourtaxServicePrice(true);
                task.setLabourtax_id(labourTax.getId());
                saveTask(task);
            }
            startUpdateService();
        }

    }
    public void removeLabourTaxesServicePrice(List<LabourTax> labourTaxes, boolean removeInServer){
        for(LabourTax labourTax: labourTaxes){
            removeLabourTaxServicePrice(labourTax, removeInServer);
        }
    }


    /*
            Service Prices - Variable Cost
     */
    public List<VariableCost> findVariableCostServicePriceNotUpdated() {
        List<VariableCost> list = null;
        try {
            list = getHelper().getVariableCostDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public CreateOrUpdateStatus saveVariableCostServicePriceFromLocal(VariableCost variableCost){
        variableCost.setUpdated(false);
        Log.e("savingvariableCost","savingvariableCost from local " +  variableCost.toString());
        CreateOrUpdateStatus i = null;

        try {
            i = getHelper().getVariableCostDao().createOrUpdate(variableCost);

        } catch (SQLException e) {e.printStackTrace();}



        //  startUpdateService();

        return i;
    }
    public CreateOrUpdateStatus saveVariableCostServicePriceFromLocalUpdatedNow(VariableCost variableCost,
                                                                             boolean updatenow){
        variableCost.setUpdated(false);
        Log.e("savingvariableCost","savingvariableCost from local " +  variableCost.toString());
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getVariableCostDao().createOrUpdate(variableCost);

        } catch (SQLException e) {e.printStackTrace();}


        if(updatenow){
            startUpdateService();
        }


        return i;
    }

    public void saveVariableCostServicePriceFromServer(VariableCost variableCost){
        ServicePrice servicePrice = findServicePriceByServerId(variableCost.getServicepriceid());
        VariableCost variableCostSavedLocally = findVariableCostServicePriceByIdServer(variableCost.getId());

        if(variableCostSavedLocally != null && variableCostSavedLocally.getLocal_id() != 0){
            variableCost.setLocal_id(variableCostSavedLocally.getLocal_id());
        }
        if(servicePrice != null && servicePrice.getLocal_id() != 0){
            variableCost.setServicepricelocalid(servicePrice.getLocal_id());

            try{
                getHelper().getVariableCostDao().createOrUpdate(variableCost);
            }catch (SQLException e){ e.printStackTrace(); }
        }
    }
    public VariableCost findVariableCostServicePriceByIdServer(long server_id){
        List<VariableCost> variableCosts = new ArrayList<>();

        try{
            variableCosts =  getHelper().getVariableCostDao().queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(variableCosts != null && !variableCosts.isEmpty()){
            return variableCosts.get(0);
        }else{
            return null;
        }
    }
    public VariableCost findVariableCostServicePriceByLocalId(long local_id){
        List<VariableCost> variableCosts = new ArrayList<>();

        try{
            variableCosts =  getHelper().getVariableCostDao().queryBuilder().where().eq("local_id", local_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(variableCosts != null && !variableCosts.isEmpty()){
            return variableCosts.get(0);
        }else{
            return null;
        }
    }
    public List<VariableCost> findVariableCostsServicePriceByServicePriceLocalId(long serviceprice_localid){
        List<VariableCost> variableCosts = new ArrayList<>();

        try{
            variableCosts = getHelper().getVariableCostDao().queryBuilder().where()
                    .eq("servicepricelocalid", serviceprice_localid).query();
        }catch (SQLException e){e.printStackTrace();}

        return variableCosts;

    }
    public void removeVariableCostServicePrice(VariableCost variableCost, boolean removeInServer) {

        boolean ok = true;

        try {
            getHelper().getVariableCostDao().delete(variableCost);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(removeInServer){

            if(ok && variableCost.isUpdated()){
                Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
                task.setDelete(true);
                task.setVariablecostServicePrice(true);
                task.setVariablecost_id(variableCost.getId());
                saveTask(task);
            }
            startUpdateService();
        }

    }
    public void removeVariableCostsServicePrice(List<VariableCost> variableCosts, boolean removeInServer){
        for(VariableCost variableCost: variableCosts){
            removeVariableCostServicePrice(variableCost, removeInServer);
        }
    }

    /*
            PRODUCT PRICE
     */



    public long saveProductpriceFromLocal(ProductPrice productPrice){
        productPrice.setUpdated(false);

        CreateOrUpdateStatus a = null;
        try {
            a = getHelper().getProductPriceDao().createOrUpdate(productPrice);

        } catch (SQLException e) {e.printStackTrace();}

        for(Raw raw : productPrice.getRaws()){
            raw.setProductpricelocalid(productPrice.getLocal_id());
            saveRawProductPriceFromLocal(raw);
        }
        for(FixedVariableCost fixedVariableCost : productPrice.getFixedvariablecosts()){
            fixedVariableCost.setProductpricelocalid(productPrice.getLocal_id());
            saveFixedVariableCostProductPriceFromLocal(fixedVariableCost);
        }
        if(productPrice.isFinished()){
            startUpdateService();
        }
        Log.e("saveProductPrFromLocal", "AFTER: " + productPrice.toString());
        return productPrice.getLocal_id();
    }

    public CreateOrUpdateStatus saveProductpriceFromServer(ProductPrice productPrice){
        CreateOrUpdateStatus a = null;
        ProductPrice productpriceSavedLocally = findProductPriceByServerId(productPrice.getId());
        if(productpriceSavedLocally != null && productpriceSavedLocally.getLocal_id() != 0){
            productPrice.setLocal_id(productpriceSavedLocally.getLocal_id());
            List<Raw>  raws = findRawsProductPriceByProductPriceLocalId(productpriceSavedLocally.getLocal_id());
            List<FixedVariableCost>  fixedVariableCosts =
                    findFixedVariableCostsProductPriceByProductPriceLocalId(productpriceSavedLocally.getLocal_id());
            removeRawsProductPrice(raws, false);
            removeFixedVariableCostsProductPrice(fixedVariableCosts, false);
        }
        try {
            a = getHelper().getProductPriceDao().createOrUpdate(productPrice);
            Log.e("SaveProductFromServer",
                    "My id = " + productPrice.getId() + " | My local_id = " + productPrice.getLocal_id() + "| " + productPrice.toString());
            List<Raw>  raws = findRawsProductPriceByProductPriceLocalId(productPrice.getLocal_id());
            List<FixedVariableCost>  fixedVariableCosts =
                    findFixedVariableCostsProductPriceByProductPriceLocalId(productPrice.getLocal_id());
            removeRawsProductPrice(raws, false);
            removeFixedVariableCostsProductPrice(fixedVariableCosts, false);

        } catch (SQLException e) {e.printStackTrace();}

        for(Raw raw : productPrice.getRaws()){
            raw.setProductpricelocalid(productPrice.getLocal_id());
            saveRawProductPriceFromServer(raw);
        }
        for(FixedVariableCost fixedVariableCost : productPrice.getFixedvariablecosts()){
            fixedVariableCost.setProductpricelocalid(productPrice.getLocal_id());
            saveFixedVariableCostProductPriceFromServer(fixedVariableCost);
        }
        Log.e("DATABASE", "FINISHED saveProductpriceFromServer");

        return a;
    }
    public ProductPrice findProductPriceByLocalid(long local_id, boolean updateNow) {
        List<ProductPrice> list = null;
        try {
            list = getHelper().getProductPriceDao().queryBuilder()
                    .where().eq("local_id", local_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            List<Raw> raws = new ArrayList<>();
            List<FixedVariableCost> fixedVariableCosts = new ArrayList<>();
            try{
                raws = getHelper().getRawDao().queryBuilder().where().eq("productpricelocalid", local_id).query();
                fixedVariableCosts = getHelper().getFixedVariableCostDao().queryBuilder().where().
                        eq("productpricelocalid", local_id).query();

            }catch (SQLException ee){
                ee.printStackTrace();
            }
            list.get(0).setRaws(raws);
            list.get(0).setFixedvariablecosts(fixedVariableCosts);
            if(updateNow) {
                list.get(0).updateValues(context);
            }
            return list.get(0);
        }

    }

    public List<ProductPrice> findProductPriceNotFinished(){
        List<ProductPrice> list = null;
        try {
            list = getHelper().getProductPriceDao().queryBuilder()
                    .where().eq("finished", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{
            for(int i = 0; i < list.size(); i++) {
                List<Raw> raws = new ArrayList<>();
                List<FixedVariableCost> fixedVariableCosts = new ArrayList<>();

                try{
                    raws = getHelper().getRawDao().queryBuilder().where()
                            .eq("productpricelocalid", list.get(i).getLocal_id()).query();
                    fixedVariableCosts = getHelper().getFixedVariableCostDao().queryBuilder().where()
                            .eq("productpricelocalid", list.get(i).getLocal_id()).query();

                }catch (SQLException ee){
                    ee.printStackTrace();
                }
                list.get(i).setRaws(raws);
                list.get(i).setFixedvariablecosts(fixedVariableCosts);
                list.get(i).updateValues(context);
            }
            return list;
        }
    }
    public ProductPrice findProductPriceByServerId(long server_id) {
        List<ProductPrice> list = null;
        try {
            list = getHelper().getProductPriceDao().queryBuilder()
                    .where().eq("id", server_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return null;
        }else{
            List<Raw> raws = new ArrayList<>();
            List<FixedVariableCost> fixedVariableCosts = new ArrayList<>();

            try{
                raws = getHelper().getRawDao().queryBuilder().where()
                        .eq("productpricelocalid", list.get(0).getLocal_id()).query();
                fixedVariableCosts = getHelper().getFixedVariableCostDao().queryBuilder().where()
                        .eq("productpricelocalid", list.get(0).getLocal_id()).query();

            }catch (SQLException ee){
                ee.printStackTrace();
            }
            list.get(0).setRaws(raws);
            list.get(0).setFixedvariablecosts(fixedVariableCosts);
        //    list.get(0).updateValues(context);
            return list.get(0);
        }

    }

    public List<ProductPrice> findProductPricesNotUpdated() {
        List<ProductPrice> list = null;
        try {
            list = getHelper().getProductPriceDao().queryBuilder()
                    .where().eq("updated", false).
                            and().eq("finished", true).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{
            for(int i = 0; i < list.size(); i++) {
                List<Raw> raws = new ArrayList<>();
                List<FixedVariableCost> fixedVariableCosts = new ArrayList<>();

                try{
                    raws = getHelper().getRawDao().queryBuilder().where()
                            .eq("productpricelocalid", list.get(i).getLocal_id()).query();
                    fixedVariableCosts = getHelper().getFixedVariableCostDao().queryBuilder().where()
                            .eq("productpricelocalid", list.get(i).getLocal_id()).query();

                }catch (SQLException ee){
                    ee.printStackTrace();
                }
                list.get(i).setRaws(raws);
                list.get(i).setFixedvariablecosts(fixedVariableCosts);
                list.get(i).updateValues(context);
            }
            Log.e("DATABASE", "findProductPricesNotUpdated -> " + list.toString());
            return list;
        }

    }


    public List<ProductPrice> findAllProductPrices(){
        List<ProductPrice> productPrices = new ArrayList<>();
        try{
            productPrices = getHelper().getProductPriceDao().queryBuilder()
                    .orderBy("local_id", false).query();
        }catch (Exception e){e.printStackTrace();}

        return productPrices;
    }
    public List<ProductPrice> findAllProductPricesFinished(){
        List<ProductPrice> productPrices = new ArrayList<>();
        try{
            productPrices = getHelper().getProductPriceDao().queryBuilder()
                    .where().eq("finished", true).query();
        }catch (Exception e){e.printStackTrace();}

        return productPrices;
    }
    public void removeProductPrice(ProductPrice productPrice){
        boolean ok = true;

        try {
            getHelper().getProductPriceDao().delete(productPrice);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(ok && productPrice.isUpdated()){
            Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
            task.setProductprice_id(productPrice.getId());
            task.setProductprice(true);
            task.setDelete(true);
            saveTask(task);
        }
        for(Raw raw : productPrice.getRaws()){
            Log.e("DataBase", "going to RemoveRaw... " + raw.getLocal_id());
            removeRawProductPrice(raw, false);
        }
        for(FixedVariableCost fixedVariableCost : productPrice.getFixedvariablecosts()){
            removeFixedVariableCostProductPrice(fixedVariableCost, false);
        }
        startUpdateService();
    }


    public void removeProductPrices() {
        List<ProductPrice> p = findAllProductPrices();

        if (p != null) {
            try {
                getHelper().getProductPriceDao().delete(p);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //  startUpdateService();
    }

/*
        PRODUCT PRICE  -  RAWS
 */
    public List<Raw> findRawProductPriceNotUpdated() {
        List<Raw> list = null;
        try {
            list = getHelper().getRawDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public CreateOrUpdateStatus saveRawProductPriceFromLocal(Raw raw){
        raw.setUpdated(false);
        Log.e("savingRaw","savingRaw from local " +  raw.toString());
        CreateOrUpdateStatus i = null;

        try {
            i = getHelper().getRawDao().createOrUpdate(raw);

        } catch (SQLException e) {e.printStackTrace();}



        //  startUpdateService();

        return i;
    }
    public CreateOrUpdateStatus saveRawProductPriceFromLocalUpdatedNow(Raw raw,
                                                                                boolean updatenow){
        raw.setUpdated(false);
        Log.e("savingRaw","saving Raw from local " +  raw.toString());
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getRawDao().createOrUpdate(raw);

        } catch (SQLException e) {e.printStackTrace();}


        if(updatenow){
            startUpdateService();
        }


        return i;
    }

    public void saveRawProductPriceFromServer(Raw raw){
        ProductPrice productPrice = findProductPriceByServerId(raw.getProductpriceid());
        Raw rawSavedLocally = findRawProductPriceByIdServer(raw.getId());

        if(rawSavedLocally != null && rawSavedLocally.getLocal_id() != 0){
            raw.setLocal_id(rawSavedLocally.getLocal_id());
        }
        if(productPrice != null && productPrice.getLocal_id() != 0){
            raw.setProductpricelocalid(productPrice.getLocal_id());

            try{
                getHelper().getRawDao().createOrUpdate(raw);
            }catch (SQLException e){ e.printStackTrace(); }
            Log.e("savingRaw","saving Raw from SERVER " +  raw.toString());
        }
    }
    public Raw findRawProductPriceByIdServer(long server_id){
        List<Raw> raws = new ArrayList<>();

        try{
            raws =  getHelper().getRawDao().queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(raws != null && !raws.isEmpty()){
            return raws.get(0);
        }else{
            return null;
        }
    }
    public Raw findRawProductPriceByIdLocal(long local_id){
        List<Raw> raws = new ArrayList<>();

        try{
            raws =  getHelper().getRawDao().queryBuilder().where().eq("local_id", local_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(raws != null && !raws.isEmpty()){
            return raws.get(0);
        }else{
            return null;
        }
    }
    public List<Raw> findRawsProductPriceByProductPriceLocalId(long productprice_localid){
        List<Raw> raws = new ArrayList<>();

        try{
            raws = getHelper().getRawDao().queryBuilder().where()
                    .eq("productpricelocalid", productprice_localid).query();
        }catch (SQLException e){e.printStackTrace();}

        return raws;

    }
    public void removeRawProductPrice(Raw raw, boolean removeInServer) {

        boolean ok = true;

        try {
            getHelper().getRawDao().delete(raw);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(removeInServer){

            if(ok && raw.isUpdated()){
                Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
                task.setDelete(true);
                task.setRawProductPrice(true);
                task.setRawproductprice_id(raw.getId());
                saveTask(task);
            }
            startUpdateService();
        }

    }
    public void removeRawsProductPrice(List<Raw> raws, boolean removeInServer){
        for(Raw raw: raws){
            removeRawProductPrice(raw, removeInServer);
        }
    }

/*
        PRODUCT PRICE  -  FIXEDVARIABLECOSTS
 */
    public List<FixedVariableCost> findFixedVariableCostProductPriceNotUpdated() {
        List<FixedVariableCost> list = null;
        try {
            list = getHelper().getFixedVariableCostDao().queryBuilder()
                    .where().eq("updated", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }else{

            return list;
        }
    }
    public CreateOrUpdateStatus saveFixedVariableCostProductPriceFromLocal(FixedVariableCost fixedVariableCost){
        fixedVariableCost.setUpdated(false);
        Log.e("savingfixedVariableCost","saving fixedVariableCost from local " +  fixedVariableCost.toString());
        CreateOrUpdateStatus i = null;

        try {
            i = getHelper().getFixedVariableCostDao().createOrUpdate(fixedVariableCost);

        } catch (SQLException e) {e.printStackTrace();}



        //  startUpdateService();

        return i;
    }
    public CreateOrUpdateStatus saveFixedVariableCostProductPriceFromLocalUpdatedNow(FixedVariableCost fixedVariableCost,
                                                                       boolean updatenow){
        fixedVariableCost.setUpdated(false);
        Log.e("saving fxdVarCost","saving fixedVariableCost from local " +  fixedVariableCost.toString());
        CreateOrUpdateStatus i = null;
        try {
            i = getHelper().getFixedVariableCostDao().createOrUpdate(fixedVariableCost);

        } catch (SQLException e) {e.printStackTrace();}


        if(updatenow){
            startUpdateService();
        }


        return i;
    }

    public void saveFixedVariableCostProductPriceFromServer(FixedVariableCost fixedVariableCost){
        ProductPrice productPrice = findProductPriceByServerId(fixedVariableCost.getProductpriceid());
        FixedVariableCost fixedVariableCostSavedLocally =
                findFixedVariableCostProductPriceByIdServer(fixedVariableCost.getId());

        if(fixedVariableCostSavedLocally != null && fixedVariableCostSavedLocally.getLocal_id() != 0){
            fixedVariableCost.setLocal_id(fixedVariableCostSavedLocally.getLocal_id());
        }
        if(productPrice != null && productPrice.getLocal_id() != 0){
            fixedVariableCost.setProductpricelocalid(productPrice.getLocal_id());

            try{
                getHelper().getFixedVariableCostDao().createOrUpdate(fixedVariableCost);
            }catch (SQLException e){ e.printStackTrace(); }
            Log.e("savingRaw","saving fixedVariableCost from SERVER " +  fixedVariableCost.toString());
        }
    }
    public FixedVariableCost findFixedVariableCostProductPriceByIdServer(long server_id){
        List<FixedVariableCost> fixedVariableCosts = new ArrayList<>();

        try{
            fixedVariableCosts =  getHelper().getFixedVariableCostDao().
                    queryBuilder().where().eq("id", server_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(fixedVariableCosts != null && !fixedVariableCosts.isEmpty()){
            return fixedVariableCosts.get(0);
        }else{
            return null;
        }
    }
    public FixedVariableCost findFixedVariableCostProductPriceByLocalId(long local_id){
        List<FixedVariableCost> fixedVariableCosts = new ArrayList<>();

        try{
            fixedVariableCosts =  getHelper().getFixedVariableCostDao().
                    queryBuilder().where().eq("local_id", local_id).query();
        }catch (SQLException e){e.printStackTrace();}

        if(fixedVariableCosts != null && !fixedVariableCosts.isEmpty()){
            return fixedVariableCosts.get(0);
        }else{
            return null;
        }
    }
    public List<FixedVariableCost> findFixedVariableCostsProductPriceByProductPriceLocalId(long productprice_localid){
        List<FixedVariableCost> fixedVariableCosts = new ArrayList<>();

        try{
            fixedVariableCosts = getHelper().getFixedVariableCostDao().queryBuilder().where()
                    .eq("productpricelocalid", productprice_localid).query();
        }catch (SQLException e){e.printStackTrace();}

        return fixedVariableCosts;

    }
    public void removeFixedVariableCostProductPrice(FixedVariableCost fixedVariableCost, boolean removeInServer) {

        boolean ok = true;

        try {
            getHelper().getFixedVariableCostDao().delete(fixedVariableCost);

        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        if(removeInServer){

            if(ok && fixedVariableCost.isUpdated()){
                Task task = new Task(findUserDetails() != null ? findUserDetails().getUserid() : 0);
                task.setDelete(true);
                task.setFixedVariableCostProductPrice(true);
                task.setFixedvariablecostproductprice_id(fixedVariableCost.getId());
                saveTask(task);
            }
            startUpdateService();
        }

    }
    public void removeFixedVariableCostsProductPrice(List<FixedVariableCost> fixedVariableCosts, boolean removeInServer){
        for(FixedVariableCost fixedVariableCost: fixedVariableCosts){
            removeFixedVariableCostProductPrice(fixedVariableCost, removeInServer);
        }
    }



    void startUpdateService(){
        if(context instanceof Activity){
            if(context instanceof FinancialControlMainActivity) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ((FinancialControlMainActivity) context).scheduleNetworkCheckingJob();
                }else{
                    ((FinancialControlMainActivity) context).scheduleNetworkCheckingJobOld();
                }
            }
        }


    }
}

