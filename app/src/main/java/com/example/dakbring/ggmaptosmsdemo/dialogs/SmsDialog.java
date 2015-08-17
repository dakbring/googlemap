package com.example.dakbring.ggmaptosmsdemo.dialogs;

import com.google.android.gms.maps.model.LatLng;

import com.example.dakbring.ggmaptosmsdemo.R;
import com.example.dakbring.ggmaptosmsdemo.utils.CurrentLocation;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmsDialog extends DialogFragment {

    public final static String LOADING_STATE = "loading_state";

    @Bind(R.id.sms_phone_no)
    EditText mTxtPhoneNo;

    @Bind(R.id.coordinates_text)
    TextView mCoordinatesTxt;

    private boolean mState = false;

    private LatLng mLatLng;

    public static SmsDialog newInstance(Bundle bundle) {
        SmsDialog dialog = new SmsDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mState = getArguments().getBoolean(LOADING_STATE);
        if (mState) {
            mLatLng = new LatLng(
                    getArguments().getDouble(CurrentLocation.LATITUDE),
                    getArguments().getDouble(CurrentLocation.LONGITUDE)
            );
        }
    }

    @OnClick({R.id.success_send_btn, R.id.fail_ok_btn, R.id.success_cancel_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.success_send_btn:
                String phoneNo = mTxtPhoneNo.getText().toString();

                Uri.Builder uri = new Uri.Builder();
                uri.scheme(getString(R.string.deep_linking_scheme))
                        .authority(getString(R.string.deep_linking_host))
                        .appendPath(getString(R.string.deep_linking_map_path))
                        .appendQueryParameter(CurrentLocation.LATITUDE, String.valueOf(mLatLng.latitude))
                        .appendQueryParameter(CurrentLocation.LONGITUDE,String.valueOf(mLatLng.longitude));
                String message = uri.toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(
                            getActivity(),
                            "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                    dismiss();
                } catch (Exception e) {
                    Toast.makeText(
                            getActivity(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                break;
            case R.id.fail_ok_btn:
            case R.id.success_cancel_btn:
                dismiss();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sms, container);

        if (mState) {
            view.findViewById(R.id.dialog_success).setVisibility(View.VISIBLE);
            view.findViewById(R.id.dialog_fail).setVisibility(View.GONE);
            getDialog().setTitle(R.string.sms_success_title);
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            view.findViewById(R.id.dialog_success).setVisibility(View.GONE);
            view.findViewById(R.id.dialog_fail).setVisibility(View.VISIBLE);
            getDialog().setTitle(R.string.sms_fail_title);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if(mState){
            mCoordinatesTxt.setText(mLatLng.toString());
            mTxtPhoneNo.requestFocus();
        }
    }
}
