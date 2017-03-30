package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import com.bridgeconn.autographago.ormutils.Mapper;
import com.bridgeconn.autographago.ormutils.Specification;
import com.bridgeconn.autographago.ui.activities.HomeActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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
        String text = spinnerModels.get(position).getLanguageName() + "  " + spinnerModels.get(position).getVersionCode();
        if (position == spinnerModels.size() - 1) {
            category.setTypeface(null, Typeface.BOLD);
        }
        category.setText(text);
        ImageView delete = (ImageView) convertView.findViewById(R.id.iv_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AutographaRepository<VersionModel>().remove(new AllSpecifications.VersionModelByCode
                        (spinnerModels.get(position).getLanguageCode(), spinnerModels.get(position).getVersionCode()));
                final Realm realm = Realm.getDefaultInstance();
                ArrayList<LanguageModel> results = query(realm, new AllSpecifications.LanguageModelByCode(spinnerModels.get(position).getLanguageCode()), new AllMappers.LanguageMapper());
                if (results.size() > 0) {
                    if (results.get(0).getVersionModels().size() == 0) {
                        new AutographaRepository<LanguageModel>().remove(new AllSpecifications.LanguageModelByCode(spinnerModels.get(position).getLanguageCode()));
                    }
                }
                realm.close();
                spinnerModels.remove(position);
                notifyDataSetChanged();
            }
        });
        if (position == spinnerModels.size() - 1) {
            delete.setVisibility(View.GONE);
        } else if (spinnerModels.get(position).getLanguageCode().equalsIgnoreCase("ENG") && (spinnerModels.get(position).getVersionCode().equalsIgnoreCase(Constants.VersionCodes.ULB)) || spinnerModels.get(position).getVersionCode().equalsIgnoreCase(Constants.VersionCodes.UDB)) {
            delete.setVisibility(View.GONE);
        } else if (position == ((HomeActivity) mContext).getSelectedSpinnerPosition()) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public ArrayList<LanguageModel> query(Realm realm, Specification<LanguageModel> specification, Mapper<LanguageModel, LanguageModel> mapper) {
        RealmResults<LanguageModel> realmResults = specification.generateResults(realm);
        ArrayList<LanguageModel> resultsToReturn = new ArrayList<>();
        for (LanguageModel result : realmResults) {
            resultsToReturn.add(mapper.map(result));
        }
        return resultsToReturn;
    }
}