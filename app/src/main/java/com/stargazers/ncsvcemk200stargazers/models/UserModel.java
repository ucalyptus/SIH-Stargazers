package com.stargazers.ncsvcemk200stargazers.models;

public class UserModel {

    private String phoneNo;
    private long accountType;
    private String address;
    private String id;
    private String idProof;

    private long statusApplication;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public long getAccountType() {
        return accountType;
    }

    public void setAccountType(long accountType) {
        this.accountType = accountType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public long getStatusApplication() {
        return statusApplication;
    }

    public void setStatusApplication(long statusApplication) {
        this.statusApplication = statusApplication;
    }
}
