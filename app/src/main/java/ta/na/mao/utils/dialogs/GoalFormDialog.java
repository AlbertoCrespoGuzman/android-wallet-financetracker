package ta.na.mao.utils.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.databinding.DataBindingUtil;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Goal;
import ta.na.mao.databinding.DialogGoalFormBinding;
import ta.na.mao.utils.ErrorMessage;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class GoalFormDialog  extends Dialog {

    int month;
    int year;
    Goal goal;
    TextView titleText;
    Button saveButton, cancelButton;
    List<ErrorMessage> errors = new ArrayList<>();
    DatabaseManager db;
    Activity activity;
    long goal_id_local;
    List<Goal> existingGoals;
    Spinner categoriesSpinner;
    EditText valueInput;
    Spinner categorySpinner;
    boolean focusedFirstTime = false;

    public GoalFormDialog (final Activity activity, int month, int year, DialogClickListener dialogClickListener, long goal_id_local, List<Goal> existingGoals) {
        super(activity);
        this.month = month;
        this.year =year;
        this.activity = activity;
        this.goal_id_local = goal_id_local;
        this.existingGoals = existingGoals;







        DialogGoalFormBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout. dialog_goal_form, null, false);

        setContentView(binding.getRoot());


        valueInput = findViewById(R.id.dialog_goal_form_value);

        if(valueInput != null){
            valueInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        if(focusedFirstTime){
                            removeEditTextValueIfZero(valueInput);
                        }else{
                            focusedFirstTime = true;
                        }
                    }
                }
            });
        }


        db =  new DatabaseManager(activity);
        if(goal_id_local == 0){
            goal = new Goal(this.activity);
        }else{
            goal = db.findGoalByLocalid(goal_id_local);
            goal.reverseMainCategory(this.activity);
            Log.e("DialogGoal", "DialogGoal goal found in db = " + goal.toString());
            if(goal == null){
                goal = new Goal(this.activity);
            }
        }

        categoriesSpinner = findViewById(R.id.dialog_goal_form_category_spinner);
   /*     if(categoriesSpinner != null){
            if(goal.isIncome()){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(), android.R.layout.simple_spinner_item,
                        Arrays.asList(getContext().getResources().getStringArray(R.array.goal_income_categories_array)));

                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_textcolor_black);
                categoriesSpinner.setAdapter(adapter);
            }else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(), android.R.layout.simple_spinner_item,
                        Arrays.asList(getContext().getResources().getStringArray(R.array.goal_outgo_categories_array)));

                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_textcolor_black);
                categoriesSpinner.setAdapter(adapter);
                //    categorySpinner.setSelection(0);
            }
        }
        if(categoriesSpinner.getChildCount() > 0 && categoriesSpinner.getChildAt(0) instanceof  TextView){
            ((TextView) categoriesSpinner.getChildAt(0)).setTextColor(getContext().getResources().getColor(R.color.black));
        }
        categoriesSpinner.setBackground(getContext().getResources().getDrawable(R.drawable.spinner_selected_item_textcolor_black));
*/
       // categoriesSpinner.setBackground(getContext().getResources().getDrawable(R.drawable.spinner_selected_item_textcolor_black));


        goal.setCategorySpinner(categoriesSpinner);
        goal.setContext(activity);


        binding.setGoal(goal);

        if(categoriesSpinner != null){
            if(goal.isIncome()){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(), R.layout.simple_spinner_dropdown_item_textcolor_black,
                        Arrays.asList(getContext().getResources().getStringArray(R.array.goal_income_categories_array_main_first)));

                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_textcolor_black);
                categoriesSpinner.setAdapter(adapter);
            }else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(), R.layout.simple_spinner_dropdown_item_textcolor_black,
                        Arrays.asList(getContext().getResources().getStringArray(R.array.goal_outgo_categories_array_main_first)));

                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_textcolor_black);
                categoriesSpinner.setAdapter(adapter);
                //    categorySpinner.setSelection(0);
            }
        }
        if(categoriesSpinner.getChildCount() > 0 && categoriesSpinner.getChildAt(0) instanceof  TextView){
            ((TextView) categoriesSpinner.getChildAt(0)).setTextColor(getContext().getResources().getColor(R.color.black));
        }
        if(categoriesSpinner.getChildCount() > 0){
            Log.e("categoriesSpinner","categoriesSpinner @@");
            ((TextView) categoriesSpinner.getChildAt(0)).setTextColor(getContext().getResources().getColor(R.color.black));
        }

        titleText = findViewById(R.id.dialog_goal_form_title);
        titleText.setText(getDateText(month, year));

        saveButton = findViewById(R.id.dialog_goal_form_save_button);
        cancelButton = findViewById(R.id.dialog_goal_form_cancel_button);

        final int monthfinal = month;
        final int yearfinal = year;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                errors = goal.validator(activity, errors, new View(activity));
                if(errors.isEmpty()){
                    if(noMatchWithExistingGoals()) {
                        db = new DatabaseManager(activity);
                        Calendar c = Calendar.getInstance();
                        c.set(yearfinal, monthfinal, 1);
                        goal.setFirstdate(c.getTime());
                        c.add(Calendar.MONTH, 1);
                        c.add(Calendar.DAY_OF_MONTH, -1);
                        goal.setLastdate(c.getTime());
                        c.add(Calendar.DAY_OF_MONTH, -5);
                        goal.setNotificationdate(c.getTime());
                        db.saveGoalFromLocal(goal, true);
                        Toast.makeText(activity, activity.getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
                        ((FinancialControlMainActivity) activity).changeToFragmentGoalsMenu(monthfinal + 1, yearfinal);
                        dismiss();
                    }else{
                        Toast.makeText(activity, activity.getResources().getString(R.string.matching_goals_category), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    showErrors();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
    boolean noMatchWithExistingGoals(){
        boolean ok = true;

        if(this.goal.getLocal_id() == 0) {
            for (Goal goal : existingGoals) {
                if (goal.isIncome() == this.goal.isIncome() && goal.getCategory() == this.goal.getCategory()) {
                    ok = false;
                    break;
                }
            }
        }
        return ok;
    }
    void saveGoal(){

        Calendar c = Calendar.getInstance();
        month = month == 1 ? 12 : month - 1;
        year = month == 12 ? year - 1  : year;

        Log.e("month","" + month);
        Log.e("year","" + year);

        c.set(year, month,  1);

        Calendar firstDayDate = Calendar.getInstance();
        firstDayDate.setTime(c.getTime());
        firstDayDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDayDate.set(Calendar.HOUR_OF_DAY, 0);
        firstDayDate.set(Calendar.MINUTE, 0);
        firstDayDate.set(Calendar.SECOND, 0);


        //last day of month
        Calendar lastDayDate = Calendar.getInstance();
        lastDayDate.setTime(firstDayDate.getTime());
        lastDayDate.add(Calendar.MONTH, 1);
        lastDayDate.add(Calendar.DAY_OF_MONTH, -1);
        lastDayDate.set(Calendar.HOUR_OF_DAY, 0);
        lastDayDate.set(Calendar.MINUTE, 0);
        lastDayDate.set(Calendar.SECOND, 0);

        goal.setFirstdate(firstDayDate.getTime());
        goal.setLastdate(lastDayDate.getTime());


        // notificar 7 dias antes
        lastDayDate.add(Calendar.DAY_OF_MONTH, -7);
        goal.setNotificationdate(lastDayDate.getTime());



    }
    public void showErrors(){
        Toast.makeText(activity, errors.get(0).getMessage(), Toast.LENGTH_SHORT).show();
    }
    private String getDateText(int month, int year){
        String text = "";

        Locale localeBR = new Locale("pt", "BR");
        SimpleDateFormat fmt = new SimpleDateFormat("' ' MMMM", localeBR);
        Calendar c = Calendar.getInstance();
        c.set(year, month,  1);

        text += fmt.format(c.getTime()) + " " + year;
        text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return text;
    }

    public interface DialogClickListener {
        void onButtonGoalFormDialogClick(boolean accepted);
    }


}