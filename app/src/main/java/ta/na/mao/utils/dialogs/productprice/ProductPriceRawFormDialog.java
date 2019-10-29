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
import ta.na.mao.database.models.productprice.Raw;
import ta.na.mao.databinding.DialogProductPriceRawFormBinding;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class ProductPriceRawFormDialog extends Dialog {

    long rawId, productPriceLocalId;
    Raw raw;
    DatabaseManager db;
    Button saveButton, cancelButton;
    Activity activity;
    EditText quantityEditText, unitCostEditText;
    TextView totalCostTextView;
    ProductPriceRawFormDialog.DialogClickListener dialogClickListener;

    DecimalFormat newFormat = new DecimalFormat("##0.00");

    public ProductPriceRawFormDialog(final Activity activity, long rawId, long productPriceLocalId,
                                     final  ProductPriceRawFormDialog.DialogClickListener dialogClickListener) {
        super(activity);

        this.activity = activity;

        db = new DatabaseManager(activity);
        this.productPriceLocalId = productPriceLocalId;
        this.rawId = rawId;
        this.dialogClickListener = dialogClickListener;

        DialogProductPriceRawFormBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_product_price_raw_form_, null, false);

        setContentView(binding.getRoot());


        if(rawId == 0){
            raw = new Raw();
            raw.setProductpricelocalid(productPriceLocalId);
        }else{
            raw = db.findRawProductPriceByIdLocal(rawId);
        }

        binding.setRaw(raw);

        saveButton = findViewById(R.id.dialog_product_price_raw_form_save_button);
        cancelButton = findViewById(R.id.dialog_product_price_raw_form_cancel_button);
        quantityEditText = findViewById(R.id.dialog_product_price_raw_form_quantity);
        unitCostEditText = findViewById(R.id.dialog_product_price_raw_form_unit_cost);
        totalCostTextView = findViewById(R.id.dialog_product_price_raw_form_total_cost);

        if(quantityEditText != null){
            quantityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        removeEditTextValueIfZero(quantityEditText);
                    }
                }
            });
        }
        if(unitCostEditText != null){
            unitCostEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        removeEditTextValueIfZero(unitCostEditText);
                    }
                }
            });
        }
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(quantityEditText.length() == 0 || unitCostEditText.length() == 0){
                    totalCostTextView.setText("" + 0.0);
                }else if(quantityEditText.length() > 0 && unitCostEditText.length() > 0){
                    try{
                        double unitCost = Double.parseDouble(unitCostEditText.getText().toString());
                        int quantity = Integer.parseInt(quantityEditText.getText().toString());
                        totalCostTextView.setText(newFormat.format((unitCost  * quantity)));
                        raw.setTotalcost(unitCost  * quantity);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });

        unitCostEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(quantityEditText.length() == 0 || unitCostEditText.length() == 0){
                    totalCostTextView.setText("" + 0.0);
                }else if(quantityEditText.length() > 0 && unitCostEditText.length() > 0){
                    try{
                        double unitCost = Double.parseDouble(unitCostEditText.getText().toString());
                        int quantity = Integer.parseInt(quantityEditText.getText().toString());
                        totalCostTextView.setText(newFormat.format((unitCost  * quantity)));
                        raw.setTotalcost(unitCost  * quantity);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validator()){
                    db.saveRawProductPriceFromLocal(raw);
                    dialogClickListener.onDialogProductPriceRawButtonClick();
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

        if (raw.getDescription() ==  null || raw.getDescription().length() == 0){
            ok = false;
        }
        if(raw.getQuantity() == 0){
            ok = false;
        }
        if(raw.getUnitcost() == 0){
            ok = false;
        }
        if(raw.getTotalcost() == 0){
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
        void onDialogProductPriceRawButtonClick();
    }
}