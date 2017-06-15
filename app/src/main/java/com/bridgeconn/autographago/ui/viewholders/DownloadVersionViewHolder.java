package com.bridgeconn.autographago.ui.viewholders;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.ui.activities.BookActivity;
import com.bridgeconn.autographago.ui.activities.SettingsActivity;

import java.util.List;

public class DownloadVersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvBookName;
    private Activity mActivity;
    private List<String> mVersions;
    private Dialog mDialog;
    private String mLanguage;

    public DownloadVersionViewHolder(View itemView, Activity activity, List<String> versions, Dialog dialog, String language) {
        super(itemView);
        mTvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);

        mActivity = activity;
        mVersions = versions;
        mDialog = dialog;
        mLanguage = language;
    }

    public void onBind(int position) {
        mTvBookName.setText(mVersions.get(position));
        mTvBookName.setOnClickListener(this);
        mTvBookName.setTag(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                mDialog.dismiss();
                if (mActivity instanceof SettingsActivity) {
                    ((SettingsActivity) mActivity).getMetaData(mLanguage, mVersions.get(position));
                } else if (mActivity instanceof BookActivity) {
                    ((BookActivity) mActivity).changeLanguageVersionOfBook(mLanguage, mVersions.get(position));
                }
                break;
            }
        }
    }
}