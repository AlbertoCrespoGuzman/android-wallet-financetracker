package ta.na.mao.utils;

import android.view.View;

public class ErrorMessage {

    String message;
    View view;


    public ErrorMessage(){

    }
    public ErrorMessage(String message, View view){
        this.message = message;
        this.view = view;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "message='" + message + '\'' +
                ", view=" + view +
                '}';
    }
}
