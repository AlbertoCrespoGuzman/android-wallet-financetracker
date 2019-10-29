package ta.na.mao.database.models;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.InverseMethod;
import ta.na.mao.BR;
import ta.na.mao.R;
import ta.na.mao.utils.ErrorMessage;

@DatabaseTable
public class Transaction  extends BaseObservable {

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;
    @DatabaseField
    long id;
    @DatabaseField
    long userid;
    @DatabaseField
    boolean updated;

    @DatabaseField
    int category;
    @DatabaseField
    boolean income;
    //EEE, dd MMM yyyy HH:mm:ss zzz
    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date date;
    @DatabaseField
    boolean installment;
    @DatabaseField
    int totalinstallments;
    @DatabaseField
    int frequencyinstallment;
    @DatabaseField
    double value;
    @DatabaseField
    double entrance_payment;
    @DatabaseField
    double payment;
    @DatabaseField
    boolean paid;
    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date firstinstallment;

    List<Installment> installments = new ArrayList<>();

    transient Context context;

    public Transaction(){

        date = new Date();
    }
    public Transaction(Context context){
        this.context = context;
        date = new Date();
    }

    public List<ErrorMessage> validator(Context context, List<ErrorMessage> errors, View errorView){
        if(category == 0 ){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.category_error), errorView));
        }else if(fromDatetoString(date).isEmpty() || fromDatetoString(date).equals("")){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.date_error), errorView));
        }else if(value == 0.0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.payment_error), errorView));
        }else if(installment){
            if(value < entrance_payment){
                errors.add(new ErrorMessage(context.getResources().getString(R.string.diff_value_payment_error), errorView));
            }else if(totalinstallments == 0){
                errors.add(new ErrorMessage(context.getResources().getString(R.string.frequency_error), errorView));
            }else if(frequencyinstallment == 0){
                errors.add(new ErrorMessage(context.getResources().getString(R.string.total_installments_error), errorView));
            }else if(fromDatetoString(firstinstallment).isEmpty() || fromDatetoString(firstinstallment).equals("")){
                errors.add(new ErrorMessage(context.getResources().getString(R.string.firstinstallment_error), errorView));
            }else if(date.getTime() > firstinstallment.getTime()){
                errors.add(new ErrorMessage(context.getResources().getString(R.string.date_firstinstallment_error), errorView));
            }
        }
        if(errors.isEmpty()){
            if(!installment){
                payment = value;
                paid = true;
            }else{
                payment = entrance_payment;
                paid = false;
            }
        }
        return errors;
    }

    public double getEntrance_payment() {
        return entrance_payment;
    }

    public void setEntrance_payment(double entrance_payment) {
        this.entrance_payment = entrance_payment;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Date getFirstinstallment() {
        return firstinstallment;
    }

    public void setFirstinstallment(Date firstinstallment) {
        this.firstinstallment = firstinstallment;
    }

    public int getTotalinstallments() {
        return totalinstallments;
    }

    public int getFrequencyinstallment() {
        return frequencyinstallment;
    }

    public void setFrequencyinstallment(int frequencyinstallment) {
        if(frequencyinstallment == 1){
            this.frequencyinstallment = 7;
        }else if(frequencyinstallment == 2){
            this.frequencyinstallment = 15;
        }else if(frequencyinstallment == 3){
            this.frequencyinstallment = 30;
        }
      //  this.frequencyinstallment = frequencyinstallment;
    }
    public void setFrequencyInstallmentForcing(int frequencyinstallment){
        this.frequencyinstallment = frequencyinstallment;
    }
    public void setTotalinstallments(int totalinstallment) {
        this.totalinstallments = totalinstallment;
    }

    public long getLocal_id() {
        return local_id;
    }

    public void setLocal_id(long local_id) {
        this.local_id = local_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }
    @Bindable
    public boolean isInstallment() {
        return installment;
    }

    public void setInstallment(boolean installment) {
        this.installment = installment;
        notifyPropertyChanged(BR.installment);

    }
    public void setInstallment(View view, boolean installment){
        this.installment = installment;
        if(this.context != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        notifyPropertyChanged(BR.installment);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public void setInstallments(List<Installment> installments) {
        this.installments = installments;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "local_id=" + local_id +
                ", id=" + id +
                ", userid=" + userid +
                ", updated=" + updated +
                ", category=" + category +
                ", income=" + income +
                ", date=" + date +
                ", installment=" + installment +
                ", totalinstallments=" + totalinstallments +
                ", frequencyinstallment=" + frequencyinstallment +
                ", value=" + value +
                ", entrance_payment=" + entrance_payment +
                ", payment=" + payment +
                ", paid=" + paid +
                ", firstinstallment=" + firstinstallment +
                ", installments=" + installments +
                '}';
    }

    @InverseMethod("fromInttoString")
    public double fromStringtoInt(String value) {
        if(value.isEmpty()){
            return 0;
        }else {
            return Integer.parseInt(value);
        }
    }

    public String fromInttoString(double value) {
        return String.valueOf(value);
    }
    @InverseMethod("fromDoubletoString")
    public double fromStringtoDouble(String value) {
        if(value.isEmpty()){
            return 0.0;
        }else {
            return Double.parseDouble(value);
        }
    }

    public String fromDoubletoString(double value) {
        return String.valueOf(value);
    }
    @InverseMethod("fromDatetoString")
    public Date fromStringtoDate(String value) {
        Date date = null;
        try {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            date = sdf.parse(value);
        }catch (Exception e){e.printStackTrace();}

        return date;
    }

    public String fromDatetoString(Date value) {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if(value == null){
            return "";
        }else {
            return sdf.format(value);
        }
    }
    public String getCategoryText(Context context){
        String categoryText = "";
        if(income){
            categoryText = context.getResources().getStringArray(R.array.statement_income_categories_array)[this.category];
            if(categoryText.contains("(")){
                categoryText = categoryText.split(Pattern.quote("("))[0];
            }
        }else{
            categoryText = context.getResources().getStringArray(R.array.statement_outgo_categories_array)[this.category];
            if(categoryText.contains("(")){
                categoryText = categoryText.split(Pattern.quote("("))[0];
            }
        }


        return categoryText;

    }
}
