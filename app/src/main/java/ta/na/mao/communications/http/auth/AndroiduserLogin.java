package ta.na.mao.communications.http.auth;

/**
 * Created by alberto on 2017/12/06.
 */

public class AndroiduserLogin {

    String username;
    String password;

    public AndroiduserLogin(){
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AndroiduserLogin{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
