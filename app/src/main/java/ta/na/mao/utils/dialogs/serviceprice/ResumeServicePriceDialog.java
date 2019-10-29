package ta.na.mao.utils.dialogs.serviceprice;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.ServicePrice;

public class ResumeServicePriceDialog extends Dialog {

    Button continueButton,  deleteButton;
    CalculatorMainActivity a;
    DatabaseManager db;
    List<ServicePrice> servicePricesNotFinished;

    public ResumeServicePriceDialog(CalculatorMainActivity a){
        super(a);
        this.a = a;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_resume_service_price);

        db = new DatabaseManager(a);
        servicePricesNotFinished = db.findServicePriceNotFinished();

        continueButton = findViewById(R.id.dialog_resume_service_price_continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.changeToFragmentServiceForm(servicePricesNotFinished.get(0).getLocal_id());
                dismiss();
            }
        });
        deleteButton = findViewById(R.id.dialog_resume_service_price_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(ServicePrice servicePrice : servicePricesNotFinished){
                    db.removeServicePrice(servicePrice);
                }
                servicePricesNotFinished = db.findServicePriceNotFinished();
                if(servicePricesNotFinished != null && !servicePricesNotFinished.isEmpty()){
                    a.changeToFragmentServiceForm(servicePricesNotFinished.get(0).getLocal_id());
                }else{
                    a.changeToFragmentServiceForm(0);
                }

                dismiss();
            }
        });



    }


}