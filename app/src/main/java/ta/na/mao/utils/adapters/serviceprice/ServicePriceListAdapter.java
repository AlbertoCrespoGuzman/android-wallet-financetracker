package ta.na.mao.utils.adapters.serviceprice;


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
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceMainFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;
import ta.na.mao.utils.dialogs.serviceprice.ServicePriceDialog;

public class ServicePriceListAdapter extends RecyclerView.Adapter<ServicePriceListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    CalculatorMainActivity calculatorMainActivity;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<ServicePrice> servicePrices;
    ServicePriceMainFragment servicePriceMainFragment;

    public ServicePriceListAdapter(CalculatorMainActivity calculatorMainActivity,
                                   List<ServicePrice> servicePrices, ServicePriceMainFragment servicePriceMainFragment) {
        this.mInflater = LayoutInflater.from(calculatorMainActivity);
        this.calculatorMainActivity = calculatorMainActivity;
        db = new DatabaseManager(calculatorMainActivity);
        this.servicePrices = servicePrices;
        this.servicePriceMainFragment = servicePriceMainFragment;

    }

    @Override
    public ServicePriceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_calculator_price_element, parent, false);
        return new ServicePriceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServicePriceListAdapter.ViewHolder holder, int position) {


            final ServicePrice servicePrice = servicePrices.get(position);
            holder.name.setText(servicePrice.getName());
            holder.price.setText("R$ "  + newFormat.format(servicePrice.getSaleprice()  ));
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ADAPTER", "local_id ->" + servicePrice.getLocal_id());
                    calculatorMainActivity.changeToFragmentServiceForm(servicePrice.getLocal_id());
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    servicePriceMainFragment.showDeleteServicePriceDialog(servicePrice);
                }
            });
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // DIALOG
                    ServicePriceDialog servicePriceDialog =
                            new ServicePriceDialog((CalculatorMainActivity)calculatorMainActivity, servicePriceMainFragment, servicePrice, servicePrice.getLocal_id());
                    servicePriceDialog.show();
                }
            });


    }

    @Override
    public int getItemCount() {
        return servicePrices.size();

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
        if(servicePrices.size() == 0){
            return null;
        }else{
            return servicePrices.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}