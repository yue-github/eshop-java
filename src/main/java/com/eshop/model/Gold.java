package com.eshop.model;

public class Gold implements  java.io.Serializable{

    private Integer allGolds;

    public Integer getAllGolds() {
        return allGolds;
    }

    public void setAllGolds(Integer allGolds) {
        this.allGolds = allGolds;
    }

    @Override
    public String toString() {
        return "gold{" +
                "allGolds=" + allGolds +
                '}';
    }
}
