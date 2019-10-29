package ta.na.mao.utils;


import ta.na.mao.R;

/**
 * Created by alberto on 2017/12/05.
 */

public class Defaultdata {
     public static final String DEFAULT_IP ="xxxxxxxx";
    public static final String DEFAULT_PORT ="8080";
   public static final String MY_CONNECTIVITY_CHANGE = "MY_CONNECTIVITY_CHANGE";
    public static final int GOAL_REACHED_NOTIFICATION = 1;
    public static final int GOAL_PREVIEW_REACHED_NOTIFICATION = 2;
    public static final String BACK_BUTTON_ENABLED = "BACK_BUTTON_ENABLED";

    public static  enum Blocker {TRANSACTION, INSTALLMENT, GOAL, TASK, USERDETAILS, FORCE_UNBLOCK,
        SERVICEPRICE, FIXEDCOST_SERVICEPRICE,LABOURCOST_SEVICEPRICE, LABOURTAX_SERVICEPRICE,
        VARIABLECOST_SERVICEPRICE, PRODUCTPRICE, RAW_PRODUCTPRICE,FIXEDVARIABLECOST_PRODUCTPRICE};
    public static  enum DataChecker {TRANSACTION, GOAL, USERDETAILS};
    public static final String MAINACTIVITY_CHANGETO_GOALS = "changeToGoals";

    public enum TopBarTitle{INCOME_OUTGO, STATEMENT, STATS, REPORT, GOALS}
}
