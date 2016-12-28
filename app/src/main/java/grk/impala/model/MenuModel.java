package grk.impala.model;

/**
 * Created by Samsung on 6/11/2015.
 */
public class MenuModel {
    public int id, qty, itemId, avl;
    public String menu;
    public String name;
    public String rate;
    public String tnUrl;
    public String imgUrl;
    public String info;

    public MenuModel() {
    }

    public MenuModel(int id, int itemId, int qty, String menu, String name, String rate, String tnUrl, String imgUrl, String info, int avl) {
        this.id = id;
        this.qty = qty;
        this.itemId = itemId;
        this.menu = menu;
        this.name = name;
        this.rate = rate;
        this.tnUrl = tnUrl;
        this.imgUrl = imgUrl;
        this.info = info;
        this.avl = avl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getAvl() {
        return avl;
    }

    public void setAvl(int avl) {
        this.avl = avl;
    }
}
