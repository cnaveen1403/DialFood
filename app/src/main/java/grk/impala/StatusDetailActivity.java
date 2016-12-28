package grk.impala;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
 * Created by Samsung on 7/13/2015.
 */
public class StatusDetailActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolBar;
    private ProgressWheel progressWheel;
    private Button btDispatch, btDeliver;

    private ArrayList<HashMap<String, String>> hashMapArrayList;
    private StatusDetailAdapter statusDetailAdapter;
    private ProgressDialog progressDialog;
    private String orderId = null, userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusdtl);

        btDispatch = (Button) findViewById(R.id.btDispatch);
        btDeliver = (Button) findViewById(R.id.btDeliver);
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        listView = (ListView) findViewById(R.id.listView);
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

        if (PrefUtil.getUserType(StatusDetailActivity.this) != 2) {
            btDispatch.setVisibility(View.GONE);
        }

        hashMapArrayList = new ArrayList<>();
        if (getIntent().getStringExtra("orderId") != null) {
            orderId = getIntent().getStringExtra("orderId");
            getSupportActionBar().setTitle("#"+orderId);
        }
        if (getIntent().getStringExtra("userId") != null) {
            userId = getIntent().getStringExtra("userId");
        }
        if (ConnectionDetector.isInternetConnected(StatusDetailActivity.this)) {
            statusDetailAsync(orderId, userId);
        }else{
            progressWheel.setVisibility(View.GONE);
            showToast(getString(R.string.no_internet));
        }

        btDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isInternetConnected(StatusDetailActivity.this)) {
                    sendDispatch(1);
                }else{
                    showToast(getString(R.string.no_internet));
                }
            }
        });

        btDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isInternetConnected(StatusDetailActivity.this)) {
                    sendDispatch(2);
                }else{
                    showToast(getString(R.string.no_internet));
                }
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(StatusDetailActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void sendDispatch(final int flag) {
        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("order_id", orderId);
        map.put("flag", Integer.toString(flag));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_NOTIFY_DISPATCH,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        cancelProgressDialog();
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                            }
                            if (success == 1) {
                                showToast("Notification Send Successfully");
                            }else if (success == 0) {
                                showToast("Error in sending Notification");
                            }else if (success == 2) {
                                showToast("Updated Successfully");
                            }else if (success == 3) {
                                showToast("Error in Updation");
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
                        cancelProgressDialog();
                        Log.e("Volley Error", error.toString());
                        showToast(getString(R.string.internet_error));
                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void statusDetailAsync(String oid, String uid) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", uid);
        map.put("order_id", oid);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_SELECT_ORDER_DTL,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    hashMapArrayList.clear();
                                    JSONArray menu = response.getJSONArray("order_dtl");
                                    for (int j = 0; j < menu.length(); j++){
                                        JSONObject ol = menu.getJSONObject(j);
                                        String name = ol.getString("item_name");
                                        String qty = ol.getString("qty");

                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("name", name);
                                        map.put("qty", qty);
                                        hashMapArrayList.add(map);
                                    }
                                }
                            }
                            progressWheel.setVisibility(View.GONE);
                            if (success == 1) {
                                listView.setVisibility(View.VISIBLE);
                                statusDetailAdapter = new StatusDetailAdapter(StatusDetailActivity.this, hashMapArrayList);
                                listView.setAdapter(statusDetailAdapter);
                            } else if (success == 0) {
                                showToast("No order found");
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

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public class StatusDetailAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ViewHolder viewHolder;
        private ArrayList<HashMap<String, String>> alist;
        private Context context;

        public StatusDetailAdapter(Context context, ArrayList<HashMap<String, String>> list){
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
                convertView = inflater.inflate(R.layout.statusdtl_tem, null);
                viewHolder = new ViewHolder();

                viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.qty = (TextView) convertView.findViewById(R.id.tvQty);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(alist.get(position).get("name"));
            viewHolder.qty.setText(alist.get(position).get("qty")+"qty");
            return convertView;
        }

        private class ViewHolder {
            TextView name, qty;
        }
    }
}
