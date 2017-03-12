package com.bridgeconn.autographago.ui.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.ui.viewholders.DownloadLanguageViewHolder;
import com.bridgeconn.autographago.ui.viewholders.DownloadVersionViewHolder;

import java.util.List;

public class DownloadDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mLanguages, mVersions;
    private String mLanguage;
    private Dialog mDialog;

    private interface ViewTypes {
        int LANGUAGE = 0;
        int VERSION = 1;
    }

    public DownloadDialogAdapter(Activity context, Dialog dialog, List<String> languages, List<String> versions, String language) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mLanguages = languages;
        mVersions = versions;
        mLanguage = language;
        mDialog = dialog;
    }

    @Override
    public int getItemViewType(int position) {
        if (mLanguages != null) {
            return ViewTypes.LANGUAGE;
        }
        return ViewTypes.VERSION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ViewTypes.LANGUAGE) {
            viewHolder = new DownloadLanguageViewHolder(mLayoutInflater.inflate
                    (R.layout.item_dialog, parent, false), mContext, mLanguages, mDialog);
        } else if (viewType == ViewTypes.VERSION) {
            viewHolder = new DownloadVersionViewHolder(mLayoutInflater.inflate
                    (R.layout.item_dialog, parent, false), mContext, mVersions, mDialog, mLanguage);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DownloadLanguageViewHolder) {
            ((DownloadLanguageViewHolder) holder).onBind(position);
        } else if (holder instanceof DownloadVersionViewHolder) {
            ((DownloadVersionViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mLanguages != null) {
            return mLanguages.size();
        } else {
            return mVersions.size();
        }
    }

}