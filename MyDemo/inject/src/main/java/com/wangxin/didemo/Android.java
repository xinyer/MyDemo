package com.wangxin.didemo;

import android.util.Log;

import javax.inject.Inject;

/**
 * Created by wangxin on 16/6/13.
 */
public class Android {

    int version;

    @Inject
    public Android() {
        this.version = 21;
    }

    public void getVersion() {
        Log.d("getVersion", "version=" + version);
    }
}
