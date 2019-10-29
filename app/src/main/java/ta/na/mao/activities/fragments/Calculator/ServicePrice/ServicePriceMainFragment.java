package ta.na.mao.activities.fragments.Calculator.ServicePrice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.utils.adapters.serviceprice.ServicePriceListAdapter;
import ta.na.mao.utils.dialogs.serviceprice.DeleteServicePriceDialog;
import ta.na.mao.utils.dialogs.serviceprice.ResumeServicePriceDialog;


public class ServicePriceMainFragment  extends Fragment implements DeleteServicePriceDialog.DialogClickListener{

    View view;
    Button addServicePriceButton;
    RecyclerView servicePriceRecycler;
    DatabaseManager db;
    ResumeServicePriceDialog resumeServicePriceDialog;
    CalculatorMainActivity calculatorMainActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view = inflater.inflate(R.layout.activity_service_price_main, container, false);
        }else{
            view = inflater.inflate(R.layout.activity_service_price_main_old, container, false);
        }



        calculatorMainActivity = (CalculatorMainActivity) getActivity();

        addServicePriceButton = view.findViewById(R.id.activity_service_price_main_add_button);
        servicePriceRecycler = view.findViewById(R.id.activity_service_price_main_recyclerview);

        db = new DatabaseManager(getActivity());


        ServicePriceListAdapter adapter = new ServicePriceListAdapter((CalculatorMainActivity) getActivity(),
                db.findAllServicePricesFinished(), ServicePriceMainFragment.this);
        servicePriceRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        servicePriceRecycler.setAdapter(adapter);


        addServicePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.findServicePriceNotFinished().size() > 0){
                    resumeServicePriceDialog = new ResumeServicePriceDialog((CalculatorMainActivity) getActivity());
                    resumeServicePriceDialog.show();
                }else{
                    calculatorMainActivity.changeToFragmentServiceForm(0);
                }
            }
        });

        return view;

    }
    public void showDeleteServicePriceDialog(ServicePrice servicePrice){
        Log.e("","showDeleteServicePriceDialog -> " + servicePrice.toString());
        DeleteServicePriceDialog deleteServicePriceDialog = new DeleteServicePriceDialog((CalculatorMainActivity) getActivity(),
                servicePrice,
                null,
                null,
                null,
                null,
                ServicePriceMainFragment.this);
        deleteServicePriceDialog.show();
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
    public void onDialogServicePriceDelete() {
        ServicePriceListAdapter adapter = new ServicePriceListAdapter((CalculatorMainActivity) getActivity(),
                db.findAllServicePricesFinished(), ServicePriceMainFragment.this);
        servicePriceRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        servicePriceRecycler.setAdapter(adapter);

    }
}