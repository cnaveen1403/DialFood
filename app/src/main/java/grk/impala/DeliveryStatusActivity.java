package grk.impala;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import grk.impala.util.PrefUtil;

/**
 * Created by Samsung on 8/17/2015.
 */
public class DeliveryStatusActivity extends AppCompatActivity {

    public String URL_PREMIUM = null;
    private Toolbar tbTitle;
    private WebView wvContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverystatus);

        if (PrefUtil.getUserType(this) == 2) {
            URL_PREMIUM = "http://www.agrokart.com/Imp";
        } else {
            URL_PREMIUM = "http://www.agrokart.com/Imp/orderhistory.php";
        }
        wvContainer = (WebView) findViewById(R.id.wvContainer);
        wvContainer.getSettings().setJavaScriptEnabled(true);
        tbTitle = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tbTitle);
        tbTitle.setNavigationIcon(R.drawable.ic_back);
        tbTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        wvContainer.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog = null;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(DeliveryStatusActivity.this);
                    progressDialog.setMessage(getString(R.string.loading));
                    progressDialog.show();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception exception) {
                    Log.e("Delivery Exception", exception.toString());
                }
            }

        });

        wvContainer.loadUrl(URL_PREMIUM);
    }
}
