package grk.impala;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import grk.impala.util.ConstantUtil;
import grk.impala.util.PrefUtil;
import grk.impala.db.ItemTbl;
import grk.impala.model.ItemModel;
import grk.impala.model.TestModel;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private ViewPager viewPager;
    private TabLayout tab;
    private FloatingActionButton fabtn;
    private ProgressWheel progressWheel;

    private ItemTbl itemTbl;
    private MyPagerAdapter adapter;

    private ArrayList<TestModel> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        fabtn = (FloatingActionButton) findViewById(R.id.fab);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tab = (TabLayout) findViewById(R.id.tabs);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.dial_food));

        itemTbl = new ItemTbl(MainActivity.this);
        arrayList = new ArrayList<>();

        //Loading Menus
        if (ConnectionDetector.isInternetConnected(MainActivity.this)) {
            connectAsync();
        } else {
            progressWheel.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        fabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent(MainActivity.this, CartActivity.class), 5000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date startTime = dateFormat.parse("07:00");
                    Date endTime = dateFormat.parse("22:00");
                    Date currentTime = dateFormat.parse(dateFormat.format(new Date()));
                    if (currentTime.after(startTime) && currentTime.before(endTime)) {
                        startActivityForResult(new Intent(MainActivity.this, CartActivity.class), 5000);
                    } else {
                        new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialog)
                                .setTitle(R.string.app_name)
                                .setMessage(getString(R.string.timing_msg))
                                .setCancelable(true)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(arrayList.size());
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < arrayList.size(); i++) {
            ArrayList<ItemModel> list = new ArrayList<>();
            for (int x = 0; x < arrayList.get(i).getItemModelArrayList().size(); x++) {
                int itemId = arrayList.get(i).getItemModelArrayList().get(x).getItemId();
                String itemName = arrayList.get(i).getItemModelArrayList().get(x).getItemName();
                String itemRate = arrayList.get(i).getItemModelArrayList().get(x).getItemRate();
                String tnUrl = arrayList.get(i).getItemModelArrayList().get(x).getTnUrl();
                String imgUrl = arrayList.get(i).getItemModelArrayList().get(x).getImgUrl();
                String info = arrayList.get(i).getItemModelArrayList().get(x).getProductInfo();
                int avl = arrayList.get(i).getItemModelArrayList().get(x).getAvl();
                ItemModel itemModel = new ItemModel();
                itemModel.setId(arrayList.get(i).getId());
                itemModel.setItemId(itemId);
                itemModel.setItemName(itemName);
                itemModel.setItemRate(itemRate);
                itemModel.setAvl(avl);
                itemModel.setTnUrl(tnUrl);
                itemModel.setImgUrl(imgUrl);
                itemModel.setProductInfo(info);
                list.add(itemModel);
            }
            adapter.addFragment(new MenuFragment().newInstance(list), arrayList.get(i).getName());
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logOut();
            return true;
        }
        if (id == R.id.action_history) {
            showHistory();
            return true;
        }
        if (id == R.id.action_update) {
            showUpdate();
            return true;
        }
        if (id == R.id.action_notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        if (itemTbl.itemCount() > 0) {
            itemTbl.deleteAllItem();
            PrefUtil.setSetOrder(MainActivity.this, false);
        }
        PrefUtil.setClearCache(MainActivity.this);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void showHistory() {
        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
    }

    private void showUpdate() {
        startActivity(new Intent(MainActivity.this, UpdateActivity.class));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (PrefUtil.getSetOrder(MainActivity.this)) {
            new AlertDialog.Builder(this, R.style.MyAlertDialog)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.discard_order))
                    .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemTbl.deleteAllItem();
                            PrefUtil.setSetOrder(MainActivity.this, false);
                            finish();
                            /*viewPager.setCurrentItem(0, true);
                            viewPager.setAdapter(adapter);*/
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            finish();
        }
    }

    static class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<Fragment> mFragments = new ArrayList<>();
        private final ArrayList<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

    }

    private void visibleWidget() {
        tab.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        fabtn.setVisibility(View.VISIBLE);
    }

    private void connectAsync() {
        JsonObjectRequest jsRequest = new JsonObjectRequest(
                Request.Method.GET,
                ConstantUtil.URL_MENU,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    arrayList.clear();
                                    JSONArray menu = response.getJSONArray("menu");
                                    for (int j = 0; j < menu.length(); j++){
                                        JSONObject ol = menu.getJSONObject(j);
                                        int menuId = ol.getInt("id");
                                        String menuName = ol.getString("name");
                                        TestModel testModel = new TestModel();
                                        testModel.setId(menuId);
                                        testModel.setName(menuName);

                                        ArrayList<ItemModel> modelArrayList = new ArrayList<>();
                                        JSONArray item = ol.getJSONArray("item");
                                        for (int x = 0; x < item.length(); x++) {
                                            JSONObject list = item.getJSONObject(x);
                                            int itemId = list.getInt("item_id");
                                            String itemName = list.getString("item_name");
                                            String itemRate = list.getString("rate");
                                            int itemAvl = list.getInt("avl_flg");
                                            String itemUrl = list.getString("menu_url");
                                            String itemDtlUrl = list.getString("menu_dtl_url");
                                            String productInfo = list.getString("product_info");
                                            ItemModel itemModel = new ItemModel();
                                            itemModel.setItemId(itemId);
                                            itemModel.setItemName(itemName);
                                            itemModel.setItemRate(itemRate);
                                            itemModel.setAvl(itemAvl);
                                            itemModel.setTnUrl(itemUrl);
                                            itemModel.setImgUrl(itemDtlUrl);
                                            itemModel.setProductInfo(productInfo);
                                            modelArrayList.add(itemModel);
                                            testModel.setItemModelArrayList(modelArrayList);
                                        }
                                        arrayList.add(testModel);
                                    }
                                }
                            }
                            progressWheel.setVisibility(View.GONE);
                            if (success == 1) {
                                visibleWidget();
                                setupViewPager(viewPager);
                                tab.setupWithViewPager(viewPager);
                            } else {
                                showToast(getString(R.string.internet_error));
                            }
                        } catch (JSONException e) {
                            Log.e("Response Error", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressWheel.setVisibility(View.GONE);
                        Log.e("Volley Error", error.toString());
                        showToast(getString(R.string.internet_error));
                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(jsRequest);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5000 && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
            //viewPager.setCurrentItem(0);
        }
    }
}
