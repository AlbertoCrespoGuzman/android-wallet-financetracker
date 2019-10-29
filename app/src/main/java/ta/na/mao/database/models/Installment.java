package ta.na.mao.database.models;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable
public class Installment {

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;
    @DatabaseField
    long id;
    @DatabaseField
    long userid;
    @DatabaseField
    boolean updated;
    @DatabaseField
    long transactionid;
    @DatabaseField
    transient long transactionlocalid;
    @DatabaseField
    boolean income;
    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date date;
    @DatabaseField
    boolean installment;
    @DatabaseField
    double value;
    @DatabaseField
    double payment;
    @DatabaseField
    boolean paid;
    @DatabaseField
    int number;


    Transaction transaction;


    public Installment(){

    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTransactionlocalid() {
        return transactionlocalid;
    }

    public void setTransactionlocalid(long transactionlocalid) {
        this.transactionlocalid = transactionlocalid;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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

    public long getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(long transactionid) {
        this.transactionid = transactionid;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isInstallment() {
        return installment;
    }

    public void setInstallment(boolean installment) {
        this.installment = installment;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Installment{" +
                "local_id=" + local_id +
                ", id=" + id +
                ", userid=" + userid +
                ", updated=" + updated +
                ", transactionid=" + transactionid +
                ", transactionlocalid=" + transactionlocalid +
                ", income=" + income +
                ", date=" + date +
                ", installment=" + installment +
                ", value=" + value +
                ", payment=" + payment +
                ", paid=" + paid +
                ", number=" + number +
                ", transaction=" + transaction +
                '}';
    }
}
