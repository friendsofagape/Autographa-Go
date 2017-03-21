package com.bridgeconn.autographago.ui.viewholders;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.ui.activities.SettingsActivity;

import java.util.List;

public class DownloadLanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private Activity mActivity;
    private List<String> mLanguages;
    private Dialog mDialog;

    public DownloadLanguageViewHolder(View itemView, Activity activity, List<String> languages, Dialog dialog) {
        super(itemView);
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);

        mActivity = activity;
        mLanguages = languages;
        mDialog = dialog;
    }

    public void onBind(int position) {
        mTvBookName.setText(mLanguages.get(position));
        mTvBookName.setOnClickListener(this);
        mTvBookName.setTag(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                mDialog.dismiss();
                ((SettingsActivity) mActivity).getAvailableListOfVersions(mLanguages.get(position));
                break;
            }
        }
    }
}