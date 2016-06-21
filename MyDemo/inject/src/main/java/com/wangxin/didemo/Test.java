package com.wangxin.didemo;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by wangxin on 16/6/13.
 */
public class Test {

    Context context;

    public Test(Context context) {
        this.context = context;
    }

    public void sayHello() {
        Log.d("Test", "---Hello---");
    }
}
