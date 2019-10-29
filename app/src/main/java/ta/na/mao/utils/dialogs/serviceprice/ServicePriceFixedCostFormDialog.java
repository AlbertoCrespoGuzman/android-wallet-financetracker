package ta.na.mao.utils.dialogs.serviceprice;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import androidx.databinding.DataBindingUtil;
import ta.na.mao.R;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.databinding.DialogServicePriceFixedCostFormBinding;

public class ServicePriceFixedCostFormDialog extends Dialog {

    long fixedCostId, servicePriceLocalId;
    ServicePrice servicePrice;

    FixedCost fixedCost;
    DatabaseManager db;
    Button saveButton, cancelButton;
    Activity activity;
    TextView valueTextView;
    ServicePriceFixedCostFormDialog.DialogClickListener dialogClickListener;

    DecimalFormat newFormat = new DecimalFormat("##0.00");

    public ServicePriceFixedCostFormDialog(final Activity activity, long fixedCostId, long servicePriceLocalId,
                                           final  ServicePriceFixedCostFormDialog.DialogClickListener dialogClickListener) {
        super(activity);

        this.activity = activity;

        db = new DatabaseManager(activity);
        this.servicePriceLocalId = servicePriceLocalId;
        this.fixedCostId = fixedCostId;
        this.dialogClickListener = dialogClickListener;

        DialogServicePriceFixedCostFormBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_service_price_fixed_cost_form_, null, false);

        setContentView(binding.getRoot());


        if(fixedCostId == 0){
            fixedCost = new FixedCost();
            fixedCost.setServicepricelocalid(servicePriceLocalId);
        }else{

        }
        servicePrice = db.findServicePriceByLocalid(servicePriceLocalId, false);
        if(servicePrice == null){
            Toast.makeText(getContext(), "Erro no banco de Datos. Service Price sem ID local.", Toast.LENGTH_LONG).show();
            dismiss();
        }
        binding.setFixedcost(fixedCost);

        saveButton = findViewById(R.id.dialog_service_price_fixed_cost_form_save_button);
        cancelButton = findViewById(R.id.dialog_service_price_fixed_cost_form_cancel_button);
        valueTextView = findViewById(R.id.dialog_service_price_fixed_cost_form_value);





        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator()){
                    db.saveFixedCostServicePriceFromLocal(fixedCost);
                    dialogClickListener.onDialogServicePriceFixedCostButtonClick();
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

        if (fixedCost.getDescription() ==  null || fixedCost.getDescription().length() == 0){
            ok = false;
        }
        if(fixedCost.getValue() == 0){
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
        void onDialogServicePriceFixedCostButtonClick();
    }
}
