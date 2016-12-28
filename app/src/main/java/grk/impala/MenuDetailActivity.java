package grk.impala;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import grk.impala.util.PrefUtil;
import grk.impala.db.ItemTbl;
import grk.impala.model.ItemModel;

/**
 * Created by Samsung on 6/11/2015.
 */
public class MenuDetailActivity extends AppCompatActivity {

    private TextView tvInfo, tvBasicRate, tvQty, tvTotalRate;//, tvItemName;
    private Toolbar toolBar;
    private Button btAdd, btSub, btCart;

    private ItemTbl itemTbl;
    private int position, num = 1;
    private String id, name, rate, menuId, url, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menudetail);

        //tvItemName = (TextView) findViewById(R.id.tvItemName);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvBasicRate = (TextView) findViewById(R.id.tvBasicRate);
        tvQty = (TextView) findViewById(R.id.tvQty);
        tvTotalRate = (TextView) findViewById(R.id.tvTotalRate);
        btAdd = (Button) findViewById(R.id.btAdd);
        btSub = (Button) findViewById(R.id.btSub);
        btCart = (Button) findViewById(R.id.btCart);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_close);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (getIntent().getStringExtra("itemId") != null) {
            id = getIntent().getStringExtra("itemId");
        }
        if (getIntent().getStringExtra("itemName") != null) {
            name = getIntent().getStringExtra("itemName");
        }
        if (getIntent().getStringExtra("itemRate") != null) {
            rate = getIntent().getStringExtra("itemRate");
        }
        if (getIntent().getStringExtra("menuId") != null) {
            menuId = getIntent().getStringExtra("menuId");
        }
        if (getIntent().getStringExtra("Image") != null) {
            url = getIntent().getStringExtra("Image");
        }
        if (getIntent().getStringExtra("info") != null) {
            info = getIntent().getStringExtra("info");
        }

        if (getIntent().getIntExtra("qty", 1) != 0) {
            num = getIntent().getIntExtra("qty", 1);
        } else {
            num = 1;
        }

        position = getIntent().getIntExtra("position", -1);

        itemTbl = new ItemTbl(MenuDetailActivity.this);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(name);

        loadBackdrop();
        //tvItemName.setText(name);
        tvInfo.setText(info);
        tvBasicRate.setText(getString(R.string.rs)+" "+rate);
        tvQty.setText("Qty "+String.valueOf(num));
        tvTotalRate.setText(getString(R.string.rs)+" "+decimalConv(num * Double.parseDouble(rate)));

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = num + 1;
                tvQty.setText("Qty "+String.valueOf(num));
                tvTotalRate.setText(getString(R.string.rs)+" "+decimalConv(num * Double.parseDouble(rate)));
            }
        });

        btSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num > 1) {
                    num = num - 1;
                    tvQty.setText("Qty "+String.valueOf(num));
                    tvTotalRate.setText(getString(R.string.rs) + " " + decimalConv(num * Double.parseDouble(rate)));
                }
            }
        });

        btCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemTbl.itemPresent(Integer.parseInt(id)) == 0) {
                    ItemModel itemModel = new ItemModel();
                    itemModel.setId(Integer.valueOf(menuId));
                    itemModel.setItemId(Integer.valueOf(id));
                    itemModel.setItemName(name);
                    itemModel.setItemRate(rate);
                    itemModel.setTotalQty(num);
                    itemTbl.insertItem(itemModel);
                } else {
                    ItemModel itemModel = new ItemModel();
                    itemModel.setId(Integer.valueOf(menuId));
                    itemModel.setItemId(Integer.valueOf(id));
                    itemModel.setItemName(name);
                    itemModel.setItemRate(rate);
                    itemModel.setTotalQty(num);
                    itemTbl.updateItem(itemModel);
                }

                PrefUtil.setSetOrder(MenuDetailActivity.this, true);

                String qty = String.valueOf(num);
                Intent in = new Intent();
                in.putExtra("totalQty", qty);
                in.putExtra("position", position);
                setResult(RESULT_OK, in);
                finish();
            }
        });
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(url).centerCrop().into(imageView);
    }

    private String decimalConv(double val) {
        /*String.format("%.02f", val);
        DecimalFormat df = new DecimalFormat("#.##");*/
        return String.format("%.02f", val);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menudetail, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (itemTbl.itemPresent(name) == 0) {
                ItemModel itemModel = new ItemModel();
                itemModel.setItemName(name);
                itemModel.setItemRate(rate);
                itemModel.setTotalQty(num);
                itemTbl.insertItem(itemModel);
            } else {
                ItemModel itemModel = new ItemModel();
                itemModel.setItemName(name);
                itemModel.setItemRate(rate);
                itemModel.setTotalQty(num);
                itemTbl.updateItem(itemModel);
            }
            PrefUtil.setSetOrder(MenuDetailActivity.this, true);

            String qty = String.valueOf(num);
            Intent in = new Intent();
            in.putExtra("totalQty", qty);
            in.putExtra("position", position);
            setResult(RESULT_OK, in);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
