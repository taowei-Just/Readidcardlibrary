package com.authen;


import static android.R.attr.name;

/**
 * Created by Tao on 2018/3/20 0020.
 */

public class AuthenDataBulder {


   String  name ;
   String  address ;
   String  authenBmPath ;
   String  idNo ;
   String  idBmPath ;

   String  sex ;
   String  birth ;
   String  nation ;


   float  score ;
   boolean  pass ;


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAuthenBmPath() {
        return authenBmPath;
    }

    public void setAuthenBmPath(String authenBmPath) {
        this.authenBmPath = authenBmPath;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIdBmPath() {
        return idBmPath;
    }

    public void setIdBmPath(String idBmPath) {
        this.idBmPath = idBmPath;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "AuthenDataBulder{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", authenBmPath='" + authenBmPath + '\'' +
                ", idNo='" + idNo + '\'' +
                ", idBmPath='" + idBmPath + '\'' +
                ", sex='" + sex + '\'' +
                ", birth='" + birth + '\'' +
                ", nation='" + nation + '\'' +
                ", score=" + score +
                ", pass=" + pass +
                '}';
    }
}
