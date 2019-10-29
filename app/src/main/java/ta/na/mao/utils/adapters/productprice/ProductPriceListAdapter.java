package ta.na.mao.utils.adapters.productprice;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.CalculatorMainActivity;
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceFormFragment;
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceMainFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;
import ta.na.mao.utils.dialogs.productprice.DeleteProductPriceDialog;
import ta.na.mao.utils.dialogs.productprice.ProductPriceDialog;

public class ProductPriceListAdapter extends RecyclerView.Adapter<ProductPriceListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    CalculatorMainActivity calculatorMainActivity;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<ProductPrice> productPrices;
    ProductPriceMainFragment productPriceMainFragment;

    public ProductPriceListAdapter(CalculatorMainActivity calculatorMainActivity,
                                   List<ProductPrice> productPrices,
                                   ProductPriceMainFragment productPriceMainFragment) {
        this.mInflater = LayoutInflater.from(calculatorMainActivity);
        this.calculatorMainActivity = calculatorMainActivity;
        db = new DatabaseManager(calculatorMainActivity);
        this.productPrices = productPrices;
        this.productPriceMainFragment = productPriceMainFragment;

    }

    @Override
    public ProductPriceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_calculator_price_element , parent, false);
        return new ProductPriceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductPriceListAdapter.ViewHolder holder, int position) {


            final ProductPrice productPrice = productPrices.get(position);
            Log.e("productPrice",productPrice.toString());
            holder.name.setText(productPrice.getName());
            holder.price.setText("R$ "  + newFormat.format(productPrice.getTotalsaleprice()  ));
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ADAPTER", "local_id ->" + productPrice.getLocal_id());
                    calculatorMainActivity.changeToFragmentProductForm(productPrice.getLocal_id());
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productPriceMainFragment.showDeleteProductPriceDialog(productPrice);
                }
            });
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // DIALOG
                    ProductPriceDialog productPriceDialog =
                            new ProductPriceDialog((CalculatorMainActivity)calculatorMainActivity,
                                    productPriceMainFragment,  productPrice, productPrice.getLocal_id());
                    productPriceDialog.show();

                }
            });


    }

    @Override
    public int getItemCount() {

            return productPrices.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, price;
        RelativeLayout edit, delete;
        LinearLayout linearLayout;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.adapter_calculator_price_name);
            price = itemView.findViewById(R.id.adapter_calculator_price_value);
            edit = itemView.findViewById(R.id.adapter_calculator_price_edit_button);
            delete = itemView.findViewById(R.id.adapter_calculator_price_delete_button);
            linearLayout = itemView.findViewById(R.id.adapter_calculator_price_white_layout);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        if(productPrices.size() == 0){
            return null;
        }else{
            return productPrices.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}