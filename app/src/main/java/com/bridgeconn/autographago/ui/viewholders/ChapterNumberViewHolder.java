package com.bridgeconn.autographago.ui.viewholders;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.ui.activities.SelectChapterAndVerseActivity;
import com.bridgeconn.autographago.ui.fragments.ChapterFragment;

import java.util.ArrayList;

public class ChapterNumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvChapterNumber;
    private Fragment mFragment;
    private ArrayList<ChapterModel> mChapterModels;

    public ChapterNumberViewHolder(View itemView, Fragment fragment, ArrayList<ChapterModel> chapterModels) {
        super(itemView);
        mTvChapterNumber = (TextView) itemView.findViewById(R.id.tv_number);

        mFragment = fragment;
        mChapterModels = chapterModels;
    }

    public void onBind(final int position) {
        ChapterModel chapterModel = mChapterModels.get(position);
        if (chapterModel.isSelected()) {
            mTvChapterNumber.setBackgroundColor(mFragment.getResources().getColor(R.color.black_40, null));
        } else {
            mTvChapterNumber.setBackground(mFragment.getResources().getDrawable(R.drawable.number_background, null));
        }
        mTvChapterNumber.setText(chapterModel.getChapterNumber() + "");
        mTvChapterNumber.setTag(position);
        mTvChapterNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_number: {
                int position = (int) v.getTag();
                if (mFragment.getActivity() instanceof SelectChapterAndVerseActivity) {
                    ((ChapterFragment)mFragment).setSelected(position);
                    ((SelectChapterAndVerseActivity) mFragment.getActivity()).openPage(1, position+1);
                }
                break;
            }
        }
    }
}