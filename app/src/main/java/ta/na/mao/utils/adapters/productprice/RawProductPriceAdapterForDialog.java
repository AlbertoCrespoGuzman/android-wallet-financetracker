package ta.na.mao.utils.adapters.productprice;

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
import ta.na.mao.activities.fragments.Calculator.ProductPrice.ProductPriceMainFragment;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.productprice.Raw;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class RawProductPriceAdapterForDialog  extends RecyclerView.Adapter<RawProductPriceAdapterForDialog.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ProductPriceMainFragment context;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    List<Raw> raws;

    public RawProductPriceAdapterForDialog(ProductPriceMainFragment context, List<Raw> raws) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.context = context;
        db = new DatabaseManager(context.getActivity());
        this.raws = raws;

    }

    @Override
    public RawProductPriceAdapterForDialog.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_simple_four_elements, parent, false);
        return new RawProductPriceAdapterForDialog.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RawProductPriceAdapterForDialog.ViewHolder holder, int position) {
        holder.editButton.setVisibility(View.INVISIBLE);
        holder.deleteButton.setVisibility(View.INVISIBLE);
        if(raws.size() == 0){
            holder.textFirst.setText(context.getResources().getString(R.string.service_prices_list_empty_list));

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