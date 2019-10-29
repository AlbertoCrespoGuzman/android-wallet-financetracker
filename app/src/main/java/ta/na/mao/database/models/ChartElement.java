package ta.na.mao.database.models;

public class ChartElement {

    String label;
    double value;
    String color;
    int category;

    public ChartElement(){}

    public ChartElement(String label, int category, double value){
        this.label = label;
        this.value = value;
        this.category = category;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ChartElement{" +
                "label='" + label + '\'' +
                ", value=" + value +
                ", color='" + color + '\'' +
                ", category=" + category +
                '}';
    }
}
