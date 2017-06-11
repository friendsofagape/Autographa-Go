package com.bridgeconn.autographago.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.AutographaGoBackup;
import com.bridgeconn.autographago.ui.viewholders.BackupViewHolder;

import java.util.ArrayList;

public class BackupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<AutographaGoBackup> mBackupModels;

    public BackupAdapter(Context context, ArrayList<AutographaGoBackup> backupModels) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mBackupModels = backupModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BackupViewHolder(mLayoutInflater.inflate(R.layout.item_backup, parent, false), mContext, mBackupModels);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BackupViewHolder) {
            ((BackupViewHolder) holder).onBind(position);
        }
    }

    @Override
    public int getItemCount() {
        return mBackupModels.size();
    }

}