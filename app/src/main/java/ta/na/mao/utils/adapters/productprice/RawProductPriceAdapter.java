package ta.na.mao.utils.adapters.productprice;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceFormFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.Raw;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;
import ta.na.mao.utils.adapters.serviceprice.LabourCostServicePriceAdapter;

public class RawProductPriceAdapter  extends RecyclerView.Adapter<RawProductPriceAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ProductPriceFormFragment context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<Raw> raws;

    public RawProductPriceAdapter(ProductPriceFormFragment context, List<Raw> raws) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.context = context;
        db = new DatabaseManager(context.getActivity());
        this.raws = raws;

    }

    @Override
    public RawProductPriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_four_elements, parent, false);
        return new RawProductPriceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RawProductPriceAdapter.ViewHolder holder, int position) {

        if(raws.size() == 0){
            holder.textFirst.setText(context.getResources().getString(R.string.service_prices_list_empty_list));
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }else{

            final Raw raw = raws.get(position);
            Log.e("onBindViewHolder","raw " + raw.toString());
            holder.textFirst.setText(raw.getDescription());
            holder.textSecond.setText(newFormat.format(raw.getQuantity()));
            holder.textThird.setText(newFormat.format(raw.getUnitcost()));
            holder.textFouth.setText(newFormat.format(raw.getTotalcost()));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //
                }
            });

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showRawDialog(raw.getLocal_id());
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showDeleteRawDialog(raw.getLocal_id());
                }
            });

        }

    }
    public void updateAdapter(List<LabourCost> labourCosts){
        Log.e("updateAdapter","labourCosts -> " + labourCosts.size());
        this.raws = raws;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(raws.size() == 0){
            return 1;
        }else{
            return raws.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textFirst, textSecond, textThird, textFouth;
        ConstraintLayout linearLayout;
        RelativeLayout editButton, deleteButton;

        ViewHolder(View itemView) {
            super(itemView);

            textFirst = itemView.findViewById(R.id.adapter_simple_four_elements_first_element);
            textSecond = itemView.findViewById(R.id.adapter_simple_four_elements_second_element);
            textThird = itemView.findViewById(R.id.adapter_simple_four_elements_third_element);
            textFouth = itemView.findViewById(R.id.adapter_simple_four_elements_fourth_element);
            linearLayout = itemView.findViewById(R.id.adapter_simple_four_elements_layout);
            editButton = itemView.findViewById(R.id.adapter_simple_four_elements_edit_button);
            deleteButton = itemView.findViewById(R.id.adapter_simple_four_elements_delete_button);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        if(raws.size() == 0){
            return null;
        }else{
            return raws.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}