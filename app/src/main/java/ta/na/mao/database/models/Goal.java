package ta.na.mao.database.models;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import androidx.databinding.BaseObservable;
import androidx.databinding.InverseMethod;
import ta.na.mao.BR;
import ta.na.mao.R;
import ta.na.mao.utils.ErrorMessage;

@DatabaseTable
public class Goal   extends BaseObservable {

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;
    @DatabaseField
    long id;
    @DatabaseField
    long userid;
    @DatabaseField
    boolean updated;

    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date firstdate;
    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date lastdate;
    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date notificationdate;

    @DatabaseField
    boolean income;
    @DatabaseField
    double value;

    @DatabaseField
    int category;

    @DatabaseField
    boolean notificated;
    @DatabaseField
    boolean previewNotificated;

    transient Context context;
    transient Spinner categorySpinner;

    public Goal(){

    }
    public Goal(Context context){
        this.context = context;
    }
    public Goal(Context context, Spinner categorySpinner){
        this.context = context;
        this.categorySpinner = categorySpinner;
    }
    public void reverseMainCategory(Context context){

        if(category == 0){
            category = 0;
        }
        if(income){
            if(category == Arrays.asList(context.getResources().getStringArray(R.array.goal_income_categories_array_main_first)).size() - 1){
                category = 1;
            }else{
                category = category + 1;
            }
        }else{
            if(category == Arrays.asList(context.getResources().getStringArray(R.array.goal_outgo_categories_array_main_first)).size() - 1){
                category = 1;
            }else{
                category = category + 1;
            }
        }
    }
    public boolean isNotificated() {
        return notificated;
    }

    public void setNotificated(boolean notificated) {
        this.notificated = notificated;
    }

    public boolean isPreviewNotificated() {
        return previewNotificated;
    }

    public void setPreviewNotificated(boolean previewNotificated) {
        this.previewNotificated = previewNotificated;
    }

    public long getLocal_id() {
        return local_id;
    }

    public void setLocal_id(long local_id) {
        this.local_id = local_id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        if(category == 1){
            if(income){
                category = Arrays.asList(context.getResources().getStringArray(R.array.goal_income_categories_array_main_first)).size() - 1;
            }else{
                category = Arrays.asList(context.getResources().getStringArray(R.array.goal_outgo_categories_array_main_first)).size() - 1;
            }
        }else{
            if(category != 0){
                category = category - 1;
            }else{
                category = 0;
            }

        }
        this.category = category;
        Log.e("Goal class", "setCategory " + this.category);
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

    public Date getFirstdate() {
        return firstdate;
    }

    public void setFirstdate(Date firstdate) {
        this.firstdate = firstdate;
    }

    public Date getLastdate() {
        return lastdate;
    }

    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }

    public Date getNotificationdate() {
        return notificationdate;
    }

    public void setNotificationdate(Date notificationdate) {
        this.notificationdate = notificationdate;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
        notifyPropertyChanged(BR.goal);

    }
    public void setIncome(View view, boolean income){
        this.income = income;
        if(this.context != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        notifyPropertyChanged(BR.goal);
        Log.e("Goal class", "setIncome " + this.income);
        if(categorySpinner != null){
            if(this.income){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        context, R.layout.simple_spinner_dropdown_item_textcolor_black,
                        Arrays.asList(context.getResources().getStringArray(R.array.goal_income_categories_array_main_first)));

                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_textcolor_black);
                categorySpinner.setAdapter(adapter);
            }else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        context,  R.layout.simple_spinner_dropdown_item_textcolor_black,
                        Arrays.asList(context.getResources().getStringArray(R.array.goal_outgo_categories_array_main_first)));

                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_textcolor_black);
                categorySpinner.setAdapter(adapter);
            //    categorySpinner.setSelection(0);
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Spinner getCategorySpinner() {
        return categorySpinner;
    }

    public void setCategorySpinner(Spinner categorySpinner) {
        this.categorySpinner = categorySpinner;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "local_id=" + local_id +
                ", id=" + id +
                ", userid=" + userid +
                ", updated=" + updated +
                ", firstdate=" + firstdate +
                ", lastdate=" + lastdate +
                ", notificationdate=" + notificationdate +
                ", income=" + income +
                ", value=" + value +
                ", category=" + category +
                ", notificated=" + notificated +
                ", previewNotificated=" + previewNotificated +
                ", context=" + context +
                ", categorySpinner=" + categorySpinner +
                '}';
    }

    public String getCategoryText(Context context){
        String categoryText = "";

        if(income){
            categoryText = context.getResources().getStringArray(R.array.goal_income_categories_array)[this.category];
            if(categoryText.contains("(")){
                categoryText = categoryText.split(Pattern.quote("("))[0];
            }
        }else{
            categoryText = context.getResources().getStringArray(R.array.goal_outgo_categories_array)[this.category];
            if(categoryText.contains("(")){
                categoryText = categoryText.split(Pattern.quote("("))[0];
            }
        }
        return categoryText;
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

    public List<ErrorMessage> validator(Context context, List<ErrorMessage> errors, View errorView){
        Log.e("validator", "category " + this.category);
        Log.e("validator", "this.value " + this.value);
        errors = new ArrayList<>();

        if(this.category == 0 ) {
            errors.add(new ErrorMessage(context.getResources().getString(R.string.category_error), errorView));
        }else if(this.value == 0.0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.payment_error), errorView));
        }

        return errors;
    }

}
