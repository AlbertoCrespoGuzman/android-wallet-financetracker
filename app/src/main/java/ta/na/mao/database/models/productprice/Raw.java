package ta.na.mao.database.models.productprice;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DecimalFormat;

import androidx.databinding.InverseMethod;

@DatabaseTable
public class Raw{

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;
    @DatabaseField
    long id;
    @DatabaseField
    boolean updated;
    @DatabaseField
    long productpriceid;
    @DatabaseField
    transient long productpricelocalid;

    @DatabaseField
    String description;
    @DatabaseField
    int quantity;
    @DatabaseField
    double unitcost;
    @DatabaseField
    double totalcost;

    public Raw(){}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getProductpriceid() {
        return productpriceid;
    }

    public void setProductpriceid(long productpriceid) {
        this.productpriceid = productpriceid;
    }

    public long getProductpricelocalid() {
        return productpricelocalid;
    }

    public void setProductpricelocalid(long productpricelocalid) {
        this.productpricelocalid = productpricelocalid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitcost() {
        return unitcost;
    }

    public void setUnitcost(double unitcost) {
        this.unitcost = unitcost;
    }

    public double getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(double totalcost) {
        this.totalcost = totalcost;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
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

    public String fromDoubletoString(double value) {

        DecimalFormat newFormat = new DecimalFormat("##0.00");

        return newFormat.format(value);
    }

    @Override
    public String toString() {
        return "Raw{" +
                "local_id=" + local_id +
                ", id=" + id +
                ", updated=" + updated +
                ", productpriceid=" + productpriceid +
                ", productpricelocalid=" + productpricelocalid +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitcost=" + unitcost +
                ", totalcost=" + totalcost +
                '}';
    }
}
