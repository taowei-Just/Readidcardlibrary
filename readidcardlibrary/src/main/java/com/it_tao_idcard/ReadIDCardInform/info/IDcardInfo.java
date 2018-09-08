package com.it_tao_idcard.ReadIDCardInform.info;

import android.graphics.Bitmap;

public class IDcardInfo {
	

	//姓名
	private String name  ;
	//性别
	private String sex  ;
	//生日
	private String birth  ;
	
	private String nation  ;
	//住址
	
	private String address ;
	//发证机关
	private String Department  ;
	//号码
	private String IDNo  ;
	
	private String EffectDate ;
	private String ExpireDate  ;
	//
	String BmpFile ;	
	//
	String FpMsg  ;
	//图片
	Bitmap bm ;

 
	public Bitmap getBm() {
		return bm;
	}

	public void setBm(Bitmap bm) {
		this.bm = bm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public String getIDNo() {
		return IDNo;
	}

	public void setIDNo(String iDNo) {
		IDNo = iDNo;
	}

	public String getEffectDate() {
		return EffectDate;
	}

	public void setEffectDate(String effectDate) {
		EffectDate = effectDate;
	}

	public String getExpireDate() {
		return ExpireDate;
	}

	public void setExpireDate(String expireDate) {
		ExpireDate = expireDate;
	}

	public String getBmpFile() {
		return BmpFile;
	}

	public void setBmpFile(String bmpFile) {
		BmpFile = bmpFile;
	}

	public String getFpMsg() {
		return FpMsg;
	}

	public void setFpMsg(String fpMsg) {
		FpMsg = fpMsg;
	}

	@Override
	public String toString() {
		return "IDcardInfo [name=" + name + ", sex=" + sex + ", birth=" + birth
				+ ", nation=" + nation + ", address=" + address
				+ ", Department=" + Department + ", IDNo=" + IDNo
				+ ", EffectDate=" + EffectDate + ", ExpireDate=" + ExpireDate
				+ ", BmpFile=" + BmpFile + ", FpMsg=" + FpMsg + ", "
				+ "]";
	}

  
	
	

}
