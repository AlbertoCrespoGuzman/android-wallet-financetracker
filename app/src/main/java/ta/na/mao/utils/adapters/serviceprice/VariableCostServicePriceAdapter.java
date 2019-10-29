package ta.na.mao.utils.adapters.serviceprice;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.errorprone.annotations.Var;

import java.text.DecimalFormat;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceFormFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.database.models.serviceprice.VariableCost;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class VariableCostServicePriceAdapter extends RecyclerView.Adapter<VariableCostServicePriceAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ServicePriceFormFragment context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<VariableCost> variableCosts;

    public VariableCostServicePriceAdapter(ServicePriceFormFragment context, List<VariableCost> variableCosts) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.context = context;
        db = new DatabaseManager(context.getActivity());
        this.variableCosts = variableCosts;

    }

    @Override
    public VariableCostServicePriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_three_elements, parent, false);
        return new VariableCostServicePriceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VariableCostServicePriceAdapter.ViewHolder holder, int position) {

        if(variableCosts.size() == 0){
            holder.text1.setText(context.getResources().getString(R.string.service_prices_list_empty_list));
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }else{
            final VariableCost variableCost = variableCosts.get(position);
            holder.text1.setText(variableCost.getDescription());
            holder.text2.setText(newFormat.format(variableCost.getPercentage()));
            holder.text3.setText(newFormat.format(variableCost.getValue()));

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });


            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showVariableCostDialog(variableCost.getLocal_id());
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showDeleteVariableCostDialog(variableCost.getLocal_id());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if(variableCosts.size() == 0){
            return 1;
        }else{
            return variableCosts.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text1, text2, text3;
        ConstraintLayout linearLayout;
        RelativeLayout editButton, deleteButton;

        ViewHolder(View itemView) {
            super(itemView);

            text1 = itemView.findViewById(R.id.adapter_simple_three_elements_first);
            text2 = itemView.findViewById(R.id.adapter_simple_three_elements_second);
            text3 = itemView.findViewById(R.id.adapter_simple_three_elements_third);
            linearLayout = itemView.findViewById(R.id.adapter_simple_three_elements_layout);
            editButton = itemView.findViewById(R.id.adapter_simple_three_elements_edit_button);
            deleteButton = itemView.findViewById(R.id.adapter_simple_three_elements_delete_button);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        if(variableCosts.size() == 0){
            return null;
        }else{
            return variableCosts.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}
