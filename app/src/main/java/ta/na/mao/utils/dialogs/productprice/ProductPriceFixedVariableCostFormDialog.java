package ta.na.mao.utils.dialogs.productprice;

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
import ta.na.mao.database.models.productprice.FixedVariableCost;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.databinding.DialogProductPriceFixedVariableCostFormBinding;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class ProductPriceFixedVariableCostFormDialog extends Dialog {

    long fixedVariableCostId, productPriceLocalId;
    ProductPrice productPrice;

    FixedVariableCost fixedVariableCost;
    DatabaseManager db;
    Button saveButton, cancelButton;
    Activity activity;
    EditText percentageEditText;
    TextView valueTextView;
    ProductPriceFixedVariableCostFormDialog.DialogClickListener dialogClickListener;

    DecimalFormat newFormat = new DecimalFormat("##0.00");

    public ProductPriceFixedVariableCostFormDialog(final Activity activity, long fixedVariableCostId, final long productPriceLocalId,
                                                   final  ProductPriceFixedVariableCostFormDialog.DialogClickListener dialogClickListener) {
        super(activity);

        this.activity = activity;

        db = new DatabaseManager(activity);
        this.productPriceLocalId = productPriceLocalId;
        this.fixedVariableCostId = fixedVariableCostId;
        this.dialogClickListener = dialogClickListener;

        DialogProductPriceFixedVariableCostFormBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_product_price_fixed_variable_cost_form_, null, false);

        setContentView(binding.getRoot());


        if(fixedVariableCostId == 0){
            fixedVariableCost = new FixedVariableCost();
            fixedVariableCost.setProductpricelocalid(productPriceLocalId);
        }else{
            fixedVariableCost = db.findFixedVariableCostProductPriceByLocalId(fixedVariableCostId);
        }
        productPrice = db.findProductPriceByLocalid(productPriceLocalId, false);
        if(productPrice == null){
            Toast.makeText(getContext(), "Erro no banco de Datos. Service Price sem ID local.", Toast.LENGTH_LONG).show();
            dismiss();
        }
        binding.setFixedvariablecost(fixedVariableCost);

        saveButton = findViewById(R.id.dialog_product_price_fixed_variable_cost_form_save_button);
        cancelButton = findViewById(R.id.dialog_product_price_fixed_variable_cost_form_cancel_button);
        percentageEditText = findViewById(R.id.dialog_product_price_fixed_variable_cost_form_percentage);
        valueTextView = findViewById(R.id.dialog_product_price_fixed_variable_cost_form_value);

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
                        valueTextView.setText((newFormat.format(productPrice.getUnitsaleprice()  * (percentage / 100))));
                        fixedVariableCost.setValue(productPrice.getUnitsaleprice()  * (percentage / 100));
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator()){
                    db.saveFixedVariableCostProductPriceFromLocal(fixedVariableCost);
                    dialogClickListener.onDialogProductPriceFixedVariableCostButtonClick();
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

        if (fixedVariableCost.getDescription() ==  null || fixedVariableCost.getDescription().length() == 0){
            ok = false;
        }
        if(fixedVariableCost.getPercentage() == 0){
            ok = false;
        }
        if(fixedVariableCost.getPercentage() > 100){
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
        void onDialogProductPriceFixedVariableCostButtonClick();
    }
}