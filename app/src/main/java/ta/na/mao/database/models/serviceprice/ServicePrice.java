package ta.na.mao.database.models.serviceprice;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.InverseMethod;
import ta.na.mao.BR;
import ta.na.mao.database.manager.DatabaseManager;


@DatabaseTable
public class ServicePrice  extends BaseObservable {

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;

    @DatabaseField
    long id;
    @DatabaseField
    long userid;
    @DatabaseField
    String name;
    @DatabaseField
    double saleprice;
    List<LabourCost> labourcosts = new ArrayList<>();
    @DatabaseField
    double totallabourcost;
    List<LabourTax> labourtaxs = new ArrayList<>();
    @DatabaseField
    double totallabourtax;

    List<VariableCost> variablecosts = new ArrayList<>();
    @DatabaseField
    double totalvariablecost;

    List<FixedCost> fixedcosts = new ArrayList<>();
    @DatabaseField
    double totalfixedcost;

    @DatabaseField
    double totalcost;

    @DatabaseField
    double profit;

    @DatabaseField
    double profitmargin;

    @DatabaseField
    boolean updated;

    @DatabaseField
    boolean finished;

    public ServicePrice(){

    }

    public void setSaleprice(double saleprice) {
        this.saleprice = saleprice;
    }

    public void setTotallabourcost(double totallabourcost) {
        this.totallabourcost = totallabourcost;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public void setTotallabourtax(double totallabourtax) {
        this.totallabourtax = totallabourtax;
    }

    public void setTotalvariablecost(double totalvariablecost) {
        this.totalvariablecost = totalvariablecost;
    }

    public void setTotalfixedcost(double totalfixedcost) {
        this.totalfixedcost = totalfixedcost;
    }

    public void setTotalcost(double totalcost) {
        this.totalcost = totalcost;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void setProfitmargin(double profitmargin) {
        this.profitmargin = profitmargin;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public long getLocal_id() {
        return local_id;
    }

    public void setLocal_id(long local_id) {
        this.local_id = local_id;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSaleprice() {
        return saleprice;
    }

    public List<LabourCost> getLabourcosts() {
        return labourcosts;
    }

    public void setLabourcosts(List<LabourCost> labourcosts) {
        this.labourcosts = labourcosts;
    }

    public Double getTotallabourcost() {
        return totallabourcost;
    }

    public void setTotallabourcost(Double totallabourcost) {
        this.totallabourcost = totallabourcost;
    }

    public List<LabourTax> getLabourtaxs() {
        return labourtaxs;
    }

    public void setLabourtaxs(List<LabourTax> labourtaxs) {
        this.labourtaxs = labourtaxs;
    }

    public Double getTotallabourtax() {
        return totallabourtax;
    }

    public void setTotallabourtax(Double totallabourtax) {
        this.totallabourtax = totallabourtax;
    }

    public List<VariableCost> getVariablecosts() {
        return variablecosts;
    }

    public void setVariablecosts(List<VariableCost> variablecosts) {
        this.variablecosts = variablecosts;
    }

    public Double getTotalvariablecost() {
        return totalvariablecost;
    }

    public void setTotalvariablecost(Double totalvariablecost) {
        this.totalvariablecost = totalvariablecost;
    }

    public List<FixedCost> getFixedcosts() {
        return fixedcosts;
    }

    public void setFixedcosts(List<FixedCost> fixedcosts) {
        this.fixedcosts = fixedcosts;
    }

    public Double getTotalfixedcost() {
        return totalfixedcost;
    }

    public void setTotalfixedcost(Double totalfixedcost) {
        this.totalfixedcost = totalfixedcost;
    }

    public Double getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(Double totalcost) {
        this.totalcost = totalcost;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getProfitmargin() {
        return profitmargin;
    }

    public void setProfitmargin(Double profitmargin) {
        this.profitmargin = profitmargin;
    }

    @Override
    public String toString() {
        return "ServicePrice{" +
                "local_id=" + local_id +
                ", id=" + id +
                ", userid=" + userid +
                ", name='" + name + '\'' +
                ", saleprice=" + saleprice +
                ", labourcosts=" + labourcosts +
                ", totallabourcost=" + totallabourcost +
                ", labourtaxs=" + labourtaxs +
                ", totallabourtax=" + totallabourtax +
                ", variablecosts=" + variablecosts +
                ", totalvariablecost=" + totalvariablecost +
                ", fixedcosts=" + fixedcosts +
                ", totalfixedcost=" + totalfixedcost +
                ", totalcost=" + totalcost +
                ", profit=" + profit +
                ", profitmargin=" + profitmargin +
                ", updated=" + updated +
                ", finished=" + finished +
                '}';
    }

    @InverseMethod("fromIntegertoString")
    public int fromStringtoInteger(String value) {
        if(value.isEmpty()){
            return 0;
        }else {
            return Integer.parseInt(value);
        }
    }

    public String fromIntegertoString(int value) {
        return String.valueOf(value);
    }


    @InverseMethod("fromDoubletoString")
    public double fromStringtoDouble(String value) {
        if(value.isEmpty()){
            return 0.0;
        }else {
            return Double.parseDouble(value);
        }
    }
    public void updateValues(Context context){
        totallabourcost = 0;
        for(LabourCost labourCost : labourcosts){
            totallabourcost += labourCost.getHours() * labourCost.getRate();
        }

        totallabourtax  = 0;
        for (LabourTax labourTax : labourtaxs){
            labourTax.setValue((labourTax.getPercentage() / 100) * totallabourcost);
            DatabaseManager db = new DatabaseManager(context);
            db.saveLabourTaxServicePriceFromLocal(labourTax);
            totallabourtax += labourTax.getValue();
        }

        totalvariablecost = 0;
        for(VariableCost variableCost : variablecosts){
            variableCost.setValue(saleprice * (variableCost.getPercentage() / 100));
            DatabaseManager db = new DatabaseManager(context);
            db.saveVariableCostServicePriceFromLocal(variableCost);
            totalvariablecost += variableCost.getValue();
        }

        totalfixedcost = 0;
        for(FixedCost fixedCost : fixedcosts){
            totalfixedcost += fixedCost.getValue();
        }

        totalcost = totallabourcost + totallabourtax + totalvariablecost + totalfixedcost;

        profit = saleprice - totalcost;

        if(saleprice != 0){
            profitmargin = ( profit / saleprice ) * 100;
        }else{
            profitmargin = 0;
        }

        Log.e("print", toString());
        notifyPropertyChanged(BR.serviceprice);
    }

    public String fromDoubletoString(double value) {
        DecimalFormat newFormat = new DecimalFormat("#0.00");
        return newFormat.format(value);
    }
    public boolean validation(){
        boolean ok = true;

        if(name == null || name.length() == 0){
            ok = false;
        }if(saleprice == 0){
            ok = false;
        }

        return ok;
    }

}
