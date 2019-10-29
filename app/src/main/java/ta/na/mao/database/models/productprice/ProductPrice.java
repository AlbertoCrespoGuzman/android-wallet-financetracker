package ta.na.mao.database.models.productprice;

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
import ta.na.mao.database.models.serviceprice.FixedCost;
import ta.na.mao.database.models.serviceprice.LabourCost;
import ta.na.mao.database.models.serviceprice.LabourTax;
import ta.na.mao.database.models.serviceprice.VariableCost;

@DatabaseTable
public class ProductPrice extends BaseObservable {

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;

    @DatabaseField
    long userid;

    @DatabaseField
    long id;
    @DatabaseField
    String name;
    @DatabaseField
    double unitsaleprice;
    @DatabaseField
    int quantity;
    List<Raw> raws = new ArrayList<>();
    @DatabaseField
    double unitrawcost;
    @DatabaseField
    double totalrawcost;

    List<FixedVariableCost> fixedvariablecosts = new ArrayList<>();
    @DatabaseField
    double unitfixedvariablecost;
    @DatabaseField
    double totalfixedvariablecost;
    @DatabaseField
    double fixedvariablepercentage;


    @DatabaseField
    double unitcost;
    @DatabaseField
    double unitprofit;
    @DatabaseField
    double totalcost;
    @DatabaseField
    double totalsaleprice;
    @DatabaseField
    double totalprofit;

    @DatabaseField
    double totalprofitmargin;

    @DatabaseField
    boolean updated;

    @DatabaseField
    boolean finished;

    public ProductPrice(){

    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getLocal_id() {
        return local_id;
    }

    public void setLocal_id(long local_id) {
        this.local_id = local_id;
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

    public double getUnitsaleprice() {
        return unitsaleprice;
    }

    public void setUnitsaleprice(double unitsaleprice) {
        this.unitsaleprice = unitsaleprice;
    }

    public List<Raw> getRaws() {
        return raws;
    }

    public void setRaws(List<Raw> raws) {
        this.raws = raws;
    }

    public double getUnitrawcost() {
        return unitrawcost;
    }

    public void setUnitrawcost(double unitrawcost) {
        this.unitrawcost = unitrawcost;
    }

    public double getTotalrawcost() {
        return totalrawcost;
    }

    public void setTotalrawcost(double totalrawcost) {
        this.totalrawcost = totalrawcost;
    }

    public List<FixedVariableCost> getFixedvariablecosts() {
        return fixedvariablecosts;
    }

    public void setFixedvariablecosts(List<FixedVariableCost> fixedvariablecosts) {
        this.fixedvariablecosts = fixedvariablecosts;
    }

    public double getUnitfixedvariablecost() {
        return unitfixedvariablecost;
    }

    public void setUnitfixedvariablecost(double unitfixedvariablecost) {
        this.unitfixedvariablecost = unitfixedvariablecost;
    }

    public double getTotalfixedvariablecost() {
        return totalfixedvariablecost;
    }

    public void setTotalfixedvariablecost(double totalfixedvariablecost) {
        this.totalfixedvariablecost = totalfixedvariablecost;
    }

    public double getFixedvariablepercentage() {
        return fixedvariablepercentage;
    }

    public void setFixedvariablepercentage(double fixedvariablepercentage) {
        this.fixedvariablepercentage = fixedvariablepercentage;
    }

    public double getUnitcost() {
        return unitcost;
    }

    public void setUnitcost(double unitcost) {
        this.unitcost = unitcost;
    }

    public double getUnitprofit() {
        return unitprofit;
    }

    public void setUnitprofit(double unitprofit) {
        this.unitprofit = unitprofit;
    }

    public double getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(double totalcost) {
        this.totalcost = totalcost;
    }

    public double getTotalsaleprice() {
        return totalsaleprice;
    }

    public void setTotalsaleprice(double totalsaleprice) {
        this.totalsaleprice = totalsaleprice;
    }

    public double getTotalprofit() {
        return totalprofit;
    }

    public void setTotalprofit(double totalprofit) {
        this.totalprofit = totalprofit;
    }

    public double getTotalprofitmargin() {
        return totalprofitmargin;
    }

    public void setTotalprofitmargin(double totalprofitmargin) {
        this.totalprofitmargin = totalprofitmargin;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ProductPrice{" +
                "local_id=" + local_id +
                ", userid=" + userid +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", unitsaleprice=" + unitsaleprice +
                ", quantity=" + quantity +
                ", raws=" + raws +
                ", unitrawcost=" + unitrawcost +
                ", totalrawcost=" + totalrawcost +
                ", fixedvariablecosts=" + fixedvariablecosts +
                ", unitfixedvariablecost=" + unitfixedvariablecost +
                ", totalfixedvariablecost=" + totalfixedvariablecost +
                ", fixedvariablepercentage=" + fixedvariablepercentage +
                ", unitcost=" + unitcost +
                ", unitprofit=" + unitprofit +
                ", totalcost=" + totalcost +
                ", totalsaleprice=" + totalsaleprice +
                ", totalprofit=" + totalprofit +
                ", totalprofitmargin=" + totalprofitmargin +
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

        Log.e("print", toString());
        unitrawcost = 0;
        for(Raw raw : getRaws()){
            unitrawcost += raw.getQuantity() * raw.getUnitcost();
        }
        totalrawcost = unitrawcost * getQuantity();

        unitfixedvariablecost = 0;
        for(FixedVariableCost fixedVariableCost : getFixedvariablecosts()){
            fixedvariablepercentage += fixedVariableCost.getPercentage();
            unitfixedvariablecost += (fixedVariableCost.getPercentage() / 100 ) * getUnitsaleprice();
            fixedVariableCost.setValue(unitfixedvariablecost);
            DatabaseManager db = new DatabaseManager(context);
            db.saveFixedVariableCostProductPriceFromLocal(fixedVariableCost);
        }
        totalfixedvariablecost = unitfixedvariablecost * getQuantity();

        unitcost = unitrawcost + unitfixedvariablecost;

        unitprofit = unitsaleprice - unitcost;

        totalsaleprice = unitsaleprice * getQuantity();

        totalcost = unitcost * getQuantity();

        totalprofit = totalsaleprice - totalcost;

        if(totalsaleprice != 0){
            totalprofitmargin = ( totalprofit / totalsaleprice ) * 100;
        }else{
            totalprofitmargin = 0;
        }



        notifyPropertyChanged(BR.productprice);

    }

    public String fromDoubletoString(double value) {
        DecimalFormat newFormat = new DecimalFormat("#0.00");
        return newFormat.format(value);
    }
    public boolean validation(){
        boolean ok = true;

        if(name == null || name.length() == 0){
            ok = false;
        }

        return ok;
    }

}
