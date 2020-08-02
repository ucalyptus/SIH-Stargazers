package com.stargazers.ncsvcemk200stargazers.util;

import android.os.Build;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser {

    private int JType;
    private String store;
    private TreeMap<String, String> JFields;
    private TreeMap<String, String> JValues;
    RegexParser(String store){
        this.store = store;
        JFields = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        JValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }
    void addField(String name, String pattern){
        this.JFields.put(name, pattern);
    }
    void scoop(){
        Pattern p;
        Matcher m;
        String match="";
        for(Map.Entry<String,String> entry : JFields.entrySet()) {
            p=Pattern.compile(entry.getValue(), Pattern.CASE_INSENSITIVE);
            m=p.matcher(store);
            if(m.find()){
                if(m.groupCount()==1){
                    match = m.group(1);
                } else if(m.groupCount()==0){
                    match = m.group(0);
                }
                if(this.JValues.containsKey(entry.getKey())){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        this.JValues.replace(entry.getKey(), match);
                    }
                } else{
                    this.JValues.put(entry.getKey(), match);
                }
            }
        }
    }

    public TreeMap<String, String> getJValues() {
        return JValues;
    }
}
