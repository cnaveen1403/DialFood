package grk.impala;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
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

import grk.impala.adapter.CityAdapter;
import grk.impala.model.CityModel;
import grk.impala.util.ConstantUtil;
import grk.impala.util.PrefUtil;

/**
 * Created by Samsung on 7/16/2015.
 */
public class UpdateActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private TextView tvEmailId;
    private EditText etName, etMobileNo, etAddress;
    private Spinner spinner;
    private Button btUpdate;
    private ScrollView scrollView;
    private ProgressWheel progressWheel;

    private ArrayList<CityModel> cityModelArrayList;
    private CityAdapter cityAdapter;
    private ProgressDialog progressDialog;

    private String selCity = null;
    private int selCityId = 0;
    private boolean editFlag = false, menuFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        tvEmailId = (TextView) findViewById(R.id.tvEmailId);
        etName = (EditText) findViewById(R.id.etName);
        etMobileNo = (EditText) findViewById(R.id.etMobNo);
        etAddress = (EditText) findViewById(R.id.etAddress);
        spinner = (Spinner) findViewById(R.id.spinnerCity);
        btUpdate = (Button) findViewById(R.id.btUpdate);
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
        getSupportActionBar().setTitle(getString(R.string.update_profile));

        cityModelArrayList = new ArrayList<>();
        CityModel cm = new CityModel("0", "Select");
        cityModelArrayList.add(cm);
        cityAdapter = new CityAdapter(UpdateActivity.this, cityModelArrayList);
        spinner.setAdapter(cityAdapter);

        if (ConnectionDetector.isInternetConnected(UpdateActivity.this)) {
            profileAsync(PrefUtil.getEmailId(UpdateActivity.this));
        }else{
            progressWheel.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            showToast(getString(R.string.no_internet));
        }

        /*if (ConnectionDetector.isInternetConnected(UpdateActivity.this)) {
            cityAsync();
        }else{
            showToast(getString(R.string.no_internet));
        }*/

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.tvCityName);
                selCity = name.getText().toString();
                TextView tid = (TextView) view.findViewById(R.id.tvCityId);
                selCityId = Integer.parseInt(tid.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editFlag) {
                    String name = etName.getText().toString().trim();
                    String mobNo = etMobileNo.getText().toString().trim();
                    String address = etAddress.getText().toString().trim();

                    if (name.equals("") && name.length() == 0) {
                        showToast(getString(R.string.enter_name));
                    }else if (mobNo.equals("") && mobNo.length() == 0) {
                        showToast(getString(R.string.enter_mobile));
                    } else if (!validMobile(mobNo)) {
                        showToast(getString(R.string.valid_mobile));
                    } else if (address.equals("") && address.length() == 0) {
                        showToast(getString(R.string.enter_address));
                    } else if (selCity.equals("Select")) {
                        showToast(getString(R.string.enter_city));
                    } else {
                        if (ConnectionDetector.isInternetConnected(UpdateActivity.this)) {
                            updateAsync(PrefUtil.getEmailId(UpdateActivity.this), name, mobNo, address, selCity);
                        } else {
                            showToast(getString(R.string.no_internet));
                        }
                    }
                }
            }
        });
    }

    private boolean validMobile(String num) {
        if (num.length() < 10) {
            return false;
        }
        return true;
    }

    private void etEnabledFalse() {
        etName.setEnabled(false);
        etMobileNo.setEnabled(false);
        etAddress.setEnabled(false);
        spinner.setEnabled(false);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update, menu);
        MenuItem item = menu.findItem(R.id.action_edit);
        if (!menuFlag) {
            item.setVisible(false);
        }else{
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            editFlag = true;
            showEnabled();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEnabled() {
        etName.setEnabled(true);
        etMobileNo.setEnabled(true);
        etAddress.setEnabled(true);
        spinner.setEnabled(true);
    }

    private void profileAsync(String email) {
        Map<String, String> map = new HashMap<>();
        map.put("user_email", email);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_PROFILE,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    JSONArray menu = response.getJSONArray("user_detail");
                                    for (int j = 0; j < menu.length(); j++){
                                        JSONObject ol = menu.getJSONObject(j);
                                        String name = ol.getString("name");
                                        String mobile = ol.getString("mobile");
                                        String address = ol.getString("address");
                                        String city = ol.getString("city");

                                        //to store in shared pref
                                        PrefUtil.setName(UpdateActivity.this, name);
                                        PrefUtil.setMobile(UpdateActivity.this, mobile);
                                        PrefUtil.setAddress(UpdateActivity.this, address);
                                        PrefUtil.setCity(UpdateActivity.this, city);
                                    }
                                }
                            }
                            progressWheel.setVisibility(View.GONE);
                            if (success == 1) {
                                menuFlag = true;
                                invalidateOptionsMenu();
                                scrollView.setVisibility(View.VISIBLE);
                                etEnabledFalse();
                                tvEmailId.setText(PrefUtil.getEmailId(UpdateActivity.this));
                                etName.setText(PrefUtil.getName(UpdateActivity.this));
                                etMobileNo.setText(PrefUtil.getMobile(UpdateActivity.this));
                                etAddress.setText(PrefUtil.getAddress(UpdateActivity.this));

                                cityAsync();
                            }else {
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(UpdateActivity.this);
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

    private void updateAsync(String email, String name, String mob, String addr, String cty) {
        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put("user_name", name);
        map.put("user_email", email);
        map.put("user_mobile", mob);
        map.put("user_address", addr);
        map.put("user_city", cty);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_UPDATE,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        cancelProgressDialog();
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    JSONArray menu = response.getJSONArray("user_detail");
                                    for (int j = 0; j < menu.length(); j++){
                                        JSONObject ol = menu.getJSONObject(j);
                                        String name = ol.getString("name");
                                        String mobile = ol.getString("mobile");
                                        String address = ol.getString("address");
                                        String city = ol.getString("city");

                                        //to store in shared pref
                                        PrefUtil.setName(UpdateActivity.this, name);
                                        PrefUtil.setMobile(UpdateActivity.this, mobile);
                                        PrefUtil.setAddress(UpdateActivity.this, address);
                                        PrefUtil.setCity(UpdateActivity.this, city);
                                    }
                                }
                            }
                            if (success == 1) {
                                showToast(getString(R.string.update_success));
                                etEnabledFalse();
                                editFlag = false;
                            }else {
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

    private int getIndex(Spinner spin, String myString) {
        int index = 0;
        for (int i = 0; i < spin.getCount(); i++) {
            if (spin.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void cityAsync() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                ConstantUtil.URL_CITY,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        int success = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    JSONArray menu = response.getJSONArray("city");
                                    for (int j = 0; j < menu.length(); j++){
                                        JSONObject ol = menu.getJSONObject(j);
                                        String id = ol.getString("id");
                                        String name = ol.getString("name");
                                        CityModel cityModel = new CityModel(id, name);
                                        cityModelArrayList.add(cityModel);
                                    }
                                }
                            }
                            if (success == 1) {
                                cityAdapter.notifyDataSetChanged();
                                spinner.setSelection(getIndex(spinner, PrefUtil.getCity(UpdateActivity.this)));
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
                        Log.e("Volley Error", error.toString());
                        showToast(getString(R.string.internet_error));
                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }
}
