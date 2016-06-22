package com.wangxin.didemo.activity;

import android.os.Bundle;

import com.wangxin.didemo.AppApplication;
import com.wangxin.didemo.R;
import com.wangxin.didemo.ToastUtil;
import com.wangxin.didemo.inject.component.DaggerActivityComponent;
import com.wangxin.didemo.inject.module.ActivityModule;

import javax.inject.Inject;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Inject
    ToastUtil toastUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeInject();
    }

    void initializeInject() {
        DaggerActivityComponent.builder()
                .applicationComponent(((AppApplication) getApplication()).getComponent())
                .activityModule(new ActivityModule(this))
                .build().injectActivity(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    int index = 0;
    @OnClick(R.id.btn_click)
    public void clickme() {
        toastUtil.show(this, "index:" + index++);
    }

}
