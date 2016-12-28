package grk.impala.model;

import java.util.ArrayList;

/**
 * Created by Samsung on 7/8/2015.
 */
public class TestModel {
    int id;
    String name;
    ArrayList<ItemModel> itemModelArrayList;
    public TestModel() {
    }

    public TestModel(int id, String name, ArrayList<ItemModel> itemModelArrayList) {
        this.id = id;
        this.name = name;
        this.itemModelArrayList = itemModelArrayList;
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

    public ArrayList<ItemModel> getItemModelArrayList() {
        return itemModelArrayList;
    }

    public void setItemModelArrayList(ArrayList<ItemModel> itemModelArrayList) {
        this.itemModelArrayList = itemModelArrayList;
    }
}
