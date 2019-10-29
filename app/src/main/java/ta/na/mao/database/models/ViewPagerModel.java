package ta.na.mao.database.models;

public class ViewPagerModel {

    String title;
    int layoutId;

    public ViewPagerModel(){}


    public ViewPagerModel(String title, int layoutId){
        this.title = title;
        this.layoutId = layoutId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public String toString() {
        return "ViewPagerModel{" +
                "title='" + title + '\'' +
                ", layoutId=" + layoutId +
                '}';
    }
}
