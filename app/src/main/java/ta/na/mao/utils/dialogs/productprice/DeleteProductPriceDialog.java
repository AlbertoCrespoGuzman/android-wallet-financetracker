package ta.na.mao.utils.dialogs.productprice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.List;

import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.FixedVariableCost;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.productprice.Raw;

public class DeleteProductPriceDialog  extends Dialog {

    Button backButton, deleteButton;
    CalculatorMainActivity a;
    DatabaseManager db;
    List<ProductPrice> productPricesNotFinished;
    ProductPrice productPrice;
    Raw raw;
    FixedVariableCost fixedVariableCost;

    DeleteProductPriceDialog.DialogClickListener dialogClickListener;

    public DeleteProductPriceDialog(CalculatorMainActivity a, ProductPrice productPrice, Raw raw, FixedVariableCost fixedVariableCost,
                                    DeleteProductPriceDialog.DialogClickListener dialogClickListener) {
        super(a);
        this.a = a;
        this.productPrice = productPrice;
        this.dialogClickListener = dialogClickListener;
        this.raw = raw;
        this.fixedVariableCost = fixedVariableCost;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_product_price);

        backButton = findViewById(R.id.dialog_delete_product_price_back_button);
        deleteButton = findViewById(R.id.dialog_delete_product_price_delete_button);

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

                if(productPrice != null){
                    db.removeProductPrice(productPrice);
                }else if(raw != null){
                    db.removeRawProductPrice(raw, true);
                }else if(fixedVariableCost!= null){
                    db.removeFixedVariableCostProductPrice(fixedVariableCost, true);
                }

                dialogClickListener.onDialogProductPriceDelete();
                dismiss();
            }
        });

    }
    public interface DialogClickListener {
        void onDialogProductPriceDelete();
    }

}