package ta.na.mao.utils.adapters.serviceprice;

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
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceFormFragment;
import ta.na.mao.activities.fragments.Calculator.ServicePrice.ServicePriceMainFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class FixedCostServicePriceAdapterForDialog   extends RecyclerView.Adapter<FixedCostServicePriceAdapterForDialog.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ServicePriceMainFragment context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<FixedCost> fixedCosts;

    public FixedCostServicePriceAdapterForDialog(ServicePriceMainFragment context, List<FixedCost> fixedCosts) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.context = context;
        db = new DatabaseManager(context.getActivity());
        this.fixedCosts = fixedCosts;

    }

    @Override
    public FixedCostServicePriceAdapterForDialog.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_two_elements, parent, false);
        return new FixedCostServicePriceAdapterForDialog.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FixedCostServicePriceAdapterForDialog.ViewHolder holder, int position) {

        holder.editButton.setVisibility(View.INVISIBLE);
        holder.deleteButton.setVisibility(View.INVISIBLE);

        if(fixedCosts.size() == 0){
            holder.text1.setText(context.getResources().getString(R.string.service_prices_list_empty_list));
        }else{
            final FixedCost fixedCost = fixedCosts.get(position);
            holder.text1.setText(fixedCost.getDescription());
            holder.text2.setText(newFormat.format(fixedCost.getValue()));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  context.showFixedCostDialog(fixedCost.getLocal_id());
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  context.showDeleteFixedCostDialog(fixedCost.getLocal_id());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if(fixedCosts.size() == 0){
            return 1;
        }else{
            return fixedCosts.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text1, text2;
        ConstraintLayout linearLayout;

        RelativeLayout editButton, deleteButton;

        ViewHolder(View itemView) {
            super(itemView);

            text1 = itemView.findViewById(R.id.adapter_simple_two_elements_left_element);
            text2 = itemView.findViewById(R.id.adapter_simple_two_elements_right_element);
            linearLayout = itemView.findViewById(R.id.adapter_simple_two_elements_layout);
            editButton = itemView.findViewById(R.id.adapter_simple_two_elements_edit_button);
            deleteButton = itemView.findViewById(R.id.adapter_simple_two_elements_delete_button);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        if(fixedCosts.size() == 0){
            return null;
        }else{
            return fixedCosts.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}