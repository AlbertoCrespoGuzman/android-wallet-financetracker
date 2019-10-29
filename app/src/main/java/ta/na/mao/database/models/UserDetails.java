package ta.na.mao.database.models;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

import ta.na.mao.R;
import ta.na.mao.utils.ErrorMessage;

@DatabaseTable
public class UserDetails  implements Serializable {

    @DatabaseField( allowGeneratedIdInsert=true, generatedId = true)
    transient private long local_id;
    @DatabaseField
    long id;
    @DatabaseField
    long userid;
    @DatabaseField
    boolean updated;


    @DatabaseField
    String name;
    @DatabaseField
    boolean socialname;
    @DatabaseField
    String cpf;
    @DatabaseField
    String birthday;
    @DatabaseField
    String mobilenumber;
    @DatabaseField
    String email;
    @DatabaseField
    int education;
    @DatabaseField
    int skincolor;
    @DatabaseField
    int genre;
    // ENTERPRISE
    @DatabaseField
    boolean formalized;
    @DatabaseField
    int familyincome;
    @DatabaseField
    String familynumber;
    @DatabaseField
    boolean activated;
    @DatabaseField
    int maincategory;
    @DatabaseField
    int subcategory;
    @DatabaseField
    String othersubcategory;

    transient public Mobile mobile = new Mobile();





    public UserDetails(){
        this.mobile = new Mobile();
    }
    public void setBirthdayformated(){

    }
    public boolean isEmpty(){
        boolean isEmpty = false;

        if(name == null || cpf == null  || birthday == null || email == null ||
                name == "" || cpf == "" || email == ""){
            isEmpty = true;
        }

        return isEmpty;
    }


    public long getLocal_id() {
        return local_id;
    }

    public boolean isSocialname() {
        return socialname;
    }

    public void setSocialname(boolean socialname) {
        this.socialname = socialname;
    }

    public String getFamilynumber() {
        return familynumber;
    }

    public void setFamilynumber(String familynumber) {
        this.familynumber = familynumber;
    }

    public int getEducation() {
        return education;
    }

    public void setEducation(int education) {
        this.education = education;
    }

    public int getSkincolor() {
        return skincolor;
    }

    public void setSkincolor(int skincolor) {
        this.skincolor = skincolor;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public boolean isFormalized() {
        return formalized;
    }

    public void setFormalized(boolean formalized) {
        this.formalized = formalized;
    }

    public int getFamilyincome() {
        return familyincome;
    }

    public void setFamilyincome(int familyincome) {
        this.familyincome = familyincome;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }


    public int getMaincategory() {
        return maincategory;
    }

    public void setMaincategory(int maincategory) {
        this.maincategory = maincategory;
    }

    public int getSubcategory() {
        return this.subcategory;
    }

    public void setSubcategory(int subcategory) {
        this.subcategory = subcategory;
    }

    public String getOthersubcategory() {
        return othersubcategory;
    }

    public void setOthersubcategory(String othersubcategory) {
        this.othersubcategory = othersubcategory;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

    public void setLocal_id(long local_id) {
        this.local_id = local_id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {

        this.mobilenumber = mobilenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void showDatepicker(){

    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "local_id=" + local_id +
                ", id=" + id +
                ", userid=" + userid +
                ", updated=" + updated +
                ", name='" + name + '\'' +
                ", socialname=" + socialname +
                ", cpf='" + cpf + '\'' +
                ", birthday=" + birthday +
                ", mobilenumber='" + mobilenumber + '\'' +
                ", email='" + email + '\'' +
                ", education=" + education +
                ", skincolor=" + skincolor +
                ", genre=" + genre +
                ", formalized=" + formalized +
                ", familyincome=" + familyincome +
                ", familynumber=" + familynumber +
                ", activated=" + activated +
                ", maincategory=" + maincategory +
                ", subcategory=" + subcategory +
                ", othersubcategory='" + othersubcategory + '\'' +
                ", mobile=" + mobile +
                '}';
    }
    public void printUserDetails (){
        Log.e("onclick binding", toString());
    }
    public List<ErrorMessage> validator(Context context, List<ErrorMessage> errors, View errorView){
        setMobilenumber(getMobile().getNumberString());

        if(name == null || name.isEmpty() || name.length() < 5){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.name_error), errorView));
        }else if(cpf == null || cpf.isEmpty() || cpf.length() != 11){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.cpf_error), errorView));
        }else if(birthday == null || birthday.isEmpty()){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.birthday_error), errorView));
        }else if(email.isEmpty() || email.length() < 5|| !email.contains("@") || !email.contains(".")){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.email_error), errorView));
        }else if(mobilenumber == null || mobilenumber.isEmpty() || mobilenumber.length() != 17){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.mobile_error), errorView));
        }else if(education == 0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.education_error), errorView));
        }else if(skincolor == 0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.skincolor_error), errorView));
        }else if(familyincome == 0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.familyincome_error), errorView));
        }else if(familynumber == null || familynumber.isEmpty() || Integer.parseInt(familynumber) == 0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.familynumber_error), errorView));
        }else if(maincategory == 0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.mainbusiness_error), errorView));
        }else if(subcategory == 0){
            errors.add(new ErrorMessage(context.getResources().getString(R.string.subcategory_error), errorView));
        }
        return errors;
    }


}
