package com.haguepharmacy.model;

import java.util.ArrayList;
import java.util.List;

public class DrugResponse {

    List<SpinnerModel> records = new ArrayList<>();

    public List<SpinnerModel> getRecords() {
        return records;
    }

    public void setRecords(List<SpinnerModel> records) {
        this.records = records;
    }
}
