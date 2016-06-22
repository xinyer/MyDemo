package com.wangxin.didemo.activity;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutId();
        ButterKnife.bind(this);
    }

    public abstract int getLayoutId();
}
