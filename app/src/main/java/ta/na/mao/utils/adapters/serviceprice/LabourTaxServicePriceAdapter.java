package ta.na.mao.utils.adapters.serviceprice;

import android.app.Activity;
import android.content.Intent;
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
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class LabourTaxServicePriceAdapter  extends RecyclerView.Adapter<LabourTaxServicePriceAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ServicePriceFormFragment context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<LabourTax> labourTaxes;

    public LabourTaxServicePriceAdapter(ServicePriceFormFragment context, List<LabourTax> labourTaxes) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.context = context;
        db = new DatabaseManager(context.getActivity());
        this.labourTaxes = labourTaxes;

    }

    @Override
    public LabourTaxServicePriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_three_elements, parent, false);
        return new LabourTaxServicePriceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LabourTaxServicePriceAdapter.ViewHolder holder, int position) {

        if(labourTaxes.size() == 0){
            holder.text1.setText(context.getResources().getString(R.string.service_prices_list_empty_list));
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }else{
            final LabourTax labourTax = labourTaxes.get(position);
            holder.text1.setText(labourTax.getName());
            holder.text2.setText(newFormat.format(labourTax.getPercentage()));
            holder.text3.setText(newFormat.format(labourTax.getValue()));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showLabourTaxDialog(labourTax.getLocal_id());
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showDeleteLabourTaxDialog(labourTax.getLocal_id());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if(labourTaxes.size() == 0){
            return 1;
        }else{
            return labourTaxes.size();
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
        if(labourTaxes.size() == 0){
            return null;
        }else{
            return labourTaxes.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}