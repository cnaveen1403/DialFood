package grk.impala;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import grk.impala.model.TotalModel;
import grk.impala.util.PrefUtil;
import grk.impala.db.ItemTbl;
import grk.impala.model.ItemModel;

/**
 * Created by Samsung on 6/13/2015.
 */
public class CartActivity extends AppCompatActivity {

    private LinearLayout llEmptyCart, llBill;
    private Toolbar toolBar;
    private ListView lvBill;

    private ItemTbl itemTbl;

    private String orderTotal = null;
    private ArrayList<HashMap<String, String>> orderList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        llEmptyCart = (LinearLayout) findViewById(R.id.llEmptyCart);
        llBill = (LinearLayout) findViewById(R.id.llBill);
        lvBill = (ListView) findViewById(R.id.listView);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.my_cart));

        itemTbl = new ItemTbl(CartActivity.this);
        orderList = new ArrayList<HashMap<String, String>>();

        if (PrefUtil.getSetOrder(CartActivity.this)) {
            llEmptyCart.setVisibility(View.GONE);
            llBill.setVisibility(View.VISIBLE);

            ArrayList<Item> itemArrayList = new ArrayList<>();
            ArrayList<ItemModel> arrayList = itemTbl.selectAllItem();
            if (arrayList.size() != 0) {
                Double total = 0.0;
                for (int i = 0; i < arrayList.size(); i++) {
                    ItemModel itemModel = new ItemModel();
                    itemModel.setId(arrayList.get(i).getId());
                    itemModel.setItemId(arrayList.get(i).getItemId());
                    itemModel.setItemName(arrayList.get(i).getItemName());
                    itemModel.setItemRate(arrayList.get(i).getItemRate());
                    itemModel.setTotalQty(arrayList.get(i).getTotalQty());
                    total = total + (Double.valueOf(arrayList.get(i).getItemRate()) * arrayList.get(i).getTotalQty());
                    itemArrayList.add(itemModel);
                }

                TotalModel totalModel = new TotalModel();
                totalModel.setTotal(decimalConv(total));
                totalModel.setPackingCharge("10.00");
                totalModel.setGrandTotal(decimalConv(total+10));
                itemArrayList.add(totalModel);
            }
            CartAdapter cartAdapter = new CartAdapter(CartActivity.this, itemArrayList);
            lvBill.setAdapter(cartAdapter);
        } else {
            llEmptyCart.setVisibility(View.VISIBLE);
            llBill.setVisibility(View.GONE);
        }
    }

    private String decimalConv(double val) {
        return String.format("%.02f", val);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6000 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    public class CartAdapter extends ArrayAdapter<Item> {

        private LayoutInflater inflater;
        private ArrayList<Item> arrayList;
        private Context context;

        public CartAdapter(Context context, ArrayList<Item> objects) {
            super(context, 0, objects);
            this.context = context;
            arrayList = objects;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
                final Item item = arrayList.get(position);
                if (item != null) {
                if (item.isTotal()) {
                    final TotalModel totalModel = (TotalModel) item;
                    view = inflater.inflate(R.layout.total_item, null);
                    TextView subTotal = (TextView) view.findViewById(R.id.tvTotalAmt);
                    TextView packingCharge = (TextView) view.findViewById(R.id.tvPackingAmt);
                    TextView grandTotal = (TextView) view.findViewById(R.id.tvGrandAmt);
                    TextView deliveryAddress = (TextView) view.findViewById(R.id.tvDel);
                    TextView deliveryCity = (TextView) view.findViewById(R.id.tvDelCity);
                    Button pay = (Button) view.findViewById(R.id.btProceed);

                    subTotal.setText(getString(R.string.rs)+totalModel.getTotal());
                    packingCharge.setText(getString(R.string.rs)+totalModel.getPackingCharge());
                    grandTotal.setText(getString(R.string.rs)+totalModel.getGrandTotal());
                    deliveryAddress.setText(PrefUtil.getAddress(context));
                    deliveryCity.setText(PrefUtil.getCity(context));

                    orderTotal = totalModel.getGrandTotal();

                    pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(context, CardActivity.class);
                            in.putExtra("total", orderTotal);
                            in.putExtra("orderList", orderList);
                            startActivityForResult(in, 6000);
                        }
                    });
                } else {
                    ItemModel itemModel = (ItemModel) item;
                    view = inflater.inflate(R.layout.cart_item, null);
                    TextView menuId = (TextView) view.findViewById(R.id.tvMenuId);
                    TextView itemId = (TextView) view.findViewById(R.id.tvItemId);
                    TextView name = (TextView) view.findViewById(R.id.tvName);
                    TextView rate = (TextView) view.findViewById(R.id.tvRate);
                    TextView qty = (TextView) view.findViewById(R.id.tvQty);
                    TextView total = (TextView) view.findViewById(R.id.tvTotal);

                    menuId.setText(String.valueOf(itemModel.getId()));
                    itemId.setText(String.valueOf(itemModel.getItemId()));
                    name.setText(itemModel.getItemName());
                    qty.setText(String.valueOf(itemModel.getTotalQty()) + " x ");
                    rate.setText(getString(R.string.rs) + itemModel.getItemRate());
                    total.setText(getString(R.string.rs) + decimalConv(itemModel.getTotalQty() * Double.valueOf(itemModel.getItemRate())));

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("menuId", String.valueOf(itemModel.getId()));
                    map.put("itemId", String.valueOf(itemModel.getItemId()));
                    map.put("qty", String.valueOf(itemModel.getTotalQty()));
                    orderList.add(map);
                }
            }
            return view;
        }
    }
}
