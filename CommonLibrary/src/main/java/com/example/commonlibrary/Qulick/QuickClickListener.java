package com.example.commonlibrary.Qulick;

import android.view.View;

public abstract class QuickClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        onNoDoubleClick(view);
    }
    protected  abstract  void onNoDoubleClick(View v);
}
