package ta.na.mao.database.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable

public class Blocker {
    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    private long id;

    @DatabaseField
    private boolean blocked = false;

    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    private Date last_update;

    @DatabaseField
    boolean userdetails = false;
    @DatabaseField
    boolean transaction = false;
    @DatabaseField
    boolean installment = false;
    @DatabaseField
    boolean goal = false;
    @DatabaseField
    boolean task = false;

    @DatabaseField
    boolean serviceprice= false;
    @DatabaseField
    boolean fixedcostServiceprice = false;
    @DatabaseField
    boolean labourcostServiceprice = false;
    @DatabaseField
    boolean labourtaxServiceprice = false;
    @DatabaseField
    boolean variablecostServiceprice = false;

    @DatabaseField
    boolean productprice = false;
    @DatabaseField
    boolean fixedvariablecostProductPrice = false;
    @DatabaseField
    boolean rawProductPrice = false;




    public Blocker(){
        this.blocked = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isProductprice() {
        return productprice;
    }

    public void setProductprice(boolean productprice) {
        this.productprice = productprice;
    }

    public boolean isFixedvariablecostProductPrice() {
        return fixedvariablecostProductPrice;
    }

    public void setFixedvariablecostProductPrice(boolean fixedvariablecostProductPrice) {
        this.fixedvariablecostProductPrice = fixedvariablecostProductPrice;
    }

    public boolean isRawProductPrice() {
        return rawProductPrice;
    }

    public void setRawProductPrice(boolean rawProductPrice) {
        this.rawProductPrice = rawProductPrice;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isUserdetails() {
        return userdetails;
    }

    public void setUserdetails(boolean userdetails) {
        this.userdetails = userdetails;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Date getLast_update() {
        return last_update;
    }

    public void setLast_update(Date last_update) {
        this.last_update = last_update;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    public boolean isInstallment() {
        return installment;
    }

    public void setInstallment(boolean installment) {
        this.installment = installment;
    }

    public boolean isGoal() {
        return goal;
    }

    public void setGoal(boolean goal) {
        this.goal = goal;
    }

    public boolean isTask() {
        return task;
    }

    public void setTask(boolean task) {
        this.task = task;
    }

    public boolean isServiceprice() {
        return serviceprice;
    }

    public void setServiceprice(boolean serviceprice) {
        this.serviceprice = serviceprice;
    }

    public boolean isFixedcostServiceprice() {
        return fixedcostServiceprice;
    }

    public void setFixedcostServiceprice(boolean fixedcostServiceprice) {
        this.fixedcostServiceprice = fixedcostServiceprice;
    }

    public boolean isLabourcostServiceprice() {
        return labourcostServiceprice;
    }

    public void setLabourcostServiceprice(boolean labourcostServiceprice) {
        this.labourcostServiceprice = labourcostServiceprice;
    }

    public boolean isLabourtaxServiceprice() {
        return labourtaxServiceprice;
    }

    public void setLabourtaxServiceprice(boolean labourtaxServiceprice) {
        this.labourtaxServiceprice = labourtaxServiceprice;
    }

    public boolean isVariablecostServiceprice() {
        return variablecostServiceprice;
    }

    public void setVariablecostServiceprice(boolean variablecostServiceprice) {
        this.variablecostServiceprice = variablecostServiceprice;
    }

    @Override
    public String toString() {
        return "Blocker{" +
                "id=" + id +
                ", blocked=" + blocked +
                ", last_update=" + last_update +
                ", userdetails=" + userdetails +
                ", transaction=" + transaction +
                ", installment=" + installment +
                ", goal=" + goal +
                ", task=" + task +
                ", serviceprice=" + serviceprice +
                ", fixedcostServiceprice=" + fixedcostServiceprice +
                ", labourcostServiceprice=" + labourcostServiceprice +
                ", labourtaxServiceprice=" + labourtaxServiceprice +
                ", variablecostServiceprice=" + variablecostServiceprice +
                ", productprice=" + productprice +
                ", fixedvariablecostProductPrice=" + fixedvariablecostProductPrice +
                ", rawProductPrice=" + rawProductPrice +
                '}';
    }
}
