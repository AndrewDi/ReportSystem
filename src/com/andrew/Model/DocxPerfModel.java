package com.andrew.Model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.reflect.Array;
import java.util.List;

@JsonSerialize
public class DocxPerfModel {


    public String[] titles;
    public String[] captions;
    public String[] charts;

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public String[] getCaptions() {
        return captions;
    }

    public void setCaptions(String[] captions) {
        this.captions = captions;
    }

    public String[] getCharts() {
        return charts;
    }

    public void setCharts(String[] charts) {
        this.charts = charts;
    }
}
