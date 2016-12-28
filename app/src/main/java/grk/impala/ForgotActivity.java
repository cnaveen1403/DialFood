package grk.impala;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import grk.impala.util.ConstantUtil;

/**
 * Created by Samsung on 7/16/2015.
 */
public class ForgotActivity extends AppCompatActivity {

    private Button btSubmit, btCancel;
    private EditText etEmailId;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        etEmailId = (EditText) findViewById(R.id.etEmailId);
        btSubmit = (Button) findViewById(R.id.btSubmit);
        btCancel = (Button) findViewById(R.id.btCancel);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailId.getText().toString().trim();
                if (email.equals("") && email.length() == 0) {
                    showToast(getString(R.string.enter_email));
                }else if (!isEmailValid(email)) {
                    showToast(getString(R.string.valid_email));
                }else {
                    if (ConnectionDetector.isInternetConnected(ForgotActivity.this)) {
                        forgotAsync(email);
                    }else{
                        showToast(getString(R.string.no_internet));
                    }
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(ForgotActivity.this);
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

    private void forgotAsync(String email) {
        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put("user_email", email);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_FORGOT,
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
                                showToast(getString(R.string.password_sent));
                                finish();
                            }else if (success == 0) {
                                showToast(getString(R.string.password_invalid));
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
