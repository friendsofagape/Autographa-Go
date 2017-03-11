package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.SpinnerModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ormutils.AllMappers;
import com.bridgeconn.autographago.ormutils.AllSpecifications;
import com.bridgeconn.autographago.ormutils.AutographaRepository;
import com.bridgeconn.autographago.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<SpinnerModel> spinnerModels;

    public SpinnerAdapter(Context context, List<SpinnerModel> objects) {
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
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_dropdown_item, parent, false);
        TextView category = (TextView) convertView.findViewById(R.id.text1);
        category.setText(spinnerModels.get(position).getLanguageName() + "  " + spinnerModels.get(position).getVersionCode());
        ImageView delete = (ImageView) convertView.findViewById(R.id.iv_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AutographaRepository<VersionModel>().remove(new AllSpecifications.VersionModelByCode
                        (spinnerModels.get(position).getLanguageCode(), spinnerModels.get(position).getVersionCode()));
                ArrayList<LanguageModel> results = new AutographaRepository<LanguageModel>().query(new AllSpecifications.LanguageModelByCode(spinnerModels.get(position).getLanguageCode()), new AllMappers.LanguageMapper());
                if (results.size() > 0) {
                    if (results.get(0).getVersionModels().size() == 0) {
                        new AutographaRepository<LanguageModel>().remove(new AllSpecifications.LanguageModelByCode(spinnerModels.get(position).getLanguageCode()));
                    }
                }
                spinnerModels.remove(position);
                notifyDataSetChanged();
            }
        });
        if (spinnerModels.get(position).getLanguageCode().equals("ENG")) {
            delete.setVisibility(View.GONE);
        } else if (position == ((HomeActivity) mContext).getSelectedSpinnerPosition()) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}