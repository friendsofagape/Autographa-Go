package com.bridgeconn.autographago.ui.viewholders;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ui.activities.SelectLanguageAndVersionActivity;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapterNumber;
    private Fragment mFragment;
    private ArrayList<VersionModel> mVersionModelArrayList;
    private boolean mSelectBook;

    public VersionViewHolder(View itemView, Fragment fragment, ArrayList<VersionModel> versionModelArrayList, boolean selectBook) {
        super(itemView);
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_book_name);

        mFragment = fragment;
        mVersionModelArrayList = versionModelArrayList;
        mSelectBook = selectBook;
    }

    public void onBind(final int position) {
        VersionModel versionModel = mVersionModelArrayList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (versionModel.isSelected()) {
                mTvChapterNumber.setBackgroundColor(mFragment.getResources().getColor(R.color.black_40, null));
            } else {
                mTvChapterNumber.setBackgroundColor(0);
            }
        }
        mTvChapterNumber.setText(versionModel.getVersionCode() + "  " + versionModel.getVersionName());
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();

                Intent output = new Intent();
                output.putExtra(Constants.Keys.LANGUAGE_CODE, ((SelectLanguageAndVersionActivity) mFragment.getActivity()).getSelectedLanguageCode());
                output.putExtra(Constants.Keys.VERSION_CODE, mVersionModelArrayList.get(position).getVersionCode());
                output.putExtra(Constants.Keys.LANGUAGE_NAME, ((SelectLanguageAndVersionActivity) mFragment.getActivity()).getSelectedLanguageName());

                mFragment.getActivity().setResult(RESULT_OK, output);
                mFragment.getActivity().finish();

                break;
            }
        }
    }
}