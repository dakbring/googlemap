package com.example.dakbring.ggmaptosmsdemo.dialogs;

import com.example.dakbring.ggmaptosmsdemo.R;
import com.example.dakbring.ggmaptosmsdemo.utils.CurrentLocation.ProviderStatus;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class DialogManager {

  private static DialogManager sInstance;

  private AlertDialog mDialog;

  private Context mContext;

  public static DialogManager getInstance(Context context){
    if(sInstance == null){
      synchronized (DialogManager.class){
        if(sInstance == null){
          sInstance = new DialogManager(context);
        }
      }
    }
    return sInstance;
  }

  private DialogManager(Context context) {
    this.mContext = context;
  }

  public void showEnableProviderDialog(ProviderStatus status) {
    if(mDialog != null && mDialog.isShowing()){
      mDialog.dismiss();
    }
    switch (status) {
      case GPS_DISABLE:
        mDialog = setupDialog(R.string.main_enable_location_title,
                    R.string.main_enable_gps_message,
                    new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        break;
      case NETWORK_DISABLE:
        mDialog = setupDialog(R.string.main_enable_location_title,
            R.string.main_enable_network_message,
            new Intent(Settings.ACTION_WIFI_SETTINGS));
        break;
    }
    if(mDialog != null){
      mDialog.show();
    }
  }

  private AlertDialog setupDialog(int title, int message, final Intent intent){
    Builder builder = new Builder(mContext);
    builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            mContext.startActivity(intent);
            dialogInterface.dismiss();
          }
        })
        .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int which) {
            dialogInterface.dismiss();
          }
        });
    return builder.create();
  }
}
