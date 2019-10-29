package ta.na.mao.communications.http.auth;

/**
 * Created by alberto on 2017/12/06.
 */

public class AndroiduserLoginResponse {
    int code;
    String message;
    String email;
    String token;
    ResponseArrayMessages errors;

    public AndroiduserLoginResponse(){
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ResponseArrayMessages getErrors() {
        return errors;
    }

    public void setErrors(ResponseArrayMessages errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "AndroiduserLoginResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", errors=" + errors +
                '}';
    }
}
