package ta.na.mao.database.models;

import ta.na.mao.utils.Utils;

public class DatePager {

    int month;
    int year;
    String name;

    public DatePager(){}

    public DatePager(int month, int year){
        this.month = month;
        this.year = year;
        name = Utils.getDate3LettersMonthAndYear(month, year);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
