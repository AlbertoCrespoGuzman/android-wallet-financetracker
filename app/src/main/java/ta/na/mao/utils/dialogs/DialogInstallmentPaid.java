package ta.na.mao.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import ta.na.mao.R;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Installment;

public class DialogInstallmentPaid extends Dialog {

    TextView title;
    TextView message;
    Button acceptButton,  cancelButton;
    Activity a;
    String pattern;
    DecimalFormat decimalFormat;
    Installment installment;
    SimpleDateFormat dateFormat;

    public DialogInstallmentPaid(Activity a, Installment installment){
        super(a);
        this.a = a;
        this.installment = installment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_installment_paid);

        pattern = "'R$' ###,###,##0.00";
        decimalFormat = new DecimalFormat(pattern);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        title = findViewById(R.id.dialog_installment_paid_title);
        message = findViewById(R.id.dialog_installment_paid_text);
        acceptButton = findViewById(R.id.dialog_installment_paid_accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInstallmentPaid();
                dismiss();
            }
        });
        cancelButton = findViewById(R.id.dialog_installment_paid_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        message.setText("A parcela de " + (installment.getTransaction().isIncome() ? a.getResources().getString(R.string.income_text) :
        a.getResources().getString(R.string.outgo_text))+ " número " + installment.getNumber() +
                " da categoria "+ installment.getTransaction().getCategoryText(a) + " com valor de " + decimalFormat.format(installment.getValue()) +
                " com data de pagamento no dia " + dateFormat.format(installment.getDate()) + " já foi paga?");


    }
    void saveInstallmentPaid(){
        DatabaseManager db = new DatabaseManager(a);
        installment.setPaid(true);
        installment.setPayment(installment.getValue());
        db.saveInstallmentFromLocalUpdatedNow(installment, true);


    }

}