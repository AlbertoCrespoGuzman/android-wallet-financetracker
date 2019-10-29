package ta.na.mao.utils.adapters.mainactivityviewpager;

import android.content.Context;
import android.content.Intent;
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
import ta.na.mao.database.models.productprice.ProductPrice;
import ta.na.mao.database.models.serviceprice.ServicePrice;
import ta.na.mao.utils.adapters.StatementRecyclerAdapter;

public class CalculatorViewPagerAdapter extends RecyclerView.Adapter<CalculatorViewPagerAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    List<Object> calculatorElements;
    Context context;

    public CalculatorViewPagerAdapter(Context context,
                                      List<Object> calculatorElements) {
        this.mInflater = LayoutInflater.from(context);
        this.calculatorElements = calculatorElements;
        this.context = context;
    }

    @Override
    public CalculatorViewPagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_main_view_pager_price_element, parent, false);
        return new CalculatorViewPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalculatorViewPagerAdapter.ViewHolder holder, int position) {


        if(calculatorElements.size() > position){
            if(calculatorElements.get(position) instanceof ServicePrice) {
                final ServicePrice servicePrice = (ServicePrice) calculatorElements.get(position);
                holder.name.setText(servicePrice.getName());
                holder.price.setText("R$ " + newFormat.format(servicePrice.getSaleprice()));
                holder.edit.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent calculatorMainActivity = new Intent(((context)), CalculatorMainActivity.class);

                        ((context)).startActivity(calculatorMainActivity);
                    }
                });
            }
            if(calculatorElements.get(position) instanceof ProductPrice) {
                final ProductPrice productPrice = (ProductPrice) calculatorElements.get(position);
                holder.name.setText(productPrice.getName());
                holder.price.setText("R$ " + newFormat.format(productPrice.getTotalsaleprice()));
                holder.edit.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent calculatorMainActivity = new Intent(((context)), CalculatorMainActivity.class);

                        ((context)).startActivity(calculatorMainActivity);
                    }
                });
            }
        }else{
            holder.name.setText(context.getResources().getString(R.string.add_new_service_price_title));
            holder.price.setText("R$ 00.0");
            holder.edit.setVisibility(View.INVISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent calculatorMainActivity = new Intent(((context)), CalculatorMainActivity.class);

                    calculatorMainActivity.putExtra(context.getResources().getString(
                            R.string.calculator_main_activity_change_to_service_price_main_fragment), true);
                    ((context)).startActivity(calculatorMainActivity);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return 1;

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
        return 1;
    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}