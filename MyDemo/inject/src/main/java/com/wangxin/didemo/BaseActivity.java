package com.wangxin.didemo;

import android.app.Activity;
import android.os.Bundle;

import javax.inject.Inject;

/**
 * Created by wangxin on 16/6/13.
 */
public abstract class BaseActivity extends Activity {

    @Inject
    ToastUtil toastUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.getApplicationComponent().inject(this);
    }

//    protected ApplicationComponent getApplicationComponent() {
//        return ((BaseApplication)getApplication()).getApplicationComponent();
//    }
//
//    protected ActivityModule getActivityModule() {
//        return new ActivityModule(this);
//    }
}
