package ta.na.mao.activities.fragments.Calculator.ProductPrice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.databinding.ActivityProductPriceFormBinding;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.adapters.productprice.FixedVariableCostProductPriceAdapter;
import ta.na.mao.utils.adapters.productprice.RawProductPriceAdapter;
import ta.na.mao.utils.dialogs.productprice.DeleteProductPriceDialog;
import ta.na.mao.utils.dialogs.productprice.ProductPriceFixedVariableCostFormDialog;
import ta.na.mao.utils.dialogs.productprice.ProductPriceRawFormDialog;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class ProductPriceFormFragment  extends Fragment implements
        ProductPriceFixedVariableCostFormDialog.DialogClickListener, ProductPriceRawFormDialog.DialogClickListener,
DeleteProductPriceDialog.DialogClickListener{


    View view;

    long local_id;
    ProductPriceFixedVariableCostFormDialog productPriceFixedVariableCostFormDialog;
    ProductPriceRawFormDialog productPriceRawFormDialog;
    Button   saveButton, cancelButton;
    ProductPrice productPrice;
    RecyclerView rawRecycler, fixedVariableCostRecycler, overviewRecycler;
    DatabaseManager db;
    RawProductPriceAdapter rawProductPriceAdapter;
    FixedVariableCostProductPriceAdapter fixedVariableCostProductPriceAdapter;
    //ActivityProductPriceFormBinding binding;
    ActivityProductPriceFormBinding binding;
    RelativeLayout addRaw, addFixedVariableCost;

    EditText salePriceEditText, quantityEditText;
    ScrollView scrollView;
    String salePriceValidator = "";
    DecimalFormat newFormat = new DecimalFormat("#0.00");
    Bundle bundle;
    DeleteProductPriceDialog deleteProductPriceDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.activity_product_price_form_, container, false);

        View view = binding.getRoot();

        bundle = getArguments();

        local_id = bundle.getLong("local_id", 0);

        db = new DatabaseManager(getActivity());

        Log.e("SPFormFragment","local_id -> " + local_id);
        db = new DatabaseManager(getActivity());

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

        addRaw = view.findViewById(R.id.activity_product_price_form_raw_add_button);
        addFixedVariableCost = view.findViewById(R.id.activity_product_price_form_fixed_variable_cost_add_button);

        saveButton = view.findViewById(R.id.activity_product_price_form_save_button);
        cancelButton = view.findViewById(R.id.activity_product_price_form_cancel_button);

        salePriceEditText = view.findViewById(R.id.activity_product_price_form_price_edittext);

        salePriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("SalesPrice","NO FOCUS!!!!");
                    db.saveProductpriceFromLocal(productPrice);
                    productPrice = db.findProductPriceByLocalid(local_id, true);
                    fixedVariableCostProductPriceAdapter = new FixedVariableCostProductPriceAdapter
                            (ProductPriceFormFragment.this, productPrice.getFixedvariablecosts());
                    fixedVariableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    fixedVariableCostRecycler.setAdapter(fixedVariableCostProductPriceAdapter);

                    binding.setProductprice(productPrice);
                }if (hasFocus) {
                    removeEditTextValueIfZero(salePriceEditText);
                }
            }
        });
        quantityEditText = view.findViewById(R.id.activity_product_price_form_quantity_edittext);
        quantityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    removeEditTextValueIfZero(quantityEditText);
                }
            }
        });
        scrollView = view.findViewById(R.id.activity_product_price_form_scrollview);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(!salePriceValidator.equals(salePriceEditText.getText().toString())){
                    Log.e("scrollView","updatingSalePrice");
                    salePriceValidator = salePriceEditText.getText().toString();
                    salePriceEditText.clearFocus();

                }

                Utils.hideKeyboard(getActivity());
            }
        });
        rawRecycler = view.findViewById(R.id.activity_product_price_form_raw_recycler);
        fixedVariableCostRecycler = view.findViewById(R.id.activity_product_price_form_fixed_variable_cost_recycler);


        fixedVariableCostProductPriceAdapter = new FixedVariableCostProductPriceAdapter
                (ProductPriceFormFragment.this, productPrice.getFixedvariablecosts());
        fixedVariableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixedVariableCostRecycler.setAdapter(fixedVariableCostProductPriceAdapter);

        rawProductPriceAdapter = new RawProductPriceAdapter
                (ProductPriceFormFragment.this, productPrice.getRaws());
        rawRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        rawRecycler.setAdapter(rawProductPriceAdapter);

