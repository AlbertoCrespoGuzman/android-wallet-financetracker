package ta.na.mao.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import ta.na.mao.R;

public class ProgressCustomDialog extends Dialog {

    Activity a;
    String title;
    String text;
    TextView titleTV, textTV;


    public ProgressCustomDialog(Activity a, String title, String text) {
        super(a);
        this.a = a;
        this.title = title;
        this.text = text;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(
                R.layout.dialog_progress);

       titleTV = findViewById(R.id.dialog_progress_title);
       textTV = findViewById(R.id.dialog_progress_text);

       titleTV.setText(title);
       textTV.setText(text);




    }
}