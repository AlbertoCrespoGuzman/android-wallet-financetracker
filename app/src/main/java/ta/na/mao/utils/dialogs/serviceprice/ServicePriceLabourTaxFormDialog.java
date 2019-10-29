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
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.databinding.DialogServicePriceLabourTaxFormBinding;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class ServicePriceLabourTaxFormDialog extends Dialog {

    long labourCostId, servicePriceLocalId;
    ServicePrice servicePrice;

    LabourTax labourTax;
    DatabaseManager db;
    Button saveButton, cancelButton;
    Activity activity;
    EditText percentageEditText;
    TextView valueTextView;
    ServicePriceLabourTaxFormDialog.DialogClickListener dialogClickListener;

    DecimalFormat newFormat = new DecimalFormat("##0.00");

    public ServicePriceLabourTaxFormDialog(final Activity activity, long labourCostId, long servicePriceLocalId, final  ServicePriceLabourTaxFormDialog.DialogClickListener dialogClickListener) {
        super(activity);

        this.activity = activity;

        db = new DatabaseManager(activity);
        this.servicePriceLocalId = servicePriceLocalId;
        this.labourCostId = labourCostId;
        this.dialogClickListener = dialogClickListener;

        DialogServicePriceLabourTaxFormBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_service_price_labour_tax_form_, null, false);

        setContentView(binding.getRoot());


        if(labourCostId == 0){
            labourTax = new LabourTax();
            labourTax.setServicepricelocalid(servicePriceLocalId);
        }else{

        }
        servicePrice = db.findServicePriceByLocalid(servicePriceLocalId, false);
        if(servicePrice == null){
            Toast.makeText(getContext(), "Erro no banco de Datos. Service Price sem ID local.", Toast.LENGTH_LONG).show();
            dismiss();
        }
        binding.setLabourtax(labourTax);

        saveButton = findViewById(R.id.dialog_service_price_labour_tax_form_save_button);
        cancelButton = findViewById(R.id.dialog_service_price_labour_tax_form_cancel_button);
        percentageEditText = findViewById(R.id.dialog_service_price_labour_tax_form_percentage);
        valueTextView = findViewById(R.id.dialog_service_price_labour_tax_form_value);

        if(percentageEditText != null){
            percentageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        removeEditTextValueIfZero(percentageEditText);
                    }
                }
            });
        }

        percentageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(percentageEditText.length() == 0){
                    valueTextView.setText("" + 0.0);
                }else if(percentageEditText.length() > 0){
                    try{
                        double percentage = Double.parseDouble(percentageEditText.getText().toString());
                        valueTextView.setText((newFormat.format(servicePrice.getTotallabourcost()  * (percentage / 100))));
                        labourTax.setValue(servicePrice.getTotallabourcost()  * (percentage / 100));
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator()){
                    db.saveLabourTaxServicePriceFromLocal(labourTax);
                    dialogClickListener.onDialogServicePriceLabourTaxButtonClick();
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

        if (labourTax.getName() ==  null || labourTax.getName().length() == 0){
            ok = false;
        }
        if(labourTax.getPercentage() == 0){
            ok = false;
        }
        if(labourTax.getPercentage() > 100){
            ok = false;
        }
        if(!ok){
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.dialog_service_price_form_error),
                    Toast.LENGTH_SHORT).show();
        }
        return ok;
    }

    public interface DialogClickListener {
        void onDialogServicePriceLabourTaxButtonClick();
    }
}
