package com.stargazers.ncsvcemk200stargazers.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class ApplicationModel {

    private String applicationID;
    @ServerTimestamp private Timestamp timestamp;

    private String docPic;

    private AadhaarModel aadhaarModel;

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocPic() {
        return docPic;
    }

    public void setDocPic(String docPic) {
        this.docPic = docPic;
    }

    public AadhaarModel getAadhaarModel() {
        return aadhaarModel;
    }

    public void setAadhaarModel(AadhaarModel aadhaarModel) {
        this.aadhaarModel = aadhaarModel;
    }
}
