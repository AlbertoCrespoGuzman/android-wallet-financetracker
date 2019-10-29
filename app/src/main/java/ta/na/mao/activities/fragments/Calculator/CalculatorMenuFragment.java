package ta.na.mao.activities.fragments.Calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.activities.MainActivity;

public class CalculatorMenuFragment  extends Fragment {
    View view;
    Button servicePriceButton, productPriceButton, productCostButton, backButton;
    CalculatorMainActivity calculatorMainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main_calculator, container, false);

        calculatorMainActivity = (CalculatorMainActivity) getActivity();

        servicePriceButton = view.findViewById(R.id.calculator_main_activity_serviceprice_button);
        productPriceButton = view.findViewById(R.id.calculator_main_activity_productprice_button);
        productCostButton = view.findViewById(R.id.calculator_main_activity_productcost_button);
        backButton = view.findViewById(R.id.calculator_main_activity_back_button);

        servicePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatorMainActivity.changeToFragmentServicePriceMain();
            }
        });
        productPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatorMainActivity.changeToFragmentProductPriceMain();
            }
        });
        productCostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatorMainActivity.changeToFragmentProductCostMain();
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));

            }
        });
        return view;

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

}