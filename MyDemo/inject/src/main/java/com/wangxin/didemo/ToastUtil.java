package com.wangxin.didemo;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;

public class ToastUtil {

    private Toast mToast;

    @Inject
    public ToastUtil() {

    }

    public void show(Context context, String str) {
        if (mToast==null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        }

        mToast.setText(str);
        mToast.show();
    }
}
