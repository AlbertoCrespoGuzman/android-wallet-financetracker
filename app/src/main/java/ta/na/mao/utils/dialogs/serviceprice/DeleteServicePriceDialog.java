package ta.na.mao.utils.dialogs.serviceprice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.List;

import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.database.models.serviceprice.VariableCost;
import ta.na.mao.utils.dialogs.productprice.DeleteProductPriceDialog;

public class DeleteServicePriceDialog  extends Dialog {

    Button backButton, deleteButton;
    CalculatorMainActivity a;
    DatabaseManager db;
    ServicePrice servicePrice;
    DeleteServicePriceDialog.DialogClickListener dialogClickListener;
    FixedCost fixedCost;
    LabourCost labourCost;
    LabourTax labourTax;
    VariableCost variableCost;

    public DeleteServicePriceDialog(CalculatorMainActivity a, ServicePrice servicePrice,
                                    FixedCost fixedCost,
                                    LabourCost labourCost,
                                    LabourTax labourTax,
                                    VariableCost variableCost,

                                    DeleteServicePriceDialog.DialogClickListener dialogClickListener) {
        super(a);
        this.a = a;
        this.servicePrice = servicePrice;
        this.dialogClickListener = dialogClickListener;
        this.fixedCost =  fixedCost;
        this.labourCost = labourCost;
        this.labourTax = labourTax;
        this.variableCost = variableCost;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_service_price);

        backButton = findViewById(R.id.dialog_delete_service_price_back_button);
        deleteButton = findViewById(R.id.dialog_delete_service_price_delete_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DatabaseManager(a);
                if(servicePrice != null){
                    db.removeServicePrice(servicePrice);
                }else if(fixedCost != null){
                    db.removeFixedCostServicePrice(fixedCost, true);
                }else if(labourCost != null){
                    db.removeLabourCostServicePrice(labourCost, true);
                }else if(labourTax != null){
                    db.removeLabourTaxServicePrice(labourTax, true);
                }else if(variableCost != null){
                    db.removeVariableCostServicePrice(variableCost, true);
                }

                dialogClickListener.onDialogServicePriceDelete();
                dismiss();
            }
        });

    }
    public interface DialogClickListener {
        void onDialogServicePriceDelete();
    }

}