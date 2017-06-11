package com.bridgeconn.autographago.ui.viewholders;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bridgeconn.autographago.R;
import com.bridgeconn.autographago.models.AutographaGoBackup;
import com.bridgeconn.autographago.ui.activities.BackupActivity;
import com.google.android.gms.drive.DriveId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BackupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTvTime, mTvType;
    private Context mContext;
    private View mView;
    private ArrayList<AutographaGoBackup> mBackupModels;

    public BackupViewHolder(View itemView, Context context, ArrayList<AutographaGoBackup> backupModels) {
        super(itemView);
        mView = itemView;
        mTvTime = (TextView) itemView.findViewById(R.id.item_history_time);
        mTvType = (TextView) itemView.findViewById(R.id.item_history_type);

        mContext = context;
        mBackupModels = backupModels;
    }

    public void onBind(final int position) {
        AutographaGoBackup model = mBackupModels.get(position);
        String modified = formatDate(model.getModifiedDate());
        String size = Formatter.formatFileSize(mContext, model.getBackupSize());

        mTvType.setText(size);
        mTvTime.setText(modified);

        mView.setOnClickListener(this);
        mView.setTag(position);
    }

    private String formatDate(Date date) {
        DateFormat finalDataFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat finalTimeFormat;

        if (android.text.format.DateFormat.is24HourFormat(mContext)) {
            finalTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            finalTimeFormat = new SimpleDateFormat("hh:mm a");
        }

        String finalData = finalDataFormat.format(date);
        String finalTime = finalTimeFormat.format(date);
        return finalData + " " + finalTime;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_history: {
                int position = (int) v.getTag();

                AutographaGoBackup model = mBackupModels.get(position);
                final DriveId driveId = model.getDriveId();
                String modified = formatDate(model.getModifiedDate());
                String size = Formatter.formatFileSize(mContext, model.getBackupSize());

                // Show custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_restore);
                TextView createdTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_created);
                TextView sizeTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_size);
                Button restoreButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_restore);
                Button cancelButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_cancel);

                createdTextView.setText(modified);
                sizeTextView.setText(size);

                restoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BackupActivity) mContext).downloadFromDrive(driveId.asDriveFile());
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                break;
            }
        }
    }
}
