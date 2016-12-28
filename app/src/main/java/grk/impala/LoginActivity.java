package grk.impala;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import grk.impala.util.ConstantUtil;
import grk.impala.util.PrefUtil;
import grk.impala.util.RuntimePermissionsActivity;

public class LoginActivity extends RuntimePermissionsActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_PHONE_STATE_PERMISSIONS = 20;

    private Button btnLogin, btnSignUp, btnForgot;
    private TextInputLayout tilEmailID, tilPassword;
    private EditText etEmailId, etPassword;

    private ProgressDialog progressDialog;
    private AsyncTask<Void, Void, Void> mRegisterTask = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilEmailID = (TextInputLayout) findViewById(R.id.tilEmailId);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        etEmailId = (EditText) findViewById(R.id.etEmailId);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btLogin);
        btnSignUp = (Button) findViewById(R.id.btSignUp);
        btnForgot = (Button) findViewById(R.id.btForgot);

        LoginActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.READ_PHONE_STATE}, R.string
                        .runtime_permissions_txt
                , REQUEST_PHONE_STATE_PERMISSIONS);

        /*btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailId.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                if (email.equals("") && email.length() == 0) {
                    showToast(getString(R.string.enter_email));
                } else if (!isEmailValid(email)) {
                    showToast(getString(R.string.valid_email));
                } else if (pass.equals("") && pass.length() == 0) {
                    showToast(getString(R.string.enter_password));
                } else {
                    if (ConnectionDetector.isInternetConnected(LoginActivity.this)) {
                        processLogin (email, pass);

                    } else {
                        showToast(getString(R.string.no_internet));
                    }
                }
            }
        });*/

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailId.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                if (email.equals("") && email.length() == 0) {
                    showToast(getString(R.string.enter_email));
                } else if (!isEmailValid(email)) {
                    showToast(getString(R.string.valid_email));
                } else if (pass.equals("") && pass.length() == 0) {
                    showToast(getString(R.string.enter_password));
                } else {
                    if (ConnectionDetector.isInternetConnected(LoginActivity.this)) {
                        processLogin (email, pass);

                    } else {
                        showToast(getString(R.string.no_internet));
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void onPermissionsGranted(final int requestCode) {
        getDevId();

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                //Log.i("GCM", "Already Registered");
            } else {
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        ServerUtilities.register(context, regId, PrefUtil.getDeviceId(LoginActivity.this));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void getDevId() {
        final TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            PrefUtil.setDeviceId(LoginActivity.this, mTelephony.getDeviceId());
        } else {
            String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            PrefUtil.setDeviceId(LoginActivity.this, deviceId);
        }
    }

    private boolean isEmailValid(String email) {
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.verify_credential));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void processLogin (String email, String pass){
        String [] requestedPermissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale) {
                Snackbar.make(findViewById(android.R.id.content), R.string.runtime_permissions_txt,
                        Snackbar.LENGTH_INDEFINITE).setAction("GRANT",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_PERMISSIONS);
            }
        } else {
            loginAsync(email, pass, PrefUtil.getDeviceId(LoginActivity.this));
        }
    }

    private void loginAsync(String email, String pass, String dev) {
        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put("user_email", email);
        map.put("user_pass", pass);
        map.put("user_device", dev);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_LOGIN,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        cancelProgressDialog();
                        int success = -1;
                        int userType = -1;
                        try {
                            if (response != null) {
                                success = response.getInt("success");
                                if (success == 1) {
                                    JSONArray menu = response.getJSONArray("user_detail");
                                    for (int j = 0; j < menu.length(); j++) {
                                        JSONObject ol = menu.getJSONObject(j);
                                        String name = ol.getString("name");
                                        String email = ol.getString("email");
                                        String mobile = ol.getString("mobile");
                                        String address = ol.getString("address");
                                        String city = ol.getString("city");
                                        userType = ol.getInt("type");

                                        //to store in shared pref
                                        PrefUtil.setName(LoginActivity.this, name);
                                        PrefUtil.setEmailId(LoginActivity.this, email);
                                        PrefUtil.setUserType(LoginActivity.this, userType);
                                        PrefUtil.setMobile(LoginActivity.this, mobile);
                                        PrefUtil.setAddress(LoginActivity.this, address);
                                        PrefUtil.setCity(LoginActivity.this, city);
                                    }
                                }
                            }
                            if (success == 1) {
                                if (userType == 2 || userType == 3 || userType == 4) {
                                    PrefUtil.setLoggedIn(LoginActivity.this, true);
                                    startActivity(new Intent(LoginActivity.this, StatusActivity.class));
                                    finish();
                                } else {
                                    PrefUtil.setLoggedIn(LoginActivity.this, true);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } /*else {
                                    PrefUtil.setLoggedIn(LoginActivity.this, true);
                                    finish();
                                }*/
                            } else if (success == 0) {
                                showToast(getString(R.string.invalid_user));
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
}


