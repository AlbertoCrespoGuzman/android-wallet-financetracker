package ta.na.mao.utils.adapters.serviceprice;

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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceFormFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class LabourCostServicePriceAdapter  extends RecyclerView.Adapter<LabourCostServicePriceAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ServicePriceFormFragment context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<LabourCost> labourCosts;

    public LabourCostServicePriceAdapter(ServicePriceFormFragment context, List<LabourCost> labourCosts) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.context = context;
        db = new DatabaseManager(context.getActivity());
        this.labourCosts = labourCosts;

    }

    @Override
    public LabourCostServicePriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_four_elements, parent, false);
        return new LabourCostServicePriceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LabourCostServicePriceAdapter.ViewHolder holder, int position) {

        if(labourCosts.size() == 0){
            holder.textFirst.setText(context.getResources().getString(R.string.service_prices_list_empty_list));
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }else{
            final LabourCost labourCost = labourCosts.get(position);
            holder.textFirst.setText(labourCost.getDescription());
            holder.textSecond.setText(newFormat.format(labourCost.getHours()));
            holder.textThird.setText(newFormat.format(labourCost.getRate()));
            holder.textFouth.setText(newFormat.format(labourCost.getTotalcost()));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //
                }
            });
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showLabourCostDialog(labourCost.getLocal_id());
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showDeleteLabourCostDialog(labourCost.getLocal_id());
                }
            });
        }

    }
    public void updateAdapter(List<LabourCost> labourCosts){
        Log.e("updateAdapter","labourCosts -> " + labourCosts.size());
        this.labourCosts = labourCosts;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(labourCosts.size() == 0){
            return 1;
        }else{
            return labourCosts.size();
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
        if(labourCosts.size() == 0){
            return null;
        }else{
            return labourCosts.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}
