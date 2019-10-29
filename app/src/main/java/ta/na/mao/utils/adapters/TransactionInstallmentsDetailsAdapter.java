package ta.na.mao.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;

import static ta.na.mao.utils.Utils.getDate3LettersMonthFormat;

public class TransactionInstallmentsDetailsAdapter  extends RecyclerView.Adapter<TransactionInstallmentsDetailsAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private StatementRecyclerAdapter.ItemClickListener mClickListener;
    SimpleDateFormat format;
    Context context;
    ViewGroup parent;
    DecimalFormat currencyFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;
    Transaction transaction;
    int currentPosition;

    public TransactionInstallmentsDetailsAdapter(Context context, Transaction transaction, int currentPosition) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        format = new SimpleDateFormat("dd/MM/yyyy");
        db = new DatabaseManager(context);
        this.transaction = transaction;
        this.currentPosition = currentPosition;

    }

    @Override
    public TransactionInstallmentsDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_transaction_installments_details_element, parent, false);
        return new TransactionInstallmentsDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionInstallmentsDetailsAdapter.ViewHolder holder, int position) {


        final TransactionInstallmentsDetailsAdapter.ViewHolder holderfinal = holder;

        if(position == 0){
            if(transaction.isInstallment()){
                if(transaction.getEntrance_payment() > 0){
                    if(transaction.getDate().compareTo(new Date()) <= 0){
                        holder.paid.setVisibility(View.VISIBLE);
                    }else{
                        holder.paid.setVisibility(View.INVISIBLE);
                    }
                    String valueText = "";
                    if(transaction.isIncome()){
                        valueText += "+ R$ ";
                        if(transaction.isPaid()){
                            holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                            holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                        }else{

                            holder.value.setTextColor(context.getResources().getColor(R.color.income_green));
                            holder.date.setTextColor(context.getResources().getColor(R.color.black));
                            if(transaction.getEntrance_payment() > 0 &&
                                    transaction.getDate().compareTo(new Date()) <= 0 ){
                                holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                                holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                            }
                        }

                    }else{
                        valueText += "- R$ ";
                        if(transaction.isPaid()){
                            holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                            holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                        }else{
                            holder.value.setTextColor(context.getResources().getColor(R.color.outgo_red));
                            holder.date.setTextColor(context.getResources().getColor(R.color.black));
                            if(transaction.getEntrance_payment() > 0 &&
                                    transaction.getDate().compareTo(new Date()) <= 0 ){
                                holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                                holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                            }
                        }
                    }
                    valueText += currencyFormat.format(transaction.getEntrance_payment());
                    valueText += " (entrada)";
                    holder.value.setText(valueText);
                    holder.date.setText(getDate3LettersMonthFormat(transaction.getDate()));
                }else{
                    holder.paid.setVisibility(View.INVISIBLE);
                }
            }else{
                if(transaction.isPaid()){
                    holder.paid.setVisibility(View.VISIBLE);
                }else{
                    holder.paid.setVisibility(View.INVISIBLE);
                }
                String valueText = "";
                if(transaction.isIncome()){
                    valueText += "+ R$ ";
                    if(transaction.isPaid()){
                        holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                        holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                    }else{
                        holder.value.setTextColor(context.getResources().getColor(R.color.income_green));
                        holder.date.setTextColor(context.getResources().getColor(R.color.black));
                    }

                }else{
                    valueText += "- R$ ";
                    if(transaction.isPaid()){
                        holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                        holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                    }else{
                        holder.value.setTextColor(context.getResources().getColor(R.color.outgo_red));
                        holder.date.setTextColor(context.getResources().getColor(R.color.black));
                    }
                }
                valueText += currencyFormat.format(transaction.getValue());
                holder.value.setText(valueText);
                holder.date.setText(getDate3LettersMonthFormat(transaction.getDate()));
            }
        }else{
            Installment installment = transaction.getInstallments().get(position - 1);

            if(installment.isPaid()){
                holder.paid.setVisibility(View.VISIBLE);
            }else{
                holder.paid.setVisibility(View.INVISIBLE);
            }
            String valueText = "";
            if(installment.isIncome()){
                valueText += "+ R$ ";
                if(installment.isPaid()){
                    holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                    holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                }else{
                    holder.value.setTextColor(context.getResources().getColor(R.color.income_green));
                    holder.date.setTextColor(context.getResources().getColor(R.color.black));
                }

            }else{
                valueText += "- R$ ";
                if(installment.isPaid()){
                    holder.value.setTextColor(context.getResources().getColor(R.color.grey));
                    holder.date.setTextColor(context.getResources().getColor(R.color.grey));
                }else{
                    holder.value.setTextColor(context.getResources().getColor(R.color.outgo_red));
                    holder.date.setTextColor(context.getResources().getColor(R.color.black));
                }
            }
            valueText += currencyFormat.format(installment.getValue());
            holder.value.setText(valueText);

            holder.date.setText(getDate3LettersMonthFormat(installment.getDate()));

        }




    }

    @Override
    public int getItemCount() {
        return transaction.getInstallments().size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView paid;
        TextView date, value;


        ViewHolder(View itemView) {
            super(itemView);
            paid = itemView.findViewById(R.id.adapter_dialog_statement_details_paid);
            date = itemView.findViewById(R.id.adapter_dialog_statement_details_date);
            value = itemView.findViewById(R.id.adapter_dialog_statement_details_paid_value);
            //    itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        if(id == 0){
            return transaction;
        }else{
            return transaction.getInstallments().get(id -1);
        }

    }

    public void setClickListener(StatementRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}