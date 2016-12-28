package grk.impala;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import grk.impala.model.MenuModel;
import grk.impala.util.PrefUtil;
import grk.impala.db.ItemTbl;
import grk.impala.model.ItemModel;

/**
 * Created by Samsung on 6/11/2015.
 */
public class MenuFragment extends Fragment{

    public static final String MENU = "menu";

    private ListView lvMenuItem;

    private MenuAdapter menuAdapter;
    private ArrayList<ItemModel> arrayList;
    private ArrayList<MenuModel> menuModelArrayList;
    private ItemTbl itemTbl;

    public static MenuFragment newInstance(ArrayList<ItemModel> list) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(MENU, list);
        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = getArguments().getParcelableArrayList(MENU);
        itemTbl = new ItemTbl(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        lvMenuItem = (ListView) view.findViewById(R.id.listView);

        menuModelArrayList = new ArrayList<>();
        if (arrayList.size() != 0) {
            for (ItemModel itemModel: arrayList) {
                MenuModel menuModel = new MenuModel();
                menuModel.setId(itemModel.getId());
                menuModel.setItemId(itemModel.getItemId());
                menuModel.setName(itemModel.getItemName());
                menuModel.setRate(itemModel.getItemRate());
                menuModel.setAvl(itemModel.getAvl());
                menuModel.setTnUrl(itemModel.getTnUrl());
                menuModel.setImgUrl(itemModel.getImgUrl());
                menuModel.setInfo(itemModel.getProductInfo());
                menuModel.setQty(0);
                menuModelArrayList.add(menuModel);
            }
        }
        menuAdapter = new MenuAdapter(getActivity(), menuModelArrayList);
        lvMenuItem.setAdapter(menuAdapter);

        lvMenuItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvItemId = (TextView) view.findViewById(R.id.tvItemId);
                String itemId = tvItemId.getText().toString();
                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                String itemName = tvName.getText().toString();
                TextView tvRate = (TextView) view.findViewById(R.id.tvRateHide);
                String itemRate = tvRate.getText().toString();
                TextView tvMenu = (TextView) view.findViewById(R.id.tvMenuId);
                String menuId = tvMenu.getText().toString();
                TextView tvUrl = (TextView) view.findViewById(R.id.tvMenuImgUrl);
                String imageUrl = tvUrl.getText().toString();
                TextView tvInfo = (TextView) view.findViewById(R.id.tvProductInfo);
                String info = tvInfo.getText().toString();

                Intent in = new Intent(getActivity(), MenuDetailActivity.class);
                in.putExtra("position", position);
                in.putExtra("itemId", itemId);
                in.putExtra("itemName", itemName);
                in.putExtra("itemRate", itemRate);
                in.putExtra("menuId", menuId);
                in.putExtra("Image", imageUrl);
                in.putExtra("info", info);
                in.putExtra("qty", menuModelArrayList.get(position).getQty());
                startActivityForResult(in, 1000);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                String total = data.getStringExtra("totalQty");
                int pos = data.getIntExtra("position", -1);

                MenuModel temp = new MenuModel();
                temp.setId(menuModelArrayList.get(pos).getId());
                temp.setItemId(menuModelArrayList.get(pos).getItemId());
                temp.setName(menuModelArrayList.get(pos).getName());
                temp.setRate(menuModelArrayList.get(pos).getRate());
                temp.setTnUrl(menuModelArrayList.get(pos).getTnUrl());
                temp.setImgUrl(menuModelArrayList.get(pos).getImgUrl());
                temp.setInfo(menuModelArrayList.get(pos).getInfo());
                temp.setQty(Integer.parseInt(total));
                menuModelArrayList.set(pos, temp);
                menuAdapter.notifyDataSetChanged();
            }
        }
    }

    public class MenuAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<MenuModel> arrayList;
        private Context context;

        public MenuAdapter(Context context, ArrayList<MenuModel> list) {
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
                convertView = inflater.inflate(R.layout.menu_item, null);
                viewHolder = new ViewHolder();

                viewHolder.ivTn = (ImageView) convertView.findViewById(R.id.menuImage);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.rate = (TextView) convertView.findViewById(R.id.tvRate);
                viewHolder.rateHide = (TextView) convertView.findViewById(R.id.tvRateHide);
                viewHolder.qty = (TextView) convertView.findViewById(R.id.tvQty);
                viewHolder.tn = (TextView) convertView.findViewById(R.id.tvMenuTnUrl);
                viewHolder.img = (TextView) convertView.findViewById(R.id.tvMenuImgUrl);
                viewHolder.menuId = (TextView) convertView.findViewById(R.id.tvMenuId);
                viewHolder.itemId = (TextView) convertView.findViewById(R.id.tvItemId);
                viewHolder.info = (TextView) convertView.findViewById(R.id.tvProductInfo);
                viewHolder.avl = (TextView) convertView.findViewById(R.id.tvAvl);
                viewHolder.add = (Button) convertView.findViewById(R.id.btAdd);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Glide.with(context)
                    .load(arrayList.get(position).getTnUrl())
                    .into(viewHolder.ivTn);
            viewHolder.name.setText(arrayList.get(position).getName());
            viewHolder.rate.setText(context.getString(R.string.rs)+" "+arrayList.get(position).getRate());
            viewHolder.rateHide.setText(arrayList.get(position).getRate());
            viewHolder.tn.setText(arrayList.get(position).getTnUrl());
            viewHolder.img.setText(arrayList.get(position).getImgUrl());
            viewHolder.info.setText(arrayList.get(position).getInfo());
            viewHolder.menuId.setText(String.valueOf(arrayList.get(position).getId()));
            viewHolder.itemId.setText(String.valueOf(arrayList.get(position).getItemId()));
            viewHolder.avl.setText(String.valueOf(arrayList.get(position).getAvl()));

            //viewHolder.qty.setText(String.valueOf(arrayList.get(position).getQty()));
            if (arrayList.get(position).getQty() != 0) {
                //viewHolder.qty.setVisibility(View.VISIBLE);
                viewHolder.qty.setText("x "+String.valueOf(arrayList.get(position).getQty()));
                viewHolder.qty.setTextColor(getResources().getColor(R.color.primary));
            } else {
                viewHolder.qty.setText("");
                viewHolder.qty.setTextColor(getResources().getColor(R.color.background_window));
            }

            if (arrayList.get(position).getAvl() == 1) {
                viewHolder.add.setEnabled(true);
                viewHolder.name.setTextColor(Color.parseColor("#000000"));
            } else {
                viewHolder.add.setEnabled(false);
                viewHolder.name.setTextColor(Color.parseColor("#80000000"));
            }

            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.qty.setTextColor(getResources().getColor(R.color.primary));
                    int qty = arrayList.get(position).getQty();
                    qty++;
                    viewHolder.qty.setText("x " + String.valueOf(qty));
                    MenuModel menuModel = new MenuModel();
                    menuModel.setId(arrayList.get(position).getId());
                    menuModel.setItemId(arrayList.get(position).getItemId());
                    menuModel.setName(arrayList.get(position).getName());
                    menuModel.setRate(arrayList.get(position).getRate());
                    menuModel.setTnUrl(arrayList.get(position).getTnUrl());
                    menuModel.setImgUrl(arrayList.get(position).getImgUrl());
                    menuModel.setInfo(arrayList.get(position).getInfo());
                    menuModel.setAvl(arrayList.get(position).getAvl());
                    menuModel.setQty(qty);
                    arrayList.set(position, menuModel);

                    if (itemTbl.itemPresent(arrayList.get(position).getItemId()) == 0) {
                        ItemModel itemModel = new ItemModel();
                        itemModel.setId(arrayList.get(position).getId());
                        itemModel.setItemId(arrayList.get(position).getItemId());
                        itemModel.setItemName(arrayList.get(position).getName());
                        itemModel.setItemRate(arrayList.get(position).getRate());
                        itemModel.setTotalQty(arrayList.get(position).getQty());
                        itemTbl.insertItem(itemModel);
                    } else {
                        ItemModel itemModel = new ItemModel();
                        itemModel.setId(arrayList.get(position).getId());
                        itemModel.setItemId(arrayList.get(position).getItemId());
                        itemModel.setItemName(arrayList.get(position).getName());
                        itemModel.setItemRate(arrayList.get(position).getRate());
                        itemModel.setTotalQty(arrayList.get(position).getQty());
                        itemTbl.updateItem(itemModel);
                    }
                    PrefUtil.setSetOrder(getActivity(), true);
                }
            });
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return (arrayList.get(position).getAvl() == 1)?true:false;
        }
    }

    private static class ViewHolder {
        Button add;
        TextView name, rate, rateHide, qty, tn, img, menuId, itemId, info, avl;
        ImageView ivTn;
    }
}
