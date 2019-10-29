package ta.na.mao.communications.http.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alberto on 2017/12/05.
 */

public class ResponseArrayMessages {

    List<ResponseMessage> messages = new ArrayList<>();

    public ResponseArrayMessages(){

    }

    public List<ResponseMessage> getErrors() {
        return messages;
    }

    public void setErrors(List<ResponseMessage> errors) {
        this.messages = errors;
    }

    @Override
    public String toString() {
        return "ResponseArrayMessages{" +
                "messages=" + messages +
                '}';
    }
}
