package com.merc.gmall.bean;

import java.io.Serializable;

public class HatArea  implements Serializable {
    private Integer id;
    private String areaId;
    private String area;
    private String father;

    @Override
    public String toString() {
        return "HatArea{" +
                "id=" + id +
                ", areaId='" + areaId + '\'' +
                ", area='" + area + '\'' +
                ", father='" + father + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }
}
