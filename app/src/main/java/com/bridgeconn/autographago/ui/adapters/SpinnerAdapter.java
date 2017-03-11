package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.SpinnerModel;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {//ArrayAdapter<SpinnerModel> {

    private Context mContext;
    private List<SpinnerModel> spinnerModels;

    public SpinnerAdapter(Context context, /* int resource, int textResourceId, */List<SpinnerModel> objects) {
//        super(context, resource, textResourceId, objects);
        mContext = context;
        spinnerModels = objects;
    }

    @Override
    public int getCount() {
        return spinnerModels.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(mContext).inflate(R.layout.item_spinner, parent, false);
        TextView category = (TextView) view.findViewById(R.id.tv_spinner_item);
        category.setText(spinnerModels.get(position).getLanguageName() + "  " + spinnerModels.get(position).getVersionCode());
        return view;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView category = (TextView) convertView.findViewById(android.R.id.text1);
        category.setText(spinnerModels.get(position).getLanguageName() + "  " + spinnerModels.get(position).getVersionCode());
        return convertView;
    }
}
