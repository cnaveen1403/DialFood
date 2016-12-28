package grk.impala.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import grk.impala.model.CityModel;
import grk.impala.R;

/**
 * Created by Samsung on 7/17/2015.
 */

public class CityAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private ArrayList<CityModel> alist;
    private Context context;

    public CityAdapter(Context context, ArrayList<CityModel> list){
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
        return alist.get(position).getCity_name();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.city_layout, null);
            viewHolder = new ViewHolder();

            viewHolder.cid = (TextView) convertView.findViewById(R.id.tvCityId);
            viewHolder.cname = (TextView) convertView.findViewById(R.id.tvCityName);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.cid.setText(alist.get(position).getCity_id());
        viewHolder.cname.setText(alist.get(position).getCity_name());
        return convertView;
    }

    private static class ViewHolder {
        TextView cid;
        TextView cname;
    }
}
