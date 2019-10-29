package ta.na.mao.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import ta.na.mao.R;
import ta.na.mao.activities.FinancialControlMainActivity;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.Transaction;
import ta.na.mao.utils.adapters.TransactionInstallmentsDetailsAdapter;

public class DialogStatementDetails extends Dialog {

    Activity a;
    Transaction transaction;
    Installment installment;
    String pattern;
    DecimalFormat decimalFormat;
    SimpleDateFormat dateFormat;
    ImageView moneybagPlus, moneybagMinus, closeButton;
    TextView dateTitle, categoryText, totalValueText;
    RecyclerView installmentsRecycler;
    RelativeLayout editButton, removeButton;
    MaterialDialog removeDialog;

    public DialogStatementDetails(Activity a, Transaction transaction, Installment installment){
        super(a);
        this.a = a;
        this.transaction = transaction;
        this.installment = installment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(
                R.layout.dialog_statement_details);

        pattern = "'R$' ###,###,##0.00";
        decimalFormat = new DecimalFormat(pattern);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        moneybagPlus = findViewById(R.id.dialog_statement_details_moneybag_plus);
        moneybagMinus = findViewById(R.id.dialog_statement_details_moneybag_minus);
        closeButton = findViewById(R.id.dialog_statement_details_close);
        dateTitle = findViewById(R.id.dialog_statement_details_date_title);
        categoryText = findViewById(R.id.dialog_statement_details_category);
        installmentsRecycler = findViewById(R.id.dialog_statement_details_recycler);
        totalValueText = findViewById(R.id.dialog_statement_details_total_value);
        editButton = findViewById(R.id.dialog_statement_details_edit);
        removeButton = findViewById(R.id.dialog_statement_details_remove);


        DatabaseManager db = new DatabaseManager(getContext());
        if(transaction != null){
            TransactionInstallmentsDetailsAdapter adapter = new TransactionInstallmentsDetailsAdapter(getContext(), db.findTransactionByLocalid(transaction.getLocal_id()), 0);
            installmentsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            installmentsRecycler.setAdapter(adapter);
        }else{
            TransactionInstallmentsDetailsAdapter adapter = new TransactionInstallmentsDetailsAdapter(getContext(), db.findTransactionByLocalid(installment.getTransactionlocalid()), installment.getNumber());
            installmentsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            installmentsRecycler.setAdapter(adapter);
        }




        if(transaction != null){
            if(transaction.isIncome()){
                moneybagPlus.setVisibility(View.VISIBLE);
                moneybagMinus.setVisibility(View.GONE);
            }else{
                moneybagMinus.setVisibility(View.VISIBLE);
                moneybagPlus.setVisibility(View.GONE);
            }
            dateTitle.setText(getDateTitle(transaction.getDate()));
            categoryText.setText(transaction.getCategoryText(getContext())
            + (transaction.isInstallment() ? " (em " + transaction.getTotalinstallments() + "X)" : ""));
        }else{
            if(installment.isIncome()){
                moneybagPlus.setVisibility(View.VISIBLE);
                moneybagMinus.setVisibility(View.GONE);
            }else{
                moneybagMinus.setVisibility(View.VISIBLE);
                moneybagPlus.setVisibility(View.GONE);
            }
            dateTitle.setText(getDateTitle(installment.getDate()));
            categoryText.setText(installment.getTransaction().getCategoryText(getContext())
            + " (em " + installment.getTransaction().getTotalinstallments() + "X)");
        }
        if(transaction != null){
            totalValueText.setText("Total: " + decimalFormat.format(transaction.getValue()));
        }else{
            totalValueText.setText("Total: " + decimalFormat.format(installment.getTransaction().getValue()));
        }

        final Transaction transactionFinal = transaction;
        final Installment installmentFinal = installment;

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a instanceof FinancialControlMainActivity){
                    if(transactionFinal != null){
                        ((FinancialControlMainActivity) a).changeToFragmentIncomeOutGoForm(transactionFinal.isIncome(), transactionFinal.getLocal_id());
                    }else{
                        ((FinancialControlMainActivity) a).changeToFragmentIncomeOutGoForm(installmentFinal.isIncome(), installmentFinal.getTransactionlocalid());
                    }
                    dismiss();
                }
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDialog = new MaterialDialog(a);

                removeDialog.message(R.string.remove_transaction_dialog_message, a.getResources().getString(R.string.remove_transaction_dialog_message));
                removeDialog.positiveButton(R.string.logout_positive_button,
                        a.getResources().getString(R.string.accept_button), new Function1<MaterialDialog, Unit>() {
                            @Override
                            public Unit invoke(MaterialDialog materialDialog) {
                                DatabaseManager db = new DatabaseManager(a);
                                if(transactionFinal != null){
                                    db.removeTransaction(transactionFinal);
                                }else{
                                    db.removeTransaction(installmentFinal.getTransaction());
                                }
                                dismiss();
                                return null;
                            }
                        });
                removeDialog.negativeButton(R.string.logout_negative_button,
                        a.getResources().getString(R.string.logout_negative_button), null);
                removeDialog.show();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public String getDateTitle(Date date){
        String month = "";

        Locale localeBR = new Locale("pt", "BR");

        SimpleDateFormat fmt = new SimpleDateFormat("dd 'de' MMMM", localeBR);

        month += fmt.format(date);

        return month;

    }
}