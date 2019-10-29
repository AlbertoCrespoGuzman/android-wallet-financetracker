package ta.na.mao.activities.fragments.Calculator.ServicePrice;

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
import ta.na.mao.activities.MainActivity;
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceFormFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.databinding.ActivityServicePriceFormBinding;
import ta.na.mao.utils.Utils;
import ta.na.mao.utils.adapters.serviceprice.FixedCostServicePriceAdapter;
import ta.na.mao.utils.adapters.serviceprice.LabourCostServicePriceAdapter;
import ta.na.mao.utils.adapters.serviceprice.LabourTaxServicePriceAdapter;
import ta.na.mao.utils.adapters.serviceprice.VariableCostServicePriceAdapter;
import ta.na.mao.utils.dialogs.productprice.DeleteProductPriceDialog;
import ta.na.mao.utils.dialogs.productprice.ProductPriceRawFormDialog;
import ta.na.mao.utils.dialogs.serviceprice.DeleteServicePriceDialog;
import ta.na.mao.utils.dialogs.serviceprice.ServicePriceFixedCostFormDialog;
import ta.na.mao.utils.dialogs.serviceprice.ServicePriceLabourCostFormDialog;
import ta.na.mao.utils.dialogs.serviceprice.ServicePriceLabourTaxFormDialog;
import ta.na.mao.utils.dialogs.serviceprice.ServicePriceVariableCostFormDialog;

import static ta.na.mao.utils.Utils.removeEditTextValueIfZero;

public class ServicePriceFormFragment  extends Fragment  implements
        ServicePriceLabourCostFormDialog.DialogClickListener, ServicePriceLabourTaxFormDialog.DialogClickListener,
        ServicePriceVariableCostFormDialog.DialogClickListener, ServicePriceFixedCostFormDialog.DialogClickListener,
DeleteServicePriceDialog.DialogClickListener{

    View view;
    long local_id;
    ServicePriceLabourCostFormDialog servicePriceLabourCostFormDialog;
    ServicePriceLabourTaxFormDialog servicePriceLabourTaxFormDialog;
    Button  saveButton, cancelButton;
    ServicePrice servicePrice;
    RecyclerView labourCostRecycler, labourTaxRecycler, variableCostRecycler, fixedCostRecycler, overviewRecycler;
    DatabaseManager db;
    LabourCostServicePriceAdapter labourCostServicePriceAdapter;
    LabourTaxServicePriceAdapter labourTaxServicePriceAdapter;
    VariableCostServicePriceAdapter variableCostServicePriceAdapter;
    FixedCostServicePriceAdapter fixedCostServicePriceAdapter;
    ActivityServicePriceFormBinding binding;
    ServicePriceVariableCostFormDialog servicePriceVariableCostFormDialog;
    ServicePriceFixedCostFormDialog servicePriceFixedCostFormDialog;

    EditText salePriceEditText;
    ScrollView scrollView;
    String salePriceValidator = "";
    DecimalFormat newFormat = new DecimalFormat("#0.00");
    Bundle bundle;
    RelativeLayout addLabourCost, addLabourTax, addVariableCost, addFixedCost;
    DeleteServicePriceDialog deleteServicePriceDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(
                inflater, R.layout.activity_service_price_form_, container, false);

        View view = binding.getRoot();

        bundle = getArguments();

        local_id = bundle.getLong("local_id", 0);

        db = new DatabaseManager(getActivity());

        Log.e("SPFormFragment","local_id -> " + local_id);
        if(local_id != 0){
            servicePrice = db.findServicePriceByLocalid(local_id, false);
            servicePrice.setFinished(false);
        }else{
            servicePrice = new ServicePrice();
            local_id = db.saveServicepriceFromLocal(servicePrice);
            Log.e("ServicePrice", "ServicePrice local_id after temporaly saved -> " + local_id);
            Log.e("ServicePrice", servicePrice.toString());
        }

        binding.setServiceprice(servicePrice);
        salePriceValidator = newFormat.format(servicePrice.getSaleprice());

        Log.e("ServicePriceForm", "local_id = " + local_id);

        addLabourCost = view.findViewById(R.id.activity_service_price_form_labour_cost_add_button);
        addLabourTax = view.findViewById(R.id.activity_service_price_form_labour_tax_add_button);
        addVariableCost = view.findViewById(R.id.activity_service_price_form_variable_cost_add_button);
        addFixedCost  = view.findViewById(R.id.activity_service_price_form_fixed_cost_add_button);

        saveButton = view.findViewById(R.id.activity_service_price_form_save_button);
        cancelButton = view.findViewById(R.id.activity_service_price_form_cancel_button);

        salePriceEditText = view.findViewById(R.id.activity_service_price_form_price_edittext);
        salePriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Log.e("salePriceEditText","salePriceEditText hasFocus -> removeEditTextValueIfZero");
                    removeEditTextValueIfZero(salePriceEditText);
                }
            }
        });
        salePriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e("SalesPrice","NO FOCUS!!!!");
                    try{
                        servicePrice.setSaleprice(Double.parseDouble(salePriceEditText.getText().toString()));
                    }catch (Exception e){e.printStackTrace();}
                    db.saveServicepriceFromLocal(servicePrice);
                    servicePrice = db.findServicePriceByLocalid(local_id, true);
                    variableCostServicePriceAdapter = new VariableCostServicePriceAdapter
                            (ServicePriceFormFragment.this, servicePrice.getVariablecosts());
                    variableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    variableCostRecycler.setAdapter(variableCostServicePriceAdapter);

                    binding.setServiceprice(servicePrice);
                }if (hasFocus) {
                    Log.e("salePriceEditText","salePriceEditText hasFocus -> removeEditTextValueIfZero");
                    removeEditTextValueIfZero(salePriceEditText);
                }
            }
        });
        scrollView = view.findViewById(R.id.activity_service_price_form_scrollview);
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
        labourCostRecycler = view.findViewById(R.id.activity_service_price_form_labour_cost_recycler);
        labourTaxRecycler = view.findViewById(R.id.activity_service_price_form_labour_tax_recycler);
        variableCostRecycler = view.findViewById(R.id.activity_service_price_form_variable_cost_recycler);
        fixedCostRecycler = view.findViewById(R.id.activity_service_price_form_fixed_cost_recycler);
        //   overviewRecycler = findViewById(R.id.activity_service_price_form_overview_recycler);


        labourCostServicePriceAdapter = new LabourCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getLabourcosts());
        labourCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        labourCostRecycler.setAdapter(labourCostServicePriceAdapter);

        labourTaxServicePriceAdapter = new LabourTaxServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getLabourtaxs());
        labourTaxRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        labourTaxRecycler.setAdapter(labourTaxServicePriceAdapter);

        variableCostServicePriceAdapter = new VariableCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getVariablecosts());
        variableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        variableCostRecycler.setAdapter(variableCostServicePriceAdapter);

        fixedCostServicePriceAdapter = new FixedCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getFixedcosts());
        fixedCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixedCostRecycler.setAdapter(fixedCostServicePriceAdapter);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ServicePriceForm", "saveButton " + servicePrice.toString());
                if(servicePrice.validation()){
                    servicePrice.setFinished(true);
                    db.saveServicepriceFromLocal(servicePrice);
                    Toast.makeText(getActivity(), "Salvo com sucesso.", Toast.LENGTH_SHORT).show();
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }else{
                    Toast.makeText(getActivity(), "Deve inserir um nome e um preco", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CalculatorMainActivity)getActivity()).changeToFragmentServicePriceMain();
            }
        });
        addLabourCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salePriceEditText.clearFocus();
                db.saveServicepriceFromLocal(servicePrice);
                servicePriceLabourCostFormDialog = new ServicePriceLabourCostFormDialog(
                        getActivity(), 0, local_id, ServicePriceFormFragment.this);
                servicePriceLabourCostFormDialog.getWindow()
                        .setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                servicePriceLabourCostFormDialog.show();
            }
        });
        addLabourTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salePriceEditText.clearFocus();
                db.saveServicepriceFromLocal(servicePrice);
                servicePriceLabourTaxFormDialog = new ServicePriceLabourTaxFormDialog(
                        getActivity(), 0, local_id, ServicePriceFormFragment.this);
                servicePriceLabourTaxFormDialog.getWindow()
                        .setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                servicePriceLabourTaxFormDialog.show();
            }
        });
        addVariableCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salePriceEditText.clearFocus();
                db.saveServicepriceFromLocal(servicePrice);
                servicePriceVariableCostFormDialog = new ServicePriceVariableCostFormDialog(
                        getActivity(), 0, local_id, ServicePriceFormFragment.this);
                servicePriceVariableCostFormDialog.getWindow()
                        .setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                servicePriceVariableCostFormDialog.show();
            }
        });
        addFixedCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salePriceEditText.clearFocus();
                db.saveServicepriceFromLocal(servicePrice);
                servicePriceFixedCostFormDialog = new ServicePriceFixedCostFormDialog(
                        getActivity(), 0, local_id, ServicePriceFormFragment.this);
                servicePriceFixedCostFormDialog.getWindow()
                        .setLayout(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                servicePriceFixedCostFormDialog.show();
            }
        });

        return view;

    }

    public void showFixedCostDialog(long local_id){
        salePriceEditText.clearFocus();
        db.saveServicepriceFromLocal(servicePrice);
        servicePriceFixedCostFormDialog = new ServicePriceFixedCostFormDialog(
                getActivity(), local_id, servicePrice.getLocal_id(), ServicePriceFormFragment.this);
        servicePriceFixedCostFormDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        servicePriceFixedCostFormDialog.show();
    }
    public void showDeleteFixedCostDialog(long local_id){
        deleteServicePriceDialog = new DeleteServicePriceDialog((CalculatorMainActivity) getActivity()
                , null, db.findFixedCostServicePriceByLocalId(local_id),
                null,
                null,
                null,
                ServicePriceFormFragment.this);
        deleteServicePriceDialog.show();
    }

    public void showLabourCostDialog(long local_id){
        salePriceEditText.clearFocus();
        db.saveServicepriceFromLocal(servicePrice);
        servicePriceLabourCostFormDialog = new ServicePriceLabourCostFormDialog(
                getActivity(), local_id, servicePrice.getLocal_id(), ServicePriceFormFragment.this);
        servicePriceLabourCostFormDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        servicePriceLabourCostFormDialog.show();
    }
    public void showDeleteLabourCostDialog(long local_id){
        deleteServicePriceDialog = new DeleteServicePriceDialog((CalculatorMainActivity) getActivity()
                , null, null,
                db.findLabourCostServicePriceByIdServer(local_id),
                null,
                null,
                ServicePriceFormFragment.this);
        deleteServicePriceDialog.show();
    }
    public void showLabourTaxDialog(long local_id){
        salePriceEditText.clearFocus();
        db.saveServicepriceFromLocal(servicePrice);
        servicePriceLabourTaxFormDialog = new ServicePriceLabourTaxFormDialog(
                getActivity(), 0, local_id, ServicePriceFormFragment.this);
        servicePriceLabourTaxFormDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        servicePriceLabourTaxFormDialog.show();
    }
    public void showDeleteLabourTaxDialog(long local_id){
        deleteServicePriceDialog = new DeleteServicePriceDialog((CalculatorMainActivity) getActivity()
                , null, null,
                null,
                db.findLabourTaxServicePriceByLocalId(local_id),
                null,
                ServicePriceFormFragment.this);
        deleteServicePriceDialog.show();
    }
    public void showVariableCostDialog(long local_id){
        salePriceEditText.clearFocus();
        db.saveServicepriceFromLocal(servicePrice);
        servicePriceVariableCostFormDialog = new ServicePriceVariableCostFormDialog(
                getActivity(), 0, local_id, ServicePriceFormFragment.this);
        servicePriceVariableCostFormDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        servicePriceVariableCostFormDialog.show();
    }
    public void showDeleteVariableCostDialog(long local_id){
        deleteServicePriceDialog = new DeleteServicePriceDialog((CalculatorMainActivity) getActivity()
                , null, null,
                null,
                null,
                db.findVariableCostServicePriceByLocalId(local_id),
                ServicePriceFormFragment.this);
        deleteServicePriceDialog.show();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Menu 1");
    }

    @Override
    public void onDialogServicePriceLabourCostButtonClick() {
        servicePriceLabourCostFormDialog.hide();
        servicePrice = db.findServicePriceByLocalid(local_id, true);
        labourCostServicePriceAdapter = new LabourCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getLabourcosts());
        labourCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        labourCostRecycler.setAdapter(labourCostServicePriceAdapter);
        labourTaxServicePriceAdapter = new LabourTaxServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getLabourtaxs());
        labourTaxRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        labourTaxRecycler.setAdapter(labourTaxServicePriceAdapter);

        binding.setServiceprice(servicePrice);


    }

    @Override
    public void onDialogServicePriceLabourTaxButtonClick() {
        servicePriceLabourTaxFormDialog.hide();
        servicePrice = db.findServicePriceByLocalid(local_id, true);
        labourTaxServicePriceAdapter = new LabourTaxServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getLabourtaxs());
        labourTaxRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        labourTaxRecycler.setAdapter(labourTaxServicePriceAdapter);

        binding.setServiceprice(servicePrice);


    }

    @Override
    public void onDialogServicePriceVaribleCostButtonClick() {
        servicePriceVariableCostFormDialog.hide();
        servicePrice = db.findServicePriceByLocalid(local_id, true);
        variableCostServicePriceAdapter = new VariableCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getVariablecosts());
        variableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        variableCostRecycler.setAdapter(variableCostServicePriceAdapter);

        binding.setServiceprice(servicePrice);
    }

    @Override
    public void onDialogServicePriceFixedCostButtonClick() {
        servicePriceFixedCostFormDialog.hide();
        servicePrice = db.findServicePriceByLocalid(local_id, true);
        fixedCostServicePriceAdapter = new FixedCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getFixedcosts());
        fixedCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixedCostRecycler.setAdapter(fixedCostServicePriceAdapter);

        binding.setServiceprice(servicePrice);
    }

    @Override
    public void onDialogServicePriceDelete() {
        servicePrice = db.findServicePriceByLocalid(local_id, true);
        labourCostServicePriceAdapter = new LabourCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getLabourcosts());
        labourCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        labourCostRecycler.setAdapter(labourCostServicePriceAdapter);

        labourTaxServicePriceAdapter = new LabourTaxServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getLabourtaxs());
        labourTaxRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        labourTaxRecycler.setAdapter(labourTaxServicePriceAdapter);

        variableCostServicePriceAdapter = new VariableCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getVariablecosts());
        variableCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        variableCostRecycler.setAdapter(variableCostServicePriceAdapter);

        fixedCostServicePriceAdapter = new FixedCostServicePriceAdapter
                (ServicePriceFormFragment.this, servicePrice.getFixedcosts());
        fixedCostRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixedCostRecycler.setAdapter(fixedCostServicePriceAdapter);

        binding.setServiceprice(servicePrice);

    }
}