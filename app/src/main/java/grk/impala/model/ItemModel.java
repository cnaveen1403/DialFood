package grk.impala.model;

import android.os.Parcel;
import android.os.Parcelable;

import grk.impala.Item;

/**
 * Created by Samsung on 6/13/2015.
 */
public class ItemModel implements Item, Parcelable{
    int id, itemId, totalQty, avl;
    public String itemName, itemRate, tnUrl, imgUrl, productInfo;

    public ItemModel() {
    }

    public ItemModel(int id, int itemId, int totalQty, String itemName, String itemRate, String tnUrl, String imgUrl, String productInfo, int avl) {
        this.id = id;
        this.itemId = itemId;
        this.totalQty = totalQty;
        this.itemName = itemName;
        this.itemRate = itemRate;
        this.tnUrl = tnUrl;
        this.imgUrl = imgUrl;
        this.productInfo = productInfo;
        this.avl = avl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemRate() {
        return itemRate;
    }

    public void setItemRate(String itemRate) {
        this.itemRate = itemRate;
    }

    public String getTnUrl() {
        return tnUrl;
    }

    public void setTnUrl(String tnUrl) {
        this.tnUrl = tnUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public int getAvl() {
        return avl;
    }

    public void setAvl(int avl) {
        this.avl = avl;
    }

    @Override
    public boolean isTotal() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(itemId);
        dest.writeString(itemName);
        dest.writeString(itemRate);
        dest.writeString(tnUrl);
        dest.writeString(imgUrl);
        dest.writeInt(totalQty);
        dest.writeString(productInfo);
        dest.writeInt(avl);
    }

    private ItemModel(Parcel in) {
        this.id = in.readInt();
        this.itemId = in.readInt();
        this.itemName = in.readString();
        this.itemRate = in.readString();
        this.tnUrl = in.readString();
        this.imgUrl = in.readString();
        this.totalQty = in.readInt();
        this.productInfo = in.readString();
        this.avl = in.readInt();
    }

    public static final Creator<ItemModel> CREATOR = new Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel source) {
            return new ItemModel(source);
        }

        @Override
        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };
}
