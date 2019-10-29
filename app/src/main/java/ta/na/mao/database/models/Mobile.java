package ta.na.mao.database.models;


import java.io.Serializable;

public class Mobile implements Serializable {

    String code = "+55";
    String state;
    String number;

    public Mobile(){
        this.code = "+55";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumberString(){
        if(number != null && number.length() == 9) {
            return code + "(" + state + ") " + number.substring(5) + "-" + number.substring(5, number.length());
        }else{
            return null;
        }
    }

    @Override
    public String toString() {
        return "Mobile{" +
                "code='" + code + '\'' +
                ", state='" + state + '\'' +
                ", number=" + number +
                '}';
    }
}
