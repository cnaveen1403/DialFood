package grk.impala;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import grk.impala.adapter.CityAdapter;
import grk.impala.model.CityModel;
import grk.impala.util.ConstantUtil;
import grk.impala.util.PrefUtil;

/**
 * Created by Samsung on 7/5/2015.
 */
public class SignUpActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private EditText etFirstName, etEmailId, etPassword, etMobileNo, etAddress;
    private Spinner spinner;
    private Button btRegister;

    private String selCity = null;
    private ArrayList<CityModel> cityModelArrayList;
    private CityAdapter cityAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etFirstName = (EditText) findViewById(R.id.etName);
        etEmailId = (EditText) findViewById(R.id.etEmailId);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etMobileNo = (EditText) findViewById(R.id.etMobNo);
        etAddress = (EditText) findViewById(R.id.etAddress);
        spinner = (Spinner) findViewById(R.id.spinnerCity);
        btRegister = (Button) findViewById(R.id.btRegister);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.registration));

        cityModelArrayList = new ArrayList<>();
        CityModel cm = new CityModel("0", "Select");
        cityModelArrayList.add(cm);
        cityAdapter = new CityAdapter(SignUpActivity.this, cityModelArrayList);
        spinner.setAdapter(cityAdapter);

        if (ConnectionDetector.isInternetConnected(SignUpActivity.this)) {
            cityAsync();
        }else{
            showToast(getString(R.string.no_internet));
        }

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etFirstName.getText().toString().trim();
                String mailId = etEmailId.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String mobNo = etMobileNo.getText().toString().trim();
                String address = etAddress.getText().toString().trim();

                if (name.equals("") && name.length() == 0) {
                    showToast(getString(R.string.enter_name));
                } else if (mailId.equals("") && mailId.length() == 0) {
                    showToast(getString(R.string.enter_email));
                } else if (!isEmailValid(mailId)) {
                    showToast(getString(R.string.valid_email));
                } else if (password.equals("") && password.length() == 0) {
                    showToast(getString(R.string.enter_password));
                } else if (!validPassword(password)) {
                    showToast(getString(R.string.valid_password));
                } else if (mobNo.equals("") && mobNo.length() == 0) {
                    showToast(getString(R.string.enter_mobile));
                } else if (!validMobile(mobNo)) {
                    showToast(getString(R.string.valid_mobile));
                } else if (address.equals("") && address.length() == 0) {
                    showToast(getString(R.string.enter_address));
                } else if (selCity.equals("Select")) {
                    showToast(getString(R.string.enter_city));
                } else {
                    if (ConnectionDetector.isInternetConnected(SignUpActivity.this)) {
                        registerAsync(name, mailId, password, mobNo, address, selCity, PrefUtil.getDeviceId(SignUpActivity.this));
                    } else {
                        showToast(getString(R.string.no_internet));
                    }
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.tvCityName);
                selCity = name.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private boolean isEmailValid(String email){
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private boolean validMobile(String num) {
        if (num.length() < 10) {
            return false;
        }
        return true;
    }

    private boolean validPassword(String num) {
        if (num.length() < 6) {
            return false;
        }
        return true;
    }

    private void registerAsync(String name, String email, String pass, String mob, String addr, String cty, String dev) {
        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put("user_name", name);
        map.put("user_email", email);
        map.put("user_pass", pass);
        map.put("user_mobile", mob);
        map.put("user_address", addr);
        map.put("user_city", cty);
        map.put("user_device", dev);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_REGISTER,
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
                                        String email = ol.getString("email");
                                        String mobile = ol.getString("mobile");
                                        String address = ol.getString("address");
                                        String city = ol.getString("city");

                                        //to store in shared pref
                                        PrefUtil.setName(SignUpActivity.this, name);
                                        PrefUtil.setEmailId(SignUpActivity.this, email);
                                        PrefUtil.setMobile(SignUpActivity.this, mobile);
                                        PrefUtil.setAddress(SignUpActivity.this, address);
                                        PrefUtil.setCity(SignUpActivity.this, city);
                                    }
                                }
                            }

                            if (success == 1) {
                                PrefUtil.setLoggedIn(SignUpActivity.this, true);
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            }else if (success == 2) {
                                showToast(getString(R.string.already_register));
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(SignUpActivity.this);
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

    private void cityAsync() {
        showProgressDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                ConstantUtil.URL_CITY,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        cancelProgressDialog();
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
                            } else {
                                showToast(getString(R.string.internet_error));
                            }
                        } catch (JSONException e) {
                            cancelProgressDialog();
                            showToast(getString(R.string.internet_error));
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
}
