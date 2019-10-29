package ta.na.mao.utils.dialogs.productprice;

import android.app.Dialog;
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

import java.text.DecimalFormat;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceMainFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.databinding.ActivityProductPriceFormBinding;
import ta.na.mao.utils.adapters.productprice.FixedVariableCostProductPriceAdapterForDialog;
import ta.na.mao.utils.adapters.productprice.RawProductPriceAdapterForDialog;


public class ProductPriceDialog  extends Dialog {

    View view;

    long local_id;
    Button   saveButton, cancelButton;
    ProductPrice productPrice;
    RecyclerView rawRecycler, fixedVariableCostRecycler;
    DatabaseManager db;
    RawProductPriceAdapterForDialog rawProductPriceAdapter;
    FixedVariableCostProductPriceAdapterForDialog fixedVariableCostProductPriceAdapter;
    ActivityProductPriceFormBinding binding;
    RelativeLayout addRaw, addFixedVariableCost;

    EditText salePriceEditText, activity_product_price_form_name_edittext, activity_product_price_form_quantity_edittext;
    ScrollView scrollView;
    String salePriceValidator = "";
    DecimalFormat newFormat = new DecimalFormat("#0.00");
    CalculatorMainActivity a;
    ProductPriceMainFragment productPriceMainFragment;
    RelativeLayout activity_product_price_form_dialog_back_layout;
    Button activity_product_price_form_dialog_back_button;

    public ProductPriceDialog(CalculatorMainActivity a, ProductPriceMainFragment productPriceMainFragment,
                              ProductPrice productPrice, long local_id) {
        super(a);
        this.a = a;
        this.productPrice = productPrice;
        this.local_id = local_id;
        this.productPriceMainFragment = productPriceMainFragment;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        binding = DataBindingUtil.inflate(LayoutInflater.from(this.getContext()),R.layout.activity_product_price_form_,null,false);

        setContentView(binding .getRoot());

        db = new DatabaseManager(a);

        if(local_id != 0){
            productPrice = db.findProductPriceByLocalid(local_id, false);
            productPrice.setFinished(false);
        }else{
            productPrice = new ProductPrice();
            local_id = db.saveProductpriceFromLocal(productPrice);
            Log.e("productPrice", "productPrice local_id after temporaly saved -> " + local_id);
        }

        binding.setProductprice(productPrice);

        salePriceValidator = newFormat.format(productPrice.getUnitsaleprice());

        Log.e("ProductPriceForm", "local_id = " + local_id);
        activity_product_price_form_name_edittext = findViewById(R.id.activity_product_price_form_name_edittext);
        activity_product_price_form_name_edittext.setEnabled(false);

        activity_product_price_form_quantity_edittext = findViewById(R.id.activity_product_price_form_quantity_edittext);
        activity_product_price_form_quantity_edittext.setEnabled(false);

        activity_product_price_form_dialog_back_layout = findViewById(R.id.activity_product_price_form_dialog_back_layout);
        activity_product_price_form_dialog_back_layout.setVisibility(View.VISIBLE);

        activity_product_price_form_dialog_back_button = findViewById(R.id.activity_product_price_form_dialog_back_button);
        activity_product_price_form_dialog_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        addRaw = findViewById(R.id.activity_product_price_form_raw_add_button);
        addFixedVariableCost = findViewById(R.id.activity_product_price_form_fixed_variable_cost_add_button);

        saveButton = findViewById(R.id.activity_product_price_form_save_button);
        cancelButton = findViewById(R.id.activity_product_price_form_cancel_button);

        salePriceEditText = findViewById(R.id.activity_product_price_form_price_edittext);


        rawRecycler = findViewById(R.id.activity_product_price_form_raw_recycler);
        fixedVariableCostRecycler = findViewById(R.id.activity_product_price_form_fixed_variable_cost_recycler);


        fixedVariableCostProductPriceAdapter = new FixedVariableCostProductPriceAdapterForDialog
                (productPriceMainFragment, productPrice.getFixedvariablecosts());
        fixedVariableCostRecycler.setLayoutManager(new LinearLayoutManager(a));
        fixedVariableCostRecycler.setAdapter(fixedVariableCostProductPriceAdapter);

        rawProductPriceAdapter = new RawProductPriceAdapterForDialog
                (productPriceMainFragment, productPrice.getRaws());
        rawRecycler.setLayoutManager(new LinearLayoutManager(a));
        rawRecycler.setAdapter(rawProductPriceAdapter);

        salePriceEditText.setEnabled(false);
        addRaw.setVisibility(View.GONE);
        addFixedVariableCost.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);

    }
    public interface DialogClickListener {
        void onDialogProductPrice();
    }

}
