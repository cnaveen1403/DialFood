package grk.impala;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import grk.impala.util.ConstantUtil;
import grk.impala.util.PrefUtil;
import grk.impala.model.HistoryModel;

/**
 * Created by Samsung on 7/15/2015.
 */
public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolBar;
    private ProgressWheel progressWheel;
    private LinearLayout noHistory;

    private ArrayList<HistoryModel> arrayList;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        noHistory = (LinearLayout) findViewById(R.id.llNoHistory);
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        listView = (ListView) findViewById(R.id.listView);
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
        getSupportActionBar().setTitle(getString(R.string.order_history));

        arrayList = new ArrayList<>();
        if (ConnectionDetector.isInternetConnected(HistoryActivity.this)) {
            historyAsync(PrefUtil.getEmailId(HistoryActivity.this));
        }else{
            progressWheel.setVisibility(View.GONE);
            showToast(getString(R.string.no_internet));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<HashMap<String, String>> hashMaps = arrayList.get(position).getHashMapArrayList();
                showItemDialog(hashMaps);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void historyAsync(String email) {
        Map<String, String> map = new HashMap<>();
        map.put("user_email", email);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_SELECT_HISTORY,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    arrayList.clear();
                                    JSONArray menu = response.getJSONArray("order");
                                    for (int j = 0; j < menu.length(); j++){
                                        JSONObject ol = menu.getJSONObject(j);
                                        String id = ol.getString("order_id");
                                        String dat = ol.getString("order_date");
                                        String total = ol.getString("total");
                                        int status = ol.getInt("status");

                                        HistoryModel historyModel = new HistoryModel();
                                        historyModel.setId(id);
                                        historyModel.setDat(dat);
                                        historyModel.setTotal(total);
                                        if (status == 1) {
                                            historyModel.setStatus("Booked");
                                        } else {
                                            historyModel.setStatus("Delivered");
                                        }

                                        ArrayList<HashMap<String, String>> modelArrayList = new ArrayList<>();
                                        JSONArray item = ol.getJSONArray("item");
                                        for (int i = 0; i < item.length(); i++) {
                                            JSONObject json = item.getJSONObject(i);
                                            String name = json.getString("name");
                                            String qty = json.getString("qty");
                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("name", name);
                                            hashMap.put("qty", qty);
                                            modelArrayList.add(hashMap);
                                            historyModel.setHashMapArrayList(modelArrayList);
                                        }
                                        arrayList.add(historyModel);
                                    }
                                }
                            }
                            progressWheel.setVisibility(View.GONE);
                            if (success == 1) {
                                listView.setVisibility(View.VISIBLE);
                                historyAdapter = new HistoryAdapter(HistoryActivity.this, arrayList);
                                listView.setAdapter(historyAdapter);
                            } else if (success == 0) {
                                noHistory.setVisibility(View.VISIBLE);
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


    private void showItemDialog(ArrayList<HashMap<String, String>> hash) {
        View convertView = getLayoutInflater().inflate(R.layout.listview_layout, null);
        ListView lv = (ListView) convertView.findViewById(R.id.listView);
        DetailAdapter detailAdapter = new DetailAdapter(HistoryActivity.this, hash);
        lv.setAdapter(detailAdapter);
        new AlertDialog.Builder(this, R.style.MyAlertDialog)
                .setView(convertView)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public class HistoryAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ViewHolder viewHolder;
        private ArrayList<HistoryModel> alist;
        private Context context;
        String ans = "";

        public HistoryAdapter(Context context, ArrayList<HistoryModel> list){
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
                convertView = inflater.inflate(R.layout.history_item, null);
                viewHolder = new ViewHolder();

                viewHolder.dat = (TextView) convertView.findViewById(R.id.tvOrderDate);
                viewHolder.total = (TextView) convertView.findViewById(R.id.tvTotal);
                viewHolder.status = (TextView) convertView.findViewById(R.id.tvStatus);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.dat.setText(alist.get(position).getDat());
            viewHolder.total.setText(getString(R.string.rs)+alist.get(position).getTotal());
            if (alist.get(position).getStatus().equalsIgnoreCase("Booked")) {
                viewHolder.status.setTextColor(getResources().getColor(R.color.green));
            }else{
                viewHolder.status.setTextColor(getResources().getColor(R.color.primary));
            }
            viewHolder.status.setText(alist.get(position).getStatus());

            return convertView;
        }

        private class ViewHolder {
            TextView dat, total, status;
        }
    }

    public String dateConversion(String inDate){
        String outDate = null;
        SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        SimpleDateFormat output = new SimpleDateFormat("MMM d yyyy hh:mm a");
        try {
            Date date = input.parse(inDate);   // parse input
            outDate = output.format(date);    // format output
        } catch (ParseException e) {
            Log.e("Date Exception", e.toString());
        }
        return outDate;
    }

    public class DetailAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ViewHolder viewHolder;
        private ArrayList<HashMap<String, String>> alist;
        private Context context;

        public DetailAdapter(Context context, ArrayList<HashMap<String, String>> list){
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
