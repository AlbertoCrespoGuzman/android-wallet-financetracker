package ta.na.mao.utils.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;
import ta.na.mao.R;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.StatementElement;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.utils.dialogs.DialogStatementDetails;

import static ta.na.mao.utils.Utils.getDate3LettersMonthFormat;

public class StatementRecyclerAdapter extends RecyclerView.Adapter<StatementRecyclerAdapter.ViewHolder> {

    private List<StatementElement> statementElements = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    SimpleDateFormat format;
    Activity activity;
    ViewGroup parent;
    DecimalFormat newFormat = new DecimalFormat("###,###,##0.00");
    DatabaseManager db;

    public StatementRecyclerAdapter(Activity activity, List<StatementElement> statementElements) {
        this.mInflater = LayoutInflater.from(activity);
        this.activity = activity;
        Locale localeBR = new Locale("pt", "BR");

        format = new SimpleDateFormat("E '-' dd MMMM", localeBR);
        db = new DatabaseManager(activity);

        this.statementElements = statementElements;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = mInflater.inflate(R.layout.adapter_statement_content_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StatementElement statementElement = statementElements.get(position);
        final ViewHolder holderfinal = holder;


        if(((StatementElement) statementElement).getTransaction() != null){
            Transaction transaction = ((StatementElement) statementElement).getTransaction();

            if(transaction.isUpdated()){
                holder.saved.setVisibility(View.VISIBLE);
                holder.notSaved.setVisibility(View.GONE);
            }else {
                holder.saved.setVisibility(View.GONE);
                holder.notSaved.setVisibility(View.VISIBLE);
            }

            holder.date.setText(getDate3LettersMonthFormat(transaction.getDate()));
            if(!transaction.isInstallment()){
                if(transaction.isIncome()){
                    holder.value.setTextColor(activity.getResources().getColor(R.color.income_green));
                    holder.value.setText("+ R$ " + newFormat.format(transaction.getPayment()));
                    holder.moneyBagPlus.setVisibility(View.VISIBLE);
                    holder.moneyBagMinus.setVisibility(View.GONE);
                }else{
                    holder.value.setText("- R$ " + newFormat.format(transaction.getPayment()));
                    holder.value.setTextColor(activity.getResources().getColor(R.color.outgo_red));
                    holder.moneyBagMinus.setVisibility(View.VISIBLE);
                    holder.moneyBagPlus.setVisibility(View.GONE);
                }
            }else{
             //   holder.installmentOrNotText.setText(context.getResources().getString(R.string.installement_entrance_value_text));
                if(transaction.isIncome()){
                    holder.value.setText("+ R$ " + newFormat.format(transaction.getEntrance_payment()));
                    holder.value.setTextColor(activity.getResources().getColor(R.color.income_green));
                    holder.moneyBagPlus.setVisibility(View.VISIBLE);
                    holder.moneyBagMinus.setVisibility(View.GONE);
                }else{
                    holder.value.setText("- R$ " + newFormat.format(transaction.getEntrance_payment()));
                    holder.value.setTextColor(activity.getResources().getColor(R.color.outgo_red));
                    holder.moneyBagMinus.setVisibility(View.VISIBLE);
                    holder.moneyBagPlus.setVisibility(View.GONE);
                }
            }
            if(transaction.getDate().compareTo(new Date()) > 0){
                holder.installmentAccount.setText(" (0/1)");
                holder.installmentAccount.setTextColor(activity.getResources().getColor(R.color.filter_yellow));
            }
/*
            if(transaction.isPaid()){
                holder.paidOrNotText.setText(context.getResources().getString(R.string.transaction_paid_text));
                holder.paidOrNotText.setTextColor(context.getResources().getColor(R.color.transaction_income_value_green));
            }else{
                holder.paidOrNotText.setText(context.getResources().getString(R.string.transaction_tending_text));
                holder.paidOrNotText.setTextColor(context.getResources().getColor(R.color.transaction_pendent_payment));
            }
            */
        }else if(((StatementElement) statementElement).getInstallment() != null){
            Installment installment = ((StatementElement) statementElement).getInstallment();

            if(installment.isUpdated()){
                holder.saved.setVisibility(View.VISIBLE);
                holder.notSaved.setVisibility(View.GONE);
            }else {
                holder.saved.setVisibility(View.GONE);
                holder.notSaved.setVisibility(View.VISIBLE);
            }



            holder.date.setText(getDate3LettersMonthFormat(installment.getDate()));
      //      holder.categoryText.setText(installment.getTransaction().getCategoryText(context));
            holder.installmentAccount.setText(" (" + installment.getNumber() + "/" +
                    installment.getTransaction().getTotalinstallments() + ")");

            if(installment.isIncome()){
                if(installment.isPaid()){
                    holder.value.setText("+ R$ " + newFormat.format(installment.getPayment()));

                }else{
                    holder.value.setText("+ R$ " + newFormat.format(installment.getValue()));
                }
                holder.moneyBagPlus.setVisibility(View.VISIBLE);
                holder.moneyBagMinus.setVisibility(View.GONE);
                holder.value.setTextColor(activity.getResources().getColor(R.color.income_green));
            }else{
                if(installment.isPaid()){
                    holder.value.setText("- R$ " + newFormat.format(installment.getPayment()));
                }else{
                    holder.value.setText("- R$ " + newFormat.format(installment.getValue()));
                }
                holder.moneyBagPlus.setVisibility(View.GONE);
                holder.moneyBagMinus.setVisibility(View.VISIBLE);
                holder.value.setTextColor(activity.getResources().getColor(R.color.outgo_red));
            }
            if(!installment.isPaid()){
                holder.installmentAccount.setTextColor(activity.getResources().getColor(R.color.filter_yellow));
            }
/*
            if(installment.isPaid()){
                holder.paidOrNotText.setText(context.getResources().getString(R.string.transaction_paid_text));
                holder.paidOrNotText.setTextColor(context.getResources().getColor(R.color.transaction_income_value_green));
            }else{
                holder.paidOrNotText.setText(context.getResources().getString(R.string.transaction_tending_text));
                holder.paidOrNotText.setTextColor(context.getResources().getColor(R.color.transaction_pendent_payment));
            }
            */
        }
        final StatementElement statementElementfinal =  statementElement;

        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statementElementfinal.getTransaction() != null){
                    DialogStatementDetails dialogStatementDetails = new DialogStatementDetails(activity, statementElementfinal.getTransaction(), null);
                    dialogStatementDetails.show();
                }else if(statementElementfinal.getInstallment() != null){
                    DialogStatementDetails dialogStatementDetails = new DialogStatementDetails(activity,null , statementElementfinal.getInstallment());
                    dialogStatementDetails.show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return statementElements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moneyBagPlus, moneyBagMinus, saved, notSaved;
        TextView date, installmentAccount, value;
        LinearLayout detailsButton;
        ViewHolder(View itemView) {
            super(itemView);

            moneyBagPlus = itemView.findViewById(R.id.adapter_statement_content_moneybag_plus);
            moneyBagMinus = itemView.findViewById(R.id.adapter_statement_content_moneybag_minus);
            saved = itemView.findViewById(R.id.adapter_statement_content_saved);
            notSaved = itemView.findViewById(R.id.adapter_statement_content_not_saved);
            date = itemView.findViewById(R.id.adapter_statement_content_date);
            installmentAccount = itemView.findViewById(R.id.adapter_statement_content_installment);
            value = itemView.findViewById(R.id.adapter_statement_content_value);
            detailsButton = itemView.findViewById(R.id.adapter_statement_content_details);
        //    itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Object getItem(int id) {
        return statementElements.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}