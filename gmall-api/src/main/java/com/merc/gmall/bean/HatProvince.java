package com.merc.gmall.bean;

import java.io.Serializable;

public class HatProvince implements Serializable {

    private int id;
    private String provinceId;
    private String province;

    @Override
    public String toString() {
        return "HatProvince{" +
                "id=" + id +
                ", provinceId='" + provinceId + '\'' +
                ", province='" + province + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
