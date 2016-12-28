package grk.impala;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import grk.impala.util.ConstantUtil;
import grk.impala.util.PrefUtil;

/**
 * Created by Samsung on 7/5/2015.
 */
public class StatusActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolBar;
    private ProgressWheel progressWheel;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<HashMap<String, String>> hashMapArrayList;
    private StatusAdapter statusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.amber),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.primary));
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        listView = (ListView) findViewById(R.id.listView);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.order_status));

        hashMapArrayList = new ArrayList<>();
        /*if (ConnectionDetector.isInternetConnected(StatusActivity.this)) {
            new StatusAsync().execute();
        }else{
            progressWheel.setVisibility(View.GONE);
            showToast(getString(R.string.no_internet));
        }*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectionDetector.isInternetConnected(StatusActivity.this)) {
                    statusAsync(String.valueOf(PrefUtil.getUserType(StatusActivity.this)));
                }else{
                    showToast(getString(R.string.no_internet));
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (hashMapArrayList.size() != 0) {
                    String orderId = hashMapArrayList.get(position).get("id");
                    String userId = hashMapArrayList.get(position).get("uid");
                    Intent intent = new Intent(StatusActivity.this, StatusDetailActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectionDetector.isInternetConnected(StatusActivity.this)) {
            statusAsync(String.valueOf(PrefUtil.getUserType(StatusActivity.this)));
        }else{
            progressWheel.setVisibility(View.GONE);
            showToast(getString(R.string.no_internet));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logOut();
            return true;
        }
        if (id == R.id.action_delivery) {
            startActivity(new Intent(StatusActivity.this, DeliveryStatusActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        PrefUtil.setClearCache(StatusActivity.this);
        startActivity(new Intent(StatusActivity.this, LoginActivity.class));
        finish();
    }

    private void statusAsync(String userType) {
        Map<String, String> map = new HashMap<>();
        map.put("user_type", userType);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_SELECT_ORDER,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        Log.i("Response", response.toString());
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    hashMapArrayList.clear();
                                    JSONArray menu = response.getJSONArray("order");
                                    for (int j = 0; j < menu.length(); j++){
                                        JSONObject ol = menu.getJSONObject(j);
                                        String userId = ol.getString("user_id");
                                        String id = ol.getString("order_id");
                                        String dat = ol.getString("order_date");
                                        String total = ol.getString("total");
                                        String status = ol.getString("status");
                                        String mobileNo = ol.getString("mobile_no");
                                        String address = ol.getString("address");
                                        String city = ol.getString("city");
                                        if (status.equals("1")) {
                                            status = "Booked";
                                        }else if (status.equals("2")) {
                                            status = "Delivered";
                                        }
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("id", id);
                                        map.put("date", dat);
                                        map.put("total", total);
                                        map.put("status", status);
                                        map.put("uid", userId);
                                        map.put("mobile", mobileNo);
                                        map.put("address", address);
                                        map.put("city", city);
                                        hashMapArrayList.add(map);
                                    }
                                }
                            }
                            progressWheel.setVisibility(View.GONE);
                            if (success == 1) {
                                listView.setVisibility(View.VISIBLE);
                                statusAdapter = new StatusAdapter(StatusActivity.this, hashMapArrayList);
                                listView.setAdapter(statusAdapter);

                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            } else {
                                showToast(getString(R.string.internet_error));
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
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

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public class StatusAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ViewHolder viewHolder;
        private ArrayList<HashMap<String, String>> alist;
        private Context context;

        public StatusAdapter(Context context, ArrayList<HashMap<String, String>> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            alist = list;
        }

        @Override
        public int getCount() {
            return alist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = inflater.inflate(R.layout.status_item, null);
                viewHolder = new ViewHolder();

                viewHolder.id = (TextView) convertView.findViewById(R.id.tvOrderId);
                viewHolder.dat = (TextView) convertView.findViewById(R.id.tvOrderDT);
                viewHolder.status = (TextView) convertView.findViewById(R.id.tvTotal);
                viewHolder.total = (TextView) convertView.findViewById(R.id.tvStatus);
                viewHolder.mobile = (TextView) convertView.findViewById(R.id.tvMobileNo);
                viewHolder.addr = (TextView) convertView.findViewById(R.id.tvAddress);
                viewHolder.city = (TextView) convertView.findViewById(R.id.tvCity);
                viewHolder.userId = (TextView) convertView.findViewById(R.id.tvUserId);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.userId.setText(alist.get(position).get("uid"));
            viewHolder.id.setText("#"+alist.get(position).get("id"));
            viewHolder.dat.setText(alist.get(position).get("date"));
            viewHolder.status.setText(getString(R.string.rs)+alist.get(position).get("total"));
            if (alist.get(position).get("status").equals("Booked")) {
                viewHolder.total.setTextColor(getResources().getColor(R.color.green));
            }else{
                viewHolder.total.setTextColor(getResources().getColor(R.color.primary));
            }
            viewHolder.total.setText(alist.get(position).get("status"));
            viewHolder.mobile.setText(alist.get(position).get("mobile"));
            viewHolder.addr.setText(alist.get(position).get("address"));
            viewHolder.city.setText(alist.get(position).get("city"));
            return convertView;
        }

        private class ViewHolder {
            TextView id, dat, status, total, mobile, addr, city, userId;
        }
    }
}
