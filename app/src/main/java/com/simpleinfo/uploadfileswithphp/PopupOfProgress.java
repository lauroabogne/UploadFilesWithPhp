package com.simpleinfo.uploadfileswithphp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PopupOfProgress extends AlertDialog.Builder {

    Context mContext;
    ProgressBar mPerFileProgress;
    ProgressBar mOverallProgress;
    TextView mFileNameTextView;
    TextView mPerFileProgressTextview;
    TextView mOverallProgressTextView;
    AlertDialog mAlertDialog;

    public PopupOfProgress(Context context) {
        super(context);
        mContext = context;
        this.init();
    }

    @Override
    public AlertDialog show() {
        mAlertDialog =  super.show();

        return mAlertDialog;
    }

    private void init(){


        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.upload_progress,null,false);
        mPerFileProgress = linearLayout.findViewById(R.id.per_file_progress);
        mFileNameTextView= linearLayout.findViewById(R.id.file_name_textview);
        mPerFileProgressTextview= linearLayout.findViewById(R.id.per_file_progress_textview);
        mOverallProgress = linearLayout.findViewById(R.id.overall_progress);
        mOverallProgressTextView = linearLayout.findViewById(R.id.overall_progress_textview);
        this.setView(linearLayout);

    }


}
