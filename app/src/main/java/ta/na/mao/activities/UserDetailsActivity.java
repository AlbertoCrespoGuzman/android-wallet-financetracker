package ta.na.mao.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import ta.na.mao.R;
import ta.na.mao.communications.Svc;
import ta.na.mao.communications.SvcApi;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Mobile;
import ta.na.mao.database.models.UserDetails;
import ta.na.mao.databinding.ActivityUserDetailsBinding;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.ErrorMessage;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.adapters.ArrayAdapterCustomSpinner;

public class UserDetailsActivity extends AppCompatActivity implements Serializable{

    SvcApi svcAuth;
    DatabaseManager db;
    TextView errorText;
    TextInputLayout otherscategoryLayout;
    EditText name, cpf, birthday, mobileState, mobile1, mobile2, email, familynumber, othersubcategory;
    Spinner education, skincolor,familyincome,maincategory, subcategory;
    Button submit, back;
    List<ErrorMessage> errors = new ArrayList<>();
    ProgressDialog dialog;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    UserDetails userDetails = new UserDetails();
    Mobile mobile = new Mobile();
    ArrayList<Integer> subcategories = new ArrayList<>();
    RadioGroup activated, formalized;
    boolean backEnabled = false;
    int initialSubCategory = 0;
    boolean initialSubcategoryDone = true;
    int initialSubcategoryError = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //    getSupportActionBar().hide();
        ActivityUserDetailsBinding binding;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details_);
        }else{
            binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details_old_);
        }
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            backEnabled = false;
        } else {
            backEnabled = extras.getBoolean(Defaultdata.BACK_BUTTON_ENABLED, false);
        }
        subcategories.add(R.array.subcategory_0_array);
        subcategories.add(R.array.subcategory_1_array);
        subcategories.add(R.array.subcategory_2_array);
        subcategories.add(R.array.subcategory_3_array);
        subcategories.add(R.array.subcategory_4_array);

        db = new DatabaseManager(getApplicationContext());

        cpf = findViewById(R.id.activity_user_details_cpf);

        cpf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId ==  EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_GO){
                    Utils.hideKeyboard(UserDetailsActivity.this);
                }
                return false;
            }
        });

        email = findViewById(R.id.activity_user_details_email);
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId ==  EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_GO){
                    Utils.hideKeyboard(UserDetailsActivity.this);
                }
                return false;
            }
        });
        mobile2 = findViewById(R.id.activity_user_details_mobile_number);
        mobile2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId ==  EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_GO){
                    Utils.hideKeyboard(UserDetailsActivity.this);
                }
                return false;
            }
        });
        familynumber = findViewById(R.id.activity_user_details_familynumber);
        familynumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId ==  EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_GO){
                    Utils.hideKeyboard(UserDetailsActivity.this);
                }
                return false;
            }
        });
        activated = findViewById(R.id.activity_user_details_activated);
        activated.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Utils.hideKeyboard(UserDetailsActivity.this);
            }
        });
        formalized = findViewById(R.id.activity_user_details_formalized);
        formalized.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Utils.hideKeyboard(UserDetailsActivity.this);
            }
        });
        if(backEnabled){
            back = findViewById(R.id.activity_user_details_button_back);
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserDetailsActivity.this,
                            MainActivity.class));
                    finish();
                }
            });
        }
        birthday = findViewById(R.id.activity_user_details_birthday);
        maincategory = findViewById(R.id.activity_user_details_maincategory);
        subcategory = findViewById(R.id.activity_user_details_subcategory);
        othersubcategory = findViewById(R.id.activity_user_details_subcategory_other);
        otherscategoryLayout = findViewById(R.id.activity_user_details_subcategory_other_layout);
        submit = findViewById(R.id.activity_user_details_button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickSubmit(v);
            }
        });

        ArrayAdapterCustomSpinner<String> adapterMain = new ArrayAdapterCustomSpinner<String>(getApplicationContext(), R.layout.spinner_text_normal,
                getResources().getStringArray(R.array.maincategory_array));
        maincategory.setAdapter(adapterMain);

        if(db.findUserDetails() != null){
            userDetails = db.findUserDetails();
            maincategory.setSelection(userDetails.getMaincategory());
            ArrayAdapterCustomSpinner<String> adapter = new ArrayAdapterCustomSpinner<String>(getApplicationContext(), R.layout.spinner_text_normal,
                    getResources().getStringArray(subcategories.get(userDetails.getMaincategory())));
            subcategory.setAdapter(adapter);
            subcategory.setSelection(userDetails.getSubcategory());

            if(userDetails.getMobilenumber() != null && userDetails.getMobilenumber().length() > 5){
                Mobile mobile = new Mobile();
                mobile.setState(userDetails.getMobilenumber().substring(4,6));
                mobile.setNumber(userDetails.getMobilenumber().substring(7).replace("-", ""));
                userDetails.setMobile(mobile);


                if(userDetails.getMobilenumber() != null && userDetails.getMobilenumber().length() > 5){
                    Log.e("getMobilenumber", "userDetails.getMobilenumber()" + userDetails.getMobilenumber());
                    mobileState = findViewById(R.id.activity_user_details_mobile_state);
                    mobileState.setText(userDetails.getMobilenumber().substring(4,6));
                    mobile1 = findViewById(R.id.activity_user_details_mobile_number);
                    mobile1.setText(userDetails.getMobilenumber().substring(7).replace("-", ""));
                }


            }
            initialSubCategory = userDetails.getSubcategory();
            initialSubcategoryDone = false;
        }


        binding.setHandlerform(new Handlerform());
        binding.setUserdetails(userDetails);

    }
    @Override
    public void onBackPressed() {
        if(backEnabled){
            super.onBackPressed();
        }else{
            Toast.makeText(getApplicationContext(), R.string.message_fill_user_details_form,
                    Toast.LENGTH_SHORT).show();
        }
    }
    public class Handlerform {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            othersubcategory.setText("");
            otherscategoryLayout.setVisibility(View.INVISIBLE);
            final int positionfinal = position;
            Log.e("category", getResources().getStringArray(R.array.subcategories_array)  + "");

            ArrayAdapterCustomSpinner<String> adapter = new ArrayAdapterCustomSpinner<String>(getApplicationContext(), R.layout.spinner_text_normal,
                        getResources().getStringArray(subcategories.get(position)));
                subcategory.setAdapter(adapter);
                subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub

                       if(!initialSubcategoryDone){
                           position = initialSubCategory;
                           initialSubcategoryError ++;
                           if(initialSubcategoryError >= 2){
                               initialSubcategoryDone = true;
                           }
                           subcategory.setSelection(position);
                       }
                        Log.e("onItemSelected", "onItemSelected -> SUBCATEGORY listener-> position = " + position);
                        userDetails.setSubcategory(position);
                        if(position < getResources().getStringArray(subcategories.get(positionfinal)).length -1){
                            othersubcategory.setText("");
                            otherscategoryLayout.setVisibility(View.INVISIBLE);
                        }else if(position == 0){
                            othersubcategory.setText("");
                            otherscategoryLayout.setVisibility(View.INVISIBLE);
                        }else{
                            othersubcategory.setText("");
                            otherscategoryLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });

        }

        public void showDatePicker(View v) {
            DialogFragment datepicker = new Datepicker();
            datepicker.show(getSupportFragmentManager(), "date picker");
        }
    }

    public void onclickSubmit(View v){
        errors = new ArrayList<>();

        userDetails.printUserDetails();

        if(userDetails.validator(getApplicationContext(), errors, new View(getApplicationContext())).isEmpty()){

            try {
                  if(db == null) {
                      db = new DatabaseManager(getApplicationContext());
                  }
                      db.saveUserDetailsFromLocal(userDetails);
                      startActivity(new Intent(UserDetailsActivity.this, MainActivity.class));
                      finish();

            }catch(Exception e){e.printStackTrace();}

        }else{
            showErrors();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        svcAuth = Svc.initAuth(UserDetailsActivity.this);
        db = new DatabaseManager(UserDetailsActivity.this);
    }

    private void showErrors(){
        /*
        if(!errors.isEmpty()){
            errors.get(0).getView().setMinimumHeight(20);
            ((TextView) errors.get(0).getView()).setText(errors.get(0).getMessage());

        }
        */
        Toast.makeText(getApplicationContext(), errors.get(0).getMessage(), Toast.LENGTH_SHORT).show();
    }
    public interface DateReceiver {
        void receiveDate(String text);
    }
    public void receiveDate(String text){
        userDetails.setBirthday(text);
        birthday.setText(text);
    }
    public void showDatePicker(View v) {
        DialogFragment datepicker = new Datepicker();
        datepicker.show(getSupportFragmentManager(), "date picker");
    }
    /*
      private void postUserDetails() throws ParseException {
        dialog = Utils.showDialog(dialog, UserDetailsActivity.this, R.string.loading);



        Call<UserDetails> call = svcAuth.postUserDetails(userDetails);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                final Response responsefinal =  response;
                dialog = Utils.hideDialog(dialog);

                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("UserDetails REVEIVED", responsefinal.body().toString());
                            db.saveUserDetails((UserDetails)responsefinal.body());
                            startActivity(new Intent(UserDetailsActivity.this, FinancialControlMainActivity.class));
                            finish();
                        }
                    });
                } else {
                    try{

                        Utils.showErrorsMessagesToast(getApplicationContext(), Utils.getErrors(response.errorBody().string()));
                    }catch(Exception e){e.printStackTrace();}

                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    */
    public static class Datepicker extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Bundle bundle = getArguments();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),  R.style.datePickerTheme, dateSetListener, year, month, day);
            datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
            return datePickerDialog;
        }


        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        ((UserDetailsActivity)  getActivity()).receiveDate(view.getDayOfMonth() +
                                "/" + (view.getMonth() + 1) + "/" + view.getYear());
                    }
                };
    }
}
