package ta.na.mao.database.models;

import java.util.List;

public class ReportElement {

    List<List<Transaction>> transactions;
    List<List<Installment>> installments;
    List<List<Goal>> goals;
    int period;
    boolean income;
    String label;
    int goal_category;

    public ReportElement(){

    }
    public ReportElement(List<List<Transaction>> transactions, List<List<Installment>> installments,
            List<List<Goal>> goals, boolean income, String label, int period, int goal_category){
            this.transactions = transactions;
            this.installments = installments;
            this.goals = goals;
            this.income = income;
            this.label = label;
            this.period = period;
            this.goal_category = goal_category;
    }

    public List<List<Transaction>> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<List<Transaction>> transactions) {
        this.transactions = transactions;
    }

    public List<List<Installment>> getInstallments() {
        return installments;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getGoal_category() {
        return goal_category;
    }

    public void setGoal_category(int goal_category) {
        this.goal_category = goal_category;
    }

    public void setInstallments(List<List<Installment>> installments) {
        this.installments = installments;
    }

    public List<List<Goal>> getGoals() {
        return goals;
    }

    public void setGoals(List<List<Goal>> goals) {
        this.goals = goals;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }



    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ReportElement{" +
                "transactions=" + transactions +
                ", installments=" + installments +
                ", goals=" + goals +
                ", period=" + period +
                ", income=" + income +
                ", label='" + label + '\'' +
                ", goal_category=" + goal_category +
                '}';
    }
}
