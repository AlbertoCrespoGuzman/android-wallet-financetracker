package ta.na.mao.utils.adapters.serviceprice;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class OverviewServicePriceAdapter  extends RecyclerView.Adapter<OverviewServicePriceAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    Activity context;
    ViewGroup parent;
    DatabaseManager db;
    ServicePrice servicePrice;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");

    public OverviewServicePriceAdapter(Activity context, ServicePrice servicePrice) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        db = new DatabaseManager(context);
        this.servicePrice = servicePrice;

    }

    @Override
    public OverviewServicePriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_two_elements, parent, false);
        return new OverviewServicePriceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OverviewServicePriceAdapter.ViewHolder holder, int position) {

        switch (position){
            case 0:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_description_title));
                holder.rightElement.setText(context.getResources().getString(R.string.overview_service_price_value_title));
                holder.layout.setBackgroundColor(Color.parseColor("#C0c0c0c0"));
                break;
            case 1:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_labour_cost_title));
                holder.rightElement.setText(newFormat.format(servicePrice.getTotallabourcost()));
                holder.layout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                break;
            case 2:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_labour_tax_title));
                holder.rightElement.setText(newFormat.format(servicePrice.getTotallabourtax()));
                holder.layout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                break;
            case 3:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_variable_cost_title));
                holder.rightElement.setText(newFormat.format(servicePrice.getTotalvariablecost()));
                holder.layout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                break;
            case 4:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_fixed_cost_title));
                holder.rightElement.setText(newFormat.format(servicePrice.getTotalfixedcost()));
                holder.layout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                break;
            case 5:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_total_value_title));
                holder.rightElement.setText(newFormat.format(servicePrice.getTotalcost()));
                holder.layout.setBackgroundColor(Color.parseColor("#C0c0c0c0"));
                break;
            case 6:

                break;
            case 7:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_sale_price_title));
                holder.rightElement.setText(newFormat.format(servicePrice.getSaleprice()));
                holder.layout.setBackgroundColor(Color.parseColor("#C0c0c0c0"));
                break;
            case 8:

                break;
            case 9:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_profit_title));
                holder.rightElement.setText(newFormat.format(servicePrice.getProfit()));
                holder.layout.setBackgroundColor(Color.parseColor("#C0c0c0c0"));
                break;
            case 10:

                break;
            case 11:
                holder.leftElement.setText(context.getResources().getString(R.string.overview_service_price_profit_margin_title));
                holder.rightElement.setText(String.valueOf(servicePrice.getProfitmargin()));
                holder.layout.setBackgroundColor(Color.parseColor("#C0c0c0c0"));
                break;


        }



    }
    public void updateServicePrice(ServicePrice servicePrice){
        this.servicePrice = servicePrice;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return 12;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView leftElement, rightElement;
        ConstraintLayout layout;

        ViewHolder(View itemView) {
            super(itemView);

            leftElement = itemView.findViewById(R.id.adapter_simple_two_elements_left_element);
            rightElement = itemView.findViewById(R.id.adapter_simple_two_elements_right_element);
            layout = itemView.findViewById(R.id.adapter_simple_two_elements_layout);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        return null;
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}