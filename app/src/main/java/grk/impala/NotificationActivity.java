package grk.impala;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Samsung on 12/8/2015.
 */
public class NotificationActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private ListView listView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.action_notification));

        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayList.add("New Offer! Buy 2 parcel meals and get 1 absolutely free!! Limited time only!!!");
        notificationAdapter = new NotificationAdapter(NotificationActivity.this, arrayList);
        listView.setAdapter(notificationAdapter);
    }

    public class NotificationAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<String> arrayList;
        private Context context;

        public NotificationAdapter(Context context, ArrayList<String> list) {
            this.context = context;
            arrayList = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.notification_item, null);
                viewHolder = new ViewHolder();

                viewHolder.text = (TextView) convertView.findViewById(R.id.tvText);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.text.setText(arrayList.get(position));

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView text;
    }
}
