package com.bridgeconn.autographago.ui.viewholders;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.ui.activities.SelectLanguageAndVersionActivity;
import com.bridgeconn.autographago.ui.fragments.LanguageFragment;

import java.util.ArrayList;

public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapterNumber;
    private Fragment mFragment;
    private ArrayList<LanguageModel> mLanguageModelArrayList;

    public LanguageViewHolder(View itemView, Fragment fragment, ArrayList<LanguageModel> languageModelArrayList) {
        super(itemView);
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_book_name);

        mFragment = fragment;
        mLanguageModelArrayList = languageModelArrayList;
    }

    public void onBind(final int position) {
        LanguageModel languageModel = mLanguageModelArrayList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (languageModel.isSelected()) {
                mTvChapterNumber.setBackgroundColor(mFragment.getResources().getColor(R.color.black_40, null));
            } else {
                mTvChapterNumber.setBackgroundColor(0);
            }
        }
        mTvChapterNumber.setText(languageModel.getLanguageName());
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_book_name: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectLanguageAndVersionActivity) {
                    ((LanguageFragment) mFragment).setSelected(position);
                    ((SelectLanguageAndVersionActivity) mFragment.getActivity()).openVersionPage(mLanguageModelArrayList.get(position).getLanguageCode());
                }
                break;
            }
        }
    }
}