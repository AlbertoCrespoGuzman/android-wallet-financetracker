package ta.na.mao.database.models;

import java.util.Date;

public class StatementElement{

    private Date dateTime;
    private Object statement;
    private Transaction transaction;
    private Installment installment;

    public StatementElement(){

    }

    public StatementElement(Transaction transaction, Installment installment){
        this.transaction = transaction;
        this.installment = installment;
        if(transaction != null){
            this.dateTime = transaction.getDate();
        }else if(installment != null){
            this.dateTime = installment.getDate();
        }
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Installment getInstallment() {
        return installment;
    }

    public void setInstallment(Installment installment) {
        this.installment = installment;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    public Object getStatement() {
        return statement;
    }

    public void setStatement(Object statement) {
        this.statement = statement;
    }


}