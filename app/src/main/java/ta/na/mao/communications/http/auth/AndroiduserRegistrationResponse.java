package ta.na.mao.communications.http.auth;

/**
 * Created by alberto on 2017/12/05.
 */

public class AndroiduserRegistrationResponse {

    int code;
    String message;
    String first_name;
    String last_name;
    String phone_number;
    String email;
    String token;

    ResponseArrayMessages errors;

    public AndroiduserRegistrationResponse(){
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

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
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
        return "AndroiduserRegistrationResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", errors=" + errors +
                '}';
    }
}
