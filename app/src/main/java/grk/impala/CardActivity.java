package grk.impala;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import grk.impala.util.PrefUtil;
import grk.impala.db.ItemTbl;
import grk.impala.util.ConstantUtil;

/**
 * Created by Samsung on 6/14/2015.
 */
public class CardActivity extends AppCompatActivity {

    private static final int CREDIT_CARD_LENGTH_W_SPACE = 19;
    private int cardNumberLen = 0;

    private Toolbar toolBar;
    private TextView total;
    private EditText cardNumber, expMonth, expYear, cvv2;
    private Button pay;
    private RadioGroup mode;
    private RelativeLayout card;

    private ItemTbl itemTbl;

    private String amt = null;
    private String cardNumText= null;
    private int expDateLen = 0;
    private ArrayList<HashMap<String, String>> hashMapArrayList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mode = (RadioGroup) findViewById(R.id.radioPayment);
        card = (RelativeLayout) findViewById(R.id.rlCard);
        cardNumber = (EditText) findViewById(R.id.etFirstName);
        expMonth = (EditText) findViewById(R.id.etExpiryMM);
        expYear = (EditText) findViewById(R.id.etExpiryYY);
        cvv2 = (EditText) findViewById(R.id.etCvv);
        pay = (Button) findViewById(R.id.btPay);
        total = (TextView) findViewById(R.id.tvAmount);
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
        getSupportActionBar().setTitle("Checkout");

        itemTbl = new ItemTbl(CardActivity.this);

        if (getIntent().getStringExtra("total") != null) {
            amt = getIntent().getStringExtra("total");
            total.setText(getString(R.string.rs)+amt);
        }
        if (getIntent().getSerializableExtra("orderList") != null) {
            hashMapArrayList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("orderList");
        }

        formatCreditCard();

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = mode.getCheckedRadioButtonId();
                if (selectedId == R.id.radioCod) {
                    Gson gson = new GsonBuilder().create();
                    String jsonOrder = gson.toJson(hashMapArrayList);
                    orderAsync(PrefUtil.getEmailId(CardActivity.this), jsonOrder, amt);
                } else {
                    startActivity(new Intent(CardActivity.this, PaymentActivity.class));
                }
            }
        });

        mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //RadioButton rb = (RadioButton) group.findViewById(checkedId);
                //rb.getText().toString().equals(getString(R.string.radio_card))
                /*if (checkedId == R.id.radioCard) {
                    card.setVisibility(View.VISIBLE);
                } else {
                    card.setVisibility(View.GONE);
                }*/
            }
        });
    }

    public void formatCreditCard() {
        cardNumber.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardNumText = cardNumber.getText().toString();
                cardNumberLen = cardNumber.getText().length();
                if ((cardNumberLen == 5 || cardNumberLen == 10 || cardNumberLen == 15) && !(String.valueOf(cardNumber.getText().toString().charAt(cardNumberLen - 1))
                        .equals(" "))) {
                    cardNumber.setText(new StringBuilder(cardNumText).insert(cardNumText.length() - 1, " ").toString());
                    cardNumber.setSelection(cardNumber.getText().length());
                }
                if (cardNumText.endsWith(" ")) {
                    return;
                }
                if ((cardNumberLen == CREDIT_CARD_LENGTH_W_SPACE)) {
                    cardNumText = cardNumber.getText().toString();
                    cardNumText.replace(" ", "");
                } else {
                    cardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(CardActivity.this);
        progressDialog.setMessage(getString(R.string.processing));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void orderAsync(String email, String order, String total) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(Calendar.getInstance().getTime());
        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put("user_email", email);
        map.put("user_order", order);
        map.put("order_total", total);
        map.put("order_date", currentDate);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantUtil.URL_ORDER,
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
                                itemTbl.deleteAllItem();
                                PrefUtil.setSetOrder(CardActivity.this, false);
                                showToast("Successfully Ordered!!!");
                                setResult(RESULT_OK);
                                finish();
                            }else if (success == 0) {
                                showToast(getString(R.string.something_went));
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

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
