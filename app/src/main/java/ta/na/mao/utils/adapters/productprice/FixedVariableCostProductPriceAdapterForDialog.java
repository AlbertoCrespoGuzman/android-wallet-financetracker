package ta.na.mao.utils.adapters.productprice;

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
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceMainFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.FixedVariableCost;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class FixedVariableCostProductPriceAdapterForDialog  extends RecyclerView.Adapter<FixedVariableCostProductPriceAdapterForDialog.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ProductPriceMainFragment context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<FixedVariableCost> fixedVariableCosts;

    public FixedVariableCostProductPriceAdapterForDialog(ProductPriceMainFragment context, List<FixedVariableCost> fixedVariableCosts) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.context = context;
        db = new DatabaseManager(context.getActivity());
        this.fixedVariableCosts = fixedVariableCosts;

    }

    @Override
    public FixedVariableCostProductPriceAdapterForDialog.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_three_elements, parent, false);
        return new FixedVariableCostProductPriceAdapterForDialog.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FixedVariableCostProductPriceAdapterForDialog.ViewHolder holder, int position) {

        holder.editButton.setVisibility(View.INVISIBLE);
        holder.deleteButton.setVisibility(View.INVISIBLE);

        if(fixedVariableCosts.size() == 0){
            holder.text1.setText(context.getResources().getString(R.string.service_prices_list_empty_list));

        }else{
            final FixedVariableCost fixedVariableCost = fixedVariableCosts.get(position);
            holder.text1.setText(fixedVariableCost.getDescription());
            holder.text2.setText(newFormat.format(fixedVariableCost.getPercentage()));
            holder.text3.setText(newFormat.format(fixedVariableCost.getValue()));

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(fixedVariableCosts.size() == 0){
            return 1;
        }else{
            return fixedVariableCosts.size();
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
            editButton = itemView.findViewById(R.id.adapter_simple_three_elements_edit_button);
            deleteButton = itemView.findViewById(R.id.adapter_simple_three_elements_delete_button);


            linearLayout = itemView.findViewById(R.id.adapter_simple_three_elements_layout);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        if(fixedVariableCosts.size() == 0){
            return null;
        }else{
            return fixedVariableCosts.get(id);
        }
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}