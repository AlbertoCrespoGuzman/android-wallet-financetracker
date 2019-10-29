package ta.na.mao.database.models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by alberto on 2017/12/05.
 */

@DatabaseTable
public class User implements Serializable {

    public User(){
    }

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    private long id;
    @DatabaseField
    String username;
    @DatabaseField
    String password;
    @DatabaseField
    String confirmPassword;
    @DatabaseField
    String device_id;
    @DatabaseField
    String device_name;
    @DatabaseField
    transient String token;
    @DatabaseField
    transient boolean logged;
    @DatabaseField
    transient long userid;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getPassword_confirmation() {
        return confirmPassword;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.confirmPassword = password_confirmation;
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

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", device_id='" + device_id + '\'' +
                ", device_name='" + device_name + '\'' +
                ", token='" + token + '\'' +
                ", logged=" + logged +
                ", userid=" + userid +
                '}';
    }
}
