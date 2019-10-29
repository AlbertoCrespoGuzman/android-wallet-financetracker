package ta.na.mao.utils.dialogs.serviceprice;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import java.text.DecimalFormat;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceMainFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.databinding.ActivityServicePriceFormBinding;
import ta.na.mao.utils.adapters.serviceprice.FixedCostServicePriceAdapterForDialog;
import ta.na.mao.utils.adapters.serviceprice.LabourCostServicePriceAdapterForDialog;
import ta.na.mao.utils.adapters.serviceprice.LabourTaxServicePriceAdapterForDialog;
import ta.na.mao.utils.adapters.serviceprice.VariableCostServicePriceAdapterForDialog;


public class ServicePriceDialog  extends Dialog {

    CalculatorMainActivity a;
    DatabaseManager db;
    ServicePrice servicePrice;
    ServicePriceMainFragment servicePricePriceMainFragment;
    long local_id;
    ActivityServicePriceFormBinding binding;

    Button  saveButton, cancelButton;
    RecyclerView labourCostRecycler, labourTaxRecycler, variableCostRecycler, fixedCostRecycler, overviewRecycler;
    LabourCostServicePriceAdapterForDialog labourCostServicePriceAdapter;
    LabourTaxServicePriceAdapterForDialog labourTaxServicePriceAdapter;
    VariableCostServicePriceAdapterForDialog variableCostServicePriceAdapter;
    FixedCostServicePriceAdapterForDialog fixedCostServicePriceAdapter;

    EditText salePriceEditText;
    String salePriceValidator = "";
    DecimalFormat newFormat = new DecimalFormat("#0.00");
    RelativeLayout addLabourCost, addLabourTax, addVariableCost, addFixedCost;
    RelativeLayout activity_product_price_form_dialog_back_layout;
    Button activity_product_price_form_dialog_back_button;

    public ServicePriceDialog(CalculatorMainActivity a,
                              ServicePriceMainFragment servicePricePriceMainFragment,
                              ServicePrice servicePrice, long local_id) {
        super(a);
        this.a = a;
        this.servicePrice = servicePrice;
        this.local_id = local_id;
        this.servicePricePriceMainFragment = servicePricePriceMainFragment;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DataBindingUtil.inflate(LayoutInflater.from(this.getContext()),R.layout.activity_service_price_form_,null,false);


        setContentView(binding .getRoot());

        db = new DatabaseManager(a);

        if(local_id != 0){
            servicePrice = db.findServicePriceByLocalid(local_id, false);
            servicePrice.setFinished(false);
        }else{
            servicePrice = new ServicePrice();
            local_id = db.saveServicepriceFromLocal(servicePrice);

        }

        binding.setServiceprice(servicePrice);
        salePriceValidator = newFormat.format(servicePrice.getSaleprice());

        Log.e("ServicePriceForm", "local_id = " + local_id);

        addLabourCost = findViewById(R.id.activity_service_price_form_labour_cost_add_button);
        addLabourTax = findViewById(R.id.activity_service_price_form_labour_tax_add_button);
        addVariableCost = findViewById(R.id.activity_service_price_form_variable_cost_add_button);
        addFixedCost  = findViewById(R.id.activity_service_price_form_fixed_cost_add_button);

        saveButton = findViewById(R.id.activity_service_price_form_save_button);
        cancelButton = findViewById(R.id.activity_service_price_form_cancel_button);

        salePriceEditText = findViewById(R.id.activity_service_price_form_price_edittext);
        salePriceEditText.setEnabled(false);


        labourCostRecycler = findViewById(R.id.activity_service_price_form_labour_cost_recycler);
        labourTaxRecycler = findViewById(R.id.activity_service_price_form_labour_tax_recycler);
        variableCostRecycler = findViewById(R.id.activity_service_price_form_variable_cost_recycler);
        fixedCostRecycler = findViewById(R.id.activity_service_price_form_fixed_cost_recycler);
        //   overviewRecycler = findViewById(R.id.activity_service_price_form_overview_recycler);


        labourCostServicePriceAdapter = new LabourCostServicePriceAdapterForDialog
                (servicePricePriceMainFragment, servicePrice.getLabourcosts());
        labourCostRecycler.setLayoutManager(new LinearLayoutManager(a));
        labourCostRecycler.setAdapter(labourCostServicePriceAdapter);

        labourTaxServicePriceAdapter = new LabourTaxServicePriceAdapterForDialog
                (servicePricePriceMainFragment, servicePrice.getLabourtaxs());
        labourTaxRecycler.setLayoutManager(new LinearLayoutManager(a));
        labourTaxRecycler.setAdapter(labourTaxServicePriceAdapter);

        variableCostServicePriceAdapter = new VariableCostServicePriceAdapterForDialog
                (servicePricePriceMainFragment, servicePrice.getVariablecosts());
        variableCostRecycler.setLayoutManager(new LinearLayoutManager(a));
        variableCostRecycler.setAdapter(variableCostServicePriceAdapter);

        fixedCostServicePriceAdapter = new FixedCostServicePriceAdapterForDialog
                (servicePricePriceMainFragment, servicePrice.getFixedcosts());
        fixedCostRecycler.setLayoutManager(new LinearLayoutManager(a));
        fixedCostRecycler.setAdapter(fixedCostServicePriceAdapter);


        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        addLabourCost.setVisibility(View.GONE);
        addLabourTax.setVisibility(View.GONE);
        addVariableCost.setVisibility(View.GONE);
        addFixedCost.setVisibility(View.GONE);


        activity_product_price_form_dialog_back_layout = findViewById(R.id.activity_product_price_form_dialog_back_layout);
        activity_product_price_form_dialog_back_layout.setVisibility(View.VISIBLE);

        activity_product_price_form_dialog_back_button = findViewById(R.id.activity_product_price_form_dialog_back_button);
        activity_product_price_form_dialog_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }
    public interface DialogClickListener {
        void onDialogProductPrice();
    }

}