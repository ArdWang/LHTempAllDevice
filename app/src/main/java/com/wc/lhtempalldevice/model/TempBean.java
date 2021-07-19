package com.wc.lhtempalldevice.model;

import java.io.Serializable;

/**
 * Created by rnd on 2017/5/9.
 */

public class TempBean implements Serializable {
    private String dervicename;

    public String getDervicetemp() {
        return dervicetemp;
    }

    public void setDervicetemp(String dervicetemp) {
        this.dervicetemp = dervicetemp;
    }

    public String getDervicename() {
        return dervicename;
    }

    public void setDervicename(String dervicename) {
        this.dervicename = dervicename;
    }

    public String getDerviceaddre() {
        return derviceaddre;
    }

    public void setDerviceaddre(String derviceaddre) {
        this.derviceaddre = derviceaddre;
    }

    private String dervicetemp;
    private String derviceaddre;
}
