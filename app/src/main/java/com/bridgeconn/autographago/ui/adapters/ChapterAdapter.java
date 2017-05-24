package com.bridgeconn.autographago.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.ChapterModel;
import com.bridgeconn.autographago.ui.viewholders.CopyrightViewHolder;
import com.bridgeconn.autographago.ui.viewholders.VerseViewHolder;
import com.bridgeconn.autographago.utils.Constants;

import java.util.ArrayList;

public class ChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ChapterModel> mChapterModels;
    private Constants.FontSize mFontSize;
    private String mVersionName;
    private String mLicense;
    private int mYear;

    private interface ViewTypes {
        int VERSE = 0;
        int DUMMY = 1;
        int COPYRIGHT = 2;
    }

    public ChapterAdapter(Activity context, ArrayList<ChapterModel> chapterModels, Constants.FontSize fontSize,
                          String versionName, String license, int year) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mChapterModels = chapterModels;
        mFontSize = fontSize;
        mVersionName = versionName;
        mLicense = license;
        mYear = year;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ViewTypes.DUMMY;
        } else if (position == getItemCount() - 2) {
            return ViewTypes.COPYRIGHT;
        }
        return ViewTypes.VERSE;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ViewTypes.VERSE) {
            viewHolder = new VerseViewHolder(mLayoutInflater.inflate
                    (R.layout.item_verse, parent, false), mContext, mChapterModels, mFontSize);
        } else if (viewType == ViewTypes.DUMMY) {
            viewHolder = new RecyclerView.ViewHolder(mLayoutInflater.inflate
                    (R.layout.item_dummy, parent, false)) {
            };
        } else if (viewType == ViewTypes.COPYRIGHT) {
            viewHolder = new CopyrightViewHolder(mLayoutInflater.inflate
                    (R.layout.item_copyright, parent, false), mContext, mFontSize, mVersionName, mLicense, mYear);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VerseViewHolder) {
            ((VerseViewHolder) holder).onBind(position);
        } else if (holder instanceof CopyrightViewHolder) {
            ((CopyrightViewHolder) holder).onBind();
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        for (int k=0; k<mChapterModels.size(); k++) {
            for (int i=0; i<mChapterModels.get(k).getVerseComponentsModels().size(); i++) {
                if (i==0) {
                    size++;
                } else {
                    if (mChapterModels.get(k).getVerseComponentsModels().get(i).getVerseNumber().equals(
                            mChapterModels.get(k).getVerseComponentsModels().get(i-1).getVerseNumber())) {
                        continue;
                    } else {
                        size++;
                    }
                }
            }
        }
        return size + 2;
    }

}