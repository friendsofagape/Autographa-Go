package com.bridgeconn.autographago.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bridgeconn.autographago.services.BackupService;
import com.bridgeconn.autographago.utils.Constants;
import com.bridgeconn.autographago.utils.SharedPrefs;
import com.bridgeconn.autographago.utils.UtilFunctions;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (("android.intent.action.BOOT_COMPLETED").equals(intent.getAction())) {
            // Set the alarm here if user turned on backup
            if (SharedPrefs.getBoolean(Constants.PrefKeys.BACKUP_ALARM, false)) {
                UtilFunctions.setUpAlarmForBackup(context);
            }
        } else{
            // do backup
            Intent task = new Intent(context, BackupService.class);
            task.setAction(Constants.ACTION.BACKUP);
            context.startService(task);
        }
    }
}
