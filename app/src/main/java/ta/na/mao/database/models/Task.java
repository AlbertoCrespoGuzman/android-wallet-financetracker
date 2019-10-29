package ta.na.mao.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Task {
    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long id;
    @DatabaseField
    long userid;
    @DatabaseField
    boolean updated;
    @DatabaseField
    boolean delete;

    @DatabaseField
    boolean transaction;
    @DatabaseField
    boolean installment;
    @DatabaseField
    boolean goal;
    @DatabaseField
    boolean serviceprice;
    @DatabaseField
    boolean fixedcostServicePrice;
    @DatabaseField
    boolean labourcostServicePrice;
    @DatabaseField
    boolean labourtaxServicePrice;
    @DatabaseField
    boolean variablecostServicePrice;
    @DatabaseField
    boolean productprice;
    @DatabaseField
    boolean rawProductPrice;
    @DatabaseField
    boolean fixedVariableCostProductPrice;


    @DatabaseField
    long transaction_id;
    @DatabaseField
    long installment_id;
    @DatabaseField
    long goal_id;
    @DatabaseField
    long serviceprice_id;
    @DatabaseField
    long fixedcost_id;
    @DatabaseField
    long labourcost_id;
    @DatabaseField
    long labourtax_id;
    @DatabaseField
    long variablecost_id;
    @DatabaseField
    long productprice_id;
    @DatabaseField
    long rawproductprice_id;
    @DatabaseField
    long fixedvariablecostproductprice_id;


    public Task(){

    }

    public Task(long userid){
        this.userid = userid;
        this.updated = false;
        this.delete = false;
        this.transaction = false;
        this.installment = false;
        this.goal = false;
        this.serviceprice = false;
        this.fixedcostServicePrice = false;
        this.labourcostServicePrice = false;
        this.labourtaxServicePrice = false;
        this.variablecostServicePrice = false;

    }

    public boolean isProductprice() {
        return productprice;
    }

    public void setProductprice(boolean productprice) {
        this.productprice = productprice;
    }

    public boolean isRawProductPrice() {
        return rawProductPrice;
    }

    public void setRawProductPrice(boolean rawProductPrice) {
        this.rawProductPrice = rawProductPrice;
    }

    public boolean isFixedVariableCostProductPrice() {
        return fixedVariableCostProductPrice;
    }

    public void setFixedVariableCostProductPrice(boolean fixedVariableCostProductPrice) {
        this.fixedVariableCostProductPrice = fixedVariableCostProductPrice;
    }

    public long getProductprice_id() {
        return productprice_id;
    }

    public void setProductprice_id(long productprice_id) {
        this.productprice_id = productprice_id;
    }

    public long getRawproductprice_id() {
        return rawproductprice_id;
    }

    public void setRawproductprice_id(long rawproductprice_id) {
        this.rawproductprice_id = rawproductprice_id;
    }

    public long getFixedvariablecostproductprice_id() {
        return fixedvariablecostproductprice_id;
    }

    public void setFixedvariablecostproductprice_id(long fixedvariablecostproductprice_id) {
        this.fixedvariablecostproductprice_id = fixedvariablecostproductprice_id;
    }

    public boolean isServiceprice() {
        return serviceprice;
    }

    public void setServiceprice(boolean serviceprice) {
        this.serviceprice = serviceprice;
    }

    public boolean isFixedcostServicePrice() {
        return fixedcostServicePrice;
    }

    public void setFixedcostServicePrice(boolean fixedcostServicePrice) {
        this.fixedcostServicePrice = fixedcostServicePrice;
    }

    public boolean isLabourcostServicePrice() {
        return labourcostServicePrice;
    }

    public void setLabourcostServicePrice(boolean labourcostServicePrice) {
        this.labourcostServicePrice = labourcostServicePrice;
    }

    public boolean isLabourtaxServicePrice() {
        return labourtaxServicePrice;
    }

    public void setLabourtaxServicePrice(boolean labourtaxServicePrice) {
        this.labourtaxServicePrice = labourtaxServicePrice;
    }

    public boolean isVariablecostServicePrice() {
        return variablecostServicePrice;
    }

    public void setVariablecostServicePrice(boolean variablecostServicePrice) {
        this.variablecostServicePrice = variablecostServicePrice;
    }

    public long getFixedcost_id() {
        return fixedcost_id;
    }

    public void setFixedcost_id(long fixedcost_id) {
        this.fixedcost_id = fixedcost_id;
    }

    public long getLabourcost_id() {
        return labourcost_id;
    }

    public void setLabourcost_id(long labourcost_id) {
        this.labourcost_id = labourcost_id;
    }

    public long getLabourtax_id() {
        return labourtax_id;
    }

    public void setLabourtax_id(long labourtax_id) {
        this.labourtax_id = labourtax_id;
    }

    public long getVariablecost_id() {
        return variablecost_id;
    }

    public void setVariablecost_id(long variablecost_id) {
        this.variablecost_id = variablecost_id;
    }

    public long getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(long transaction_id) {
        this.transaction_id = transaction_id;
    }

    public long getInstallment_id() {
        return installment_id;
    }

    public void setInstallment_id(long installment_id) {
        this.installment_id = installment_id;
    }

    public long getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(long goal_id) {
        this.goal_id = goal_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
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

    public long getServiceprice_id() {
        return serviceprice_id;
    }

    public void setServiceprice_id(long serviceprice_id) {
        this.serviceprice_id = serviceprice_id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", userid=" + userid +
                ", updated=" + updated +
                ", delete=" + delete +
                ", transaction=" + transaction +
                ", installment=" + installment +
                ", goal=" + goal +
                ", serviceprice=" + serviceprice +
                ", fixedcostServicePrice=" + fixedcostServicePrice +
                ", labourcostServicePrice=" + labourcostServicePrice +
                ", labourtaxServicePrice=" + labourtaxServicePrice +
                ", variablecostServicePrice=" + variablecostServicePrice +
                ", productprice=" + productprice +
                ", rawProductPrice=" + rawProductPrice +
                ", fixedVariableCostProductPrice=" + fixedVariableCostProductPrice +
                ", transaction_id=" + transaction_id +
                ", installment_id=" + installment_id +
                ", goal_id=" + goal_id +
                ", serviceprice_id=" + serviceprice_id +
                ", fixedcost_id=" + fixedcost_id +
                ", labourcost_id=" + labourcost_id +
                ", labourtax_id=" + labourtax_id +
                ", variablecost_id=" + variablecost_id +
                ", productprice_id=" + productprice_id +
                ", rawproductprice_id=" + rawproductprice_id +
                ", fixedvariablecostproductprice_id=" + fixedvariablecostproductprice_id +
                '}';
    }
}
