package ta.na.mao.activities.fragments.Calculator.ProductPrice;

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
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.utils.adapters.productprice.ProductPriceListAdapter;
import ta.na.mao.utils.dialogs.productprice.DeleteProductPriceDialog;
import ta.na.mao.utils.dialogs.productprice.ResumeProductPriceDialog;

public class ProductPriceMainFragment  extends Fragment implements DeleteProductPriceDialog.DialogClickListener {

    Button addProductPriceButton;
    RecyclerView productPriceRecycler;
    DatabaseManager db;
    ResumeProductPriceDialog resumeProductPriceDialog;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view = inflater.inflate(R.layout.activity_product_price_main, container, false);
        }else{
            view = inflater.inflate(R.layout.activity_product_price_main_old, container, false);
        }


        addProductPriceButton = view.findViewById(R.id.activity_product_price_main_add_button);
        productPriceRecycler = view.findViewById(R.id.activity_product_price_main_recyclerview);

        db = new DatabaseManager(getActivity());


        ProductPriceListAdapter adapter = new ProductPriceListAdapter((CalculatorMainActivity)getActivity(),
                db.findAllProductPricesFinished(), this);
        productPriceRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        productPriceRecycler.setAdapter(adapter);


        addProductPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.findProductPriceNotFinished().size() > 0){
                    resumeProductPriceDialog = new ResumeProductPriceDialog((CalculatorMainActivity) getActivity());
                    resumeProductPriceDialog.show();
                }else{
                    ((CalculatorMainActivity) getActivity()).changeToFragmentProductForm(0);
                }
            }
        });

        return view;

    }
    public void showDeleteProductPriceDialog(ProductPrice productPrice){
        Log.e("","showDeleteProductPriceDialog -> " + productPrice.toString());
        DeleteProductPriceDialog deleteProductPriceDialog = new DeleteProductPriceDialog((CalculatorMainActivity) getActivity(),
                productPrice,null, null, ProductPriceMainFragment.this);
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
    public void onDialogProductPriceDelete() {
        ProductPriceListAdapter adapter = new ProductPriceListAdapter((CalculatorMainActivity)getActivity(),
                db.findAllProductPricesFinished(), this);
        productPriceRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        productPriceRecycler.setAdapter(adapter);
    }
}