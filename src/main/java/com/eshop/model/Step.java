package com.eshop.model;

public class Step implements  java.io.Serializable {

    private String phone ;
    private Integer flag;

    public Integer getFlag(){
        return flag;
    }
    public void setFlag(Integer flag){
        this.flag = flag;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
}
