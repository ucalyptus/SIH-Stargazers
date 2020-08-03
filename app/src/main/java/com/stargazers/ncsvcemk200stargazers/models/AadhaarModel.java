package com.stargazers.ncsvcemk200stargazers.models;

public class AadhaarModel {

    private String uid;
    private String name;
    private String gender;
    private String dob;
    private String careof;
    private String buildingNo;
    private String street;
    private String vtc;
    private String po;
    private String district;
    private String subDistrict;
    private String state;
    private String pin;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCareof() {
        return careof;
    }

    public void setCareof(String careof) {
        this.careof = careof;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getVtc() {
        return vtc;
    }

    public void setVtc(String vtc) {
        this.vtc = vtc;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString(){
        String s="";
        s+="Aadhaar No.: "+this.getUid();
        s+="\nName: "+this.getName();
        s+="\nGender: "+this.getGender();
        s+="\nD.O.B: "+this.getDob();
        s+="\nCare of: "+this.getCareof();
        s+="\nBuilding No: "+this.getBuildingNo();
        s+="\nStreet: "+this.getStreet();
        s+="\nVTC: "+this.getVtc();
        s+="\nP/O: "+this.getPo();
        s+="\nDistrict: "+this.getDistrict();
        s+="\nSub-District: "+this.getSubDistrict();
        s+="\nState: "+this.getState();
        s+="\nPin code: "+this.getPin();

        return s;
    }
}
