package ta.na.mao.utils.dialogs.serviceprice;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import androidx.databinding.DataBindingUtil;
import ta.na.mao.R;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.databinding.DialogServicePriceLabourCostFormBinding;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class ServicePriceLabourCostFormDialog extends Dialog {

    long labourCostId, servicePriceLocalId;
    LabourCost labourCost;
    DatabaseManager db;
    Button saveButton, cancelButton;
    Activity activity;
    EditText hoursEditText, rateEditText;
    TextView totalCostTextView;
    ServicePriceLabourCostFormDialog.DialogClickListener dialogClickListener;

    DecimalFormat newFormat = new DecimalFormat("##0.00");

    public ServicePriceLabourCostFormDialog(final Activity activity, long labourCostId, long servicePriceLocalId, final  ServicePriceLabourCostFormDialog.DialogClickListener dialogClickListener) {
        super(activity);

        this.activity = activity;

        db = new DatabaseManager(activity);
        this.servicePriceLocalId = servicePriceLocalId;
        this.labourCostId = labourCostId;
        this.dialogClickListener = dialogClickListener;

        DialogServicePriceLabourCostFormBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_service_price_labour_cost_form_, null, false);

        setContentView(binding.getRoot());


        if(labourCostId == 0){
            labourCost = new LabourCost();
            labourCost.setServicepricelocalid(servicePriceLocalId);
        }else{

        }

        binding.setLabourcost(labourCost);

        saveButton = findViewById(R.id.dialog_service_price_labour_cost_form_save_button);
        cancelButton = findViewById(R.id.dialog_service_price_labour_cost_form_cancel_button);
        hoursEditText = findViewById(R.id.dialog_service_price_labour_cost_form_hours);
        rateEditText = findViewById(R.id.dialog_service_price_labour_cost_form_ratio);
        totalCostTextView = findViewById(R.id.dialog_service_price_labour_cost_form_totalcost);

        if(hoursEditText != null){
            hoursEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                            removeEditTextValueIfZero(hoursEditText);
                    }
                }
            });
        }
        if(rateEditText != null){
            rateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        removeEditTextValueIfZero(rateEditText);
                    }
                }
            });
        }
        hoursEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(hoursEditText.length() == 0 || rateEditText.length() == 0){
                    totalCostTextView.setText("" + 0.0);
                }else if(hoursEditText.length() > 0 && rateEditText.length() > 0){
                    try{
                        double rate = Double.parseDouble(rateEditText.getText().toString());
                        int hours = Integer.parseInt(hoursEditText.getText().toString());
                        totalCostTextView.setText(newFormat.format((rate  * hours)));
                        labourCost.setTotalcost(rate  * hours);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });

        rateEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(hoursEditText.length() == 0 || rateEditText.length() == 0){
                    totalCostTextView.setText("" + 0.0);
                }else if(hoursEditText.length() > 0 && rateEditText.length() > 0){
                    try{
                        double rate = Double.parseDouble(rateEditText.getText().toString());
                        int hours = Integer.parseInt(hoursEditText.getText().toString());
                        totalCostTextView.setText(((Double)(rate  * hours)).toString());
                        labourCost.setTotalcost(rate  * hours);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator()){
                    db.saveLabourCostServicePriceFromLocal(labourCost);
                    dialogClickListener.onDialogServicePriceLabourCostButtonClick();
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

    boolean validator(){
        boolean ok = true;

        if (labourCost.getDescription() ==  null || labourCost.getDescription().length() == 0){
            ok = false;
        }
        if(labourCost.getHours() == 0){
            ok = false;
        }
        if(labourCost.getRate() == 0){
            ok = false;
        }
        if(labourCost.getTotalcost() == 0){
            ok =false;
        }
        if(!ok){
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.dialog_service_price_form_error),
                    Toast.LENGTH_SHORT).show();
        }
        return ok;
    }

    public interface DialogClickListener {
        void onDialogServicePriceLabourCostButtonClick();
    }
    }
