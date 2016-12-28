package grk.impala.model;

import grk.impala.Item;

/**
 * Created by Samsung on 6/13/2015.
 */
public class TotalModel implements Item {

    public String total, packingCharge, grandTotal;

    public TotalModel() {
    }

    public TotalModel(String total, String packingCharge, String grandTotal) {
        this.total = total;
        this.packingCharge = packingCharge;
        this.grandTotal = grandTotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPackingCharge() {
        return packingCharge;
    }

    public void setPackingCharge(String packingCharge) {
        this.packingCharge = packingCharge;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    @Override
    public boolean isTotal() {
        return true;
    }
}