/*
        overviewServicePriceAdapter = new OverviewServicePriceAdapter
                (ServicePriceFormActivity.this, servicePrice);
        fixedCostRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fixedCostRecycler.setAdapter(overviewServicePriceAdapter);
        */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ServicePriceForm", "saveButton " + productPrice.toString());
                if(productPrice.validation()){
                    productPrice.setFinished(true);
                    db.saveProductpriceFromLocal(productPrice);
                    ((CalculatorMainActivity)getActivity()).changeToFragmentProductPriceMain();
                }else{
                    Toast.makeText(getActivity(), "Deve inserir um nome e um prec", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productPrice.validation()){
                    productPrice.setFinished(true);
                    db.saveProductpriceFromLocal(productPrice);
                }else{
                    Toast.makeText(getActivity(), "Salvo como rascunho.", Toast.LENGTH_SHORT).show();
                }
                ((CalculatorMainActivity)getActivity()).changeToFragmentProductPriceMain();
            }
        });
        addRaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salePriceEditText.clearFocus();
                db.saveProductpriceFromLocal(productPrice);
                productPriceRawFormDialog = new ProductPriceRawFormDialog(
                        getActivity(), 0, local_id, ProductPriceFormFragment.this);
                productPriceRawFormDialog.getWindow()
                        .setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                productPriceRawFormDialog.show();
            }
        });
        addFixedVariableCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salePriceEditText.clearFocus();
                db.saveProductpriceFromLocal(productPrice);
                productPriceFixedVariableCostFormDialog = new ProductPriceFixedVariableCostFormDialog(
                        getActivity(), 0, local_id, ProductPriceFormFragment.this);
                productPriceFixedVariableCostFormDialog.getWindow()
                        .setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                productPriceFixedVariableCostFormDialog.show();
            }
        });

        return view;

    }
    public void showRawDialog(long local_id){
        salePriceEditText.clearFocus();
        db.saveProductpriceFromLocal(productPrice);
        productPriceRawFormDialog = new ProductPriceRawFormDialog(
                getActivity(), local_id, productPrice.getLocal_id(), ProductPriceFormFragment.this);
        productPriceRawFormDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        productPriceRawFormDialog.show();
    }
    public void showDeleteRawDialog(long local_id){
        deleteProductPriceDialog = new DeleteProductPriceDialog((CalculatorMainActivity) getActivity()
                , null, db.findRawProductPriceByIdLocal(local_id), null,
                ProductPriceFormFragment.this);
        deleteProductPriceDialog.show();
    }
    public void showFixedVariableDialog(long local_id){
        salePriceEditText.clearFocus();
        db.saveProductpriceFromLocal(productPrice);
        productPriceFixedVariableCostFormDialog = new ProductPriceFixedVariableCostFormDialog(
                getActivity(), local_id, productPrice.getLocal_id(),
                ProductPriceFormFragment.this);
        productPriceFixedVariableCostFormDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        productPriceFixedVariableCostFormDialog.show();
    }
    public void showDeleteFixedVariableDialog(long local_id){
        deleteProductPriceDialog = new DeleteProductPriceDialog((CalculatorMainActivity) getActivity()
                , null, null, db.findFixedVariableCostProductPriceByLocalId(local_id),
                ProductPriceFormFragment.this);
        deleteProductPriceDialog.show();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

            }
            return false;
        }
    };

    @Override
    public void onDialogProductPriceRawButtonClick() {
        productPriceRawFormDialog.hide();
        productPrice = db.findProductPriceByLocalid(local_id, true);
        rawProductPriceAdapter = new RawProductPriceAdapter
                (ProductPriceFormFragment.this, productPrice.getRaws());
        rawRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        rawRecycler.setAdapter(rawProductPriceAdapter);
        fixedVariableCostProductPriceAdapter = new FixedVariableCostProductPriceAdapter
                (ProductPriceFormFragment.this, productPrice.getFixedvariablecosts());
        fixedVariableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixedVariableCostRecycler.setAdapter(fixedVariableCostProductPriceAdapter);

        binding.setProductprice(productPrice);

        //REFRESH RECYCLERVIEW
    }

    @Override
    public void onDialogProductPriceFixedVariableCostButtonClick() {
        productPriceFixedVariableCostFormDialog.hide();
        productPrice = db.findProductPriceByLocalid(local_id, true);
        fixedVariableCostProductPriceAdapter = new FixedVariableCostProductPriceAdapter
                (ProductPriceFormFragment.this, productPrice.getFixedvariablecosts());
        fixedVariableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixedVariableCostRecycler.setAdapter(fixedVariableCostProductPriceAdapter);

        binding.setProductprice(productPrice);


    }

    @Override
    public void onDialogProductPriceDelete() {
        productPrice = db.findProductPriceByLocalid(local_id, true);
        rawProductPriceAdapter = new RawProductPriceAdapter
                (ProductPriceFormFragment.this, productPrice.getRaws());
        rawRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        rawRecycler.setAdapter(rawProductPriceAdapter);
        fixedVariableCostProductPriceAdapter = new FixedVariableCostProductPriceAdapter
                (ProductPriceFormFragment.this, productPrice.getFixedvariablecosts());
        fixedVariableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixedVariableCostRecycler.setAdapter(fixedVariableCostProductPriceAdapter);

        binding.setProductprice(productPrice);

    }
}