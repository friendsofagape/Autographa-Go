package com.bridgeconn.autographago.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.LanguageModel;
import com.bridgeconn.autographago.models.VersionModel;
import com.bridgeconn.autographago.ui.viewholders.LanguageViewHolder;
import com.bridgeconn.autographago.ui.viewholders.VersionViewHolder;

import java.util.ArrayList;

public class LanguageVersionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Fragment mFragment;
    private LayoutInflater mLayoutInflater;
    private ArrayList<LanguageModel> mLanguageModels;
    private ArrayList<VersionModel> mVersionModels;
    private boolean mSelectBook;

    private interface ViewTypes {
        int LANGUAGE = 0;
        int VERSION = 1;
    }

    public LanguageVersionAdapter(Fragment fragment, ArrayList<LanguageModel> languageModels, ArrayList<VersionModel> versionModels, boolean selectBook) {
        mFragment = fragment;
        mLayoutInflater = LayoutInflater.from(mFragment.getContext());
        mLanguageModels = languageModels;
        mVersionModels = versionModels;
        mSelectBook = selectBook;
    }

    @Override
    public int getItemViewType(int position) {
        if (mLanguageModels != null) {
            return ViewTypes.LANGUAGE;
        }
        return ViewTypes.VERSION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ViewTypes.LANGUAGE) {
            viewHolder = new LanguageViewHolder(mLayoutInflater.inflate(
                    R.layout.item_bookmark, parent, false), mFragment, mLanguageModels);
        } else if (viewType == ViewTypes.VERSION) {
            viewHolder = new VersionViewHolder(mLayoutInflater.inflate(
                    R.layout.item_bookmark, parent, false), mFragment, mVersionModels, mSelectBook);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LanguageViewHolder) {
            ((LanguageViewHolder) holder).onBind(position);
        } else if (holder instanceof VersionViewHolder) {
            ((VersionViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mLanguageModels != null) {
            return mLanguageModels.size();
        }
        return mVersionModels.size();
    }

}