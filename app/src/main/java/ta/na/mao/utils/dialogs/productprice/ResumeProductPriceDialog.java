package ta.na.mao.utils.dialogs.productprice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.List;

import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.ProductPrice;

public class ResumeProductPriceDialog extends Dialog {

    Button continueButton,  deleteButton;
    CalculatorMainActivity a;
    DatabaseManager db;
    List<ProductPrice> productPricesNotFinished;

    public ResumeProductPriceDialog(CalculatorMainActivity a){
        super(a);
        this.a = a;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_resume_product_price);

        db = new DatabaseManager(a);
        productPricesNotFinished = db.findProductPriceNotFinished();

        continueButton = findViewById(R.id.dialog_resume_product_price_continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.changeToFragmentProductForm(productPricesNotFinished.get(0).getLocal_id());
                dismiss();
            }
        });
        deleteButton = findViewById(R.id.dialog_resume_product_price_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(ProductPrice productPrice : productPricesNotFinished){
                    db.removeProductPrice(productPrice);
                }
                productPricesNotFinished = db.findProductPriceNotFinished();
                if(productPricesNotFinished != null && !productPricesNotFinished.isEmpty()){
                    a.changeToFragmentProductForm(productPricesNotFinished.get(0).getLocal_id());
                }else{
                    a.changeToFragmentProductForm(0);
                }

                dismiss();
            }
        });



    }


}