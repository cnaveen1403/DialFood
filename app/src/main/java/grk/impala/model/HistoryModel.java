package grk.impala.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Samsung on 7/15/2015.
 */
public class HistoryModel {
    String id, dat, total, status;
    ArrayList<HashMap<String, String>> hashMapArrayList;

    public HistoryModel() {
    }

    public HistoryModel(String id, String dat, String total, String status, ArrayList<HashMap<String, String>> hashMapArrayList) {
        this.id = id;
        this.dat = dat;
        this.total = total;
        this.status = status;
        this.hashMapArrayList = hashMapArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDat() {
        return dat;
    }

    public void setDat(String dat) {
        this.dat = dat;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<HashMap<String, String>> getHashMapArrayList() {
        return hashMapArrayList;
    }

    public void setHashMapArrayList(ArrayList<HashMap<String, String>> hashMapArrayList) {
        this.hashMapArrayList = hashMapArrayList;
    }
}
