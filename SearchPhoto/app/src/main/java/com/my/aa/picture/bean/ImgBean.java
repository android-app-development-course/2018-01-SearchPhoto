package com.my.aa.picture.bean;

public class ImgBean {
    String name;
    String path;
    String desc;

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {

        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDesc() {
        return desc;
    }

    public ImgBean(String name, String path, String desc) {

        this.name = name;
        this.path = path;
        this.desc = desc;
    }
}
