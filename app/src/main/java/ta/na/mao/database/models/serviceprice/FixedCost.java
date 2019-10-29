package ta.na.mao.database.models.serviceprice;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DecimalFormat;

import androidx.databinding.InverseMethod;

@DatabaseTable
public class FixedCost {
    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;
    @DatabaseField
    long id;
    @DatabaseField
    boolean updated;
    @DatabaseField
    long servicepriceid;
    @DatabaseField
    transient long servicepricelocalid;

    @DatabaseField
    String description;
    @DatabaseField
    double value;

    public FixedCost(){

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

    public long getServicepriceid() {
        return servicepriceid;
    }

    public void setServicepriceid(long servicepriceid) {
        this.servicepriceid = servicepriceid;
    }

    public long getServicepricelocalid() {
        return servicepricelocalid;
    }

    public void setServicepricelocalid(long servicepricelocalid) {
        this.servicepricelocalid = servicepricelocalid;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FixedCost{" +
                "local_id=" + local_id +
                ", id=" + id +
                ", updated=" + updated +
                ", servicepriceid=" + servicepriceid +
                ", servicepricelocalid=" + servicepricelocalid +
                ", description='" + description + '\'' +
                ", value=" + value +
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

    public String fromDoubletoString(double value) {

        DecimalFormat newFormat = new DecimalFormat("##0.00");

        return newFormat.format(value);
    }

}
