package com.stargazers.ncsvcemk200stargazers.util;

public class AadhaarForm extends RegexParser {

    public AadhaarForm(String store) {
        super(store);
        this.addField("Aadhaar No", "\\suid=\"(\\d{12})\"");
        this.addField("Name", "\\sname=\"([\\w\\s.]+)\"");
        this.addField("Gender", "\\sgender=\"([A-Z])\"");
        this.addField("DOB", "\\sdateOfBirth=\"([\\d-]+)\"");
        this.addField("Care Of", "\\scareOf=\"([\\w\\s/:]+)\"");
        this.addField("Building No", "\\sbuilding=\"([\\w\\s\\/]+)\"");
        this.addField("Street", "\\sstreet=\"([\\w\\s.]+)\"");
        this.addField("VTC", "\\svtcName=\"([\\w\\s.]+)\"");
        this.addField("Post Office", "\\spoName=\"([\\w\\s.]+)\"");
        this.addField("District", "\\sdistrictName=\"([\\w\\s.]+)\"");
        this.addField("Sub District", "\\ssubDistrictName=\"([\\w\\s.]+)\"");
        this.addField("State", "\\sstateName=\"([\\w\\s.]+)\"");
        this.addField("Pincode", "\\spincode=\"(\\d{6})\"");
        this.scoop();
    }
}
