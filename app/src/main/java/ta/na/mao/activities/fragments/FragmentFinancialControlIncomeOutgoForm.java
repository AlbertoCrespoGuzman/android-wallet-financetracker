package ta.na.mao.activities.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.databinding.FragmentFinancialControlIncomeOutgoFormBinding;
import ta.na.mao.utils.Defaultdata;
import ta.na.mao.utils.ErrorMessage;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class FragmentFinancialControlIncomeOutgoForm  extends Fragment {

    Transaction  transaction;
    TextView title;
    EditText firstInstallment, transactionDate;
    static  FragmentFinancialControlIncomeOutgoForm fragmentFinancialControlIncomeOutgoForm;
    Handler handler;
    List<ErrorMessage> errors = new ArrayList<>();
    DatabaseManager db;
    Bundle bundle;
    Spinner statementCategoriesIncome, statementCategoriesOutgo;
    EditText valueInput;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentFinancialControlIncomeOutgoFormBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_financial_control_income_outgo_form_, container, false);

        View view = binding.getRoot();

        title = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_form_title);
        firstInstallment = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_form_first_payment);
        transactionDate = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_form_date);
        statementCategoriesIncome = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_form_category_income_spinner);
        statementCategoriesOutgo = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_form_category_outgo_spinner);
        valueInput = view.findViewById(R.id.fragment_financialcontrol_incomeoutgo_form_totalvalue);

        fragmentFinancialControlIncomeOutgoForm = this;
        bundle = getArguments();
        db = new DatabaseManager(getActivity());


        if(bundle.getLong("transaction_local_id", 0) == 0) {
            transaction = new Transaction(getActivity());
            transaction.setIncome(bundle.getBoolean("income", false));
        }else{
            transaction = db.findTransactionByLocalid(bundle.getLong("transaction_local_id"));
            if(transaction == null){
                transaction = new Transaction(getActivity());
                transaction.setIncome(bundle.getBoolean("income", false));

            }else{
                if(transaction.getFrequencyinstallment() == 7 ){
                    transaction.setFrequencyInstallmentForcing(1);
                }else if(transaction.getFrequencyinstallment() == 15){
                    transaction.setFrequencyInstallmentForcing(2);
                }else if(transaction.getFrequencyinstallment() == 30){
                    transaction.setFrequencyInstallmentForcing(3);
                }
            }




        }
        if(!transaction.isIncome()){
            statementCategoriesIncome.setVisibility(View.GONE);
            statementCategoriesOutgo.setVisibility(View.VISIBLE);
        }else{
            statementCategoriesIncome.setVisibility(View.VISIBLE);
            statementCategoriesOutgo.setVisibility(View.GONE);
        }

        Log.e("INCOMEFORM", "EPAAA " + transaction.toString());
        handler = new Handler();

        binding.setTransaction(transaction);
        binding.setHandler(handler);

        if(bundle.getBoolean("income", false)){
            title.setText(getActivity().getResources().getString(R.string.financontrol_incomeoutgo_income_button));
        }else{
            title.setText(getActivity().getResources().getString(R.string.financontrol_incomeoutgo_outgo_button));
        }

        if(valueInput != null){
            valueInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        removeEditTextValueIfZero(valueInput);
                    }
                }
            });
        }


        return view;

    }
    @Override
    public void onResume(){
        super.onResume();
        if(getActivity() instanceof FinancialControlMainActivity){
            if(getActivity() != null){
                ((FinancialControlMainActivity) getActivity()).changeTopBarTitle(Defaultdata.TopBarTitle.INCOME_OUTGO);
            }
        }
    }
    public Handler getHandler(){
        return this.handler;
    }
    public class Handler {
        public void printTransaction(View v) {
            Log.e("Transaction", transaction.toString());
        }

        public void showDatePicker(View v) {
            DialogFragment datepicker = new Datepicker();
            Bundle bundle = new Bundle();
            bundle.putBoolean("transactionDay", false);
            datepicker.setArguments(bundle);
            datepicker.show(getActivity().getSupportFragmentManager(), "date picker");
        }
        public void showDatePickerTransactionDate(View v) {
            DialogFragment datepicker = new Datepicker();
            Bundle bundle = new Bundle();
            bundle.putBoolean("transactionDay", true);
            datepicker.setArguments(bundle);
            datepicker.show(getActivity().getSupportFragmentManager(), "date picker");
        }
        public void receiveTransactionDate(String text){
            transaction.setFirstinstallment(transaction.fromStringtoDate(text));
            transactionDate.setText(text);
        }
        public void receiveDate(String text){
            transaction.setFirstinstallment(transaction.fromStringtoDate(text));
            firstInstallment.setText(text);
        }
        public void saveTransaction(View view){
            printTransaction(null);

                errors = new ArrayList<>();


                if(transaction.validator(getActivity(), errors, new View(getActivity())).isEmpty()){
                    db = new DatabaseManager(getActivity());
                    if(bundle != null&& bundle.getLong("transaction_local_id", 0) != 0){
                        db.removeInstallmentsByTransactionLocalId(bundle.getLong("transaction_local_id", 0));
                    }
                    db.saveTransactionFromLocal(transaction);
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
                    ((FinancialControlMainActivity) getActivity()).changeToFragmentIncomeOutgoMenu();
                }else{
                    showErrors();
                }

        }
    }
    public void showErrors(){
        Toast.makeText(getActivity(), errors.get(0).getMessage(), Toast.LENGTH_SHORT).show();
    }
    public static class Datepicker extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Bundle bundle = getArguments();
            if(bundle.getBoolean("transactionDay")){
                return new DatePickerDialog(getActivity(), R.style.datePickerTheme, dateSetListenerTransactionDate, year, month, day);
            }else{
                return new DatePickerDialog(getActivity(),R.style.datePickerTheme, dateSetListener, year, month, day);
            }

        }


        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        fragmentFinancialControlIncomeOutgoForm.getHandler().receiveDate(view.getDayOfMonth() +
                                "/" + (view.getMonth() + 1) + "/" + view.getYear());
                    }
                };
        private DatePickerDialog.OnDateSetListener dateSetListenerTransactionDate =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        fragmentFinancialControlIncomeOutgoForm.getHandler().receiveTransactionDate(view.getDayOfMonth() +
                                "/" + (view.getMonth() + 1) + "/" + view.getYear());
                    }
                };
    }
}