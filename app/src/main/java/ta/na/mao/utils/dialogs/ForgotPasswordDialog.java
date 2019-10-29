package ta.na.mao.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ta.na.mao.R;
import ta.na.mao.activities.LoginActivity;
import ta.na.mao.activities.SplashActivity;
import ta.na.mao.communications.Svc;
import ta.na.mao.communications.SvcApi;
import ta.na.mao.communications.http.auth.AndroiduserLogin;
import ta.na.mao.database.manager.DatabaseManager;
import ta.na.mao.database.models.Installment;
import ta.na.mao.database.models.User;
import ta.na.mao.utils.Utils;

public class ForgotPasswordDialog  extends Dialog {

    TextView title;
    TextView message;
    Button acceptButton,  cancelButton;
    Activity a;
    EditText email;
    SvcApi svc, svcAth;
    String pattern;
    DecimalFormat decimalFormat;
    SimpleDateFormat dateFormat;
    ProgressDialog dialog;
    public ForgotPasswordDialog(Activity a){
        super(a);
        this.a = a;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.dialog_forgot_password);
        }else{
            setContentView(R.layout.dialog_forgot_password_old);
        }

        email = findViewById(R.id.dialog_forgot_email_input);
        acceptButton = findViewById(R.id.dialog_forgot_accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter()){
                    postForgotPassword();
                    dismiss();
                }

            }
        });
        cancelButton = findViewById(R.id.dialog_forgot_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });




    }

    private void postForgotPassword(){
        dialog = Utils.showDialog(dialog, getContext(), R.string.loading);
        svc = Svc.initRegisterLogin();

        final String emailString = email.getText().toString();
        User user = new User();
        user.setUsername(emailString);
        Call<Void> call = svc.postForgotPassword(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final Response responsefinal =  response;

                dialog = Utils.hideDialog(dialog);
                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    getContext().getResources().getString(R.string.recover_password_email_sent),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), R.string.email_error, Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dialog = Utils.hideDialog(dialog);
                t.printStackTrace();
                Toast.makeText(getContext(), R.string.failure_connection, Toast.LENGTH_SHORT).show();

            }
        });
    }
    private boolean filter(){
        boolean ok = false;

        if(email != null && email.getText().length() > 4 && email.getText().toString().contains("@")
        && email.getText().toString().contains(".")){
            ok = true;
        }
        else{
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.email_error),
                    Toast.LENGTH_SHORT).show();
        }
        return ok;
    }
}