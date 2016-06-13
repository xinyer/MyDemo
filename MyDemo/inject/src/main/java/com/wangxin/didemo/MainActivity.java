package com.wangxin.didemo;

import android.app.Activity;
import android.os.Bundle;

import com.wangxin.didemo.inject.component.ActivityComponent;
import com.wangxin.didemo.inject.component.DaggerActivityComponent;
import com.wangxin.didemo.inject.module.ActivityModule;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    ActivityComponent component;

    @Inject
    ToastUtil toastUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.component = DaggerActivityComponent.builder()
                .applicationComponent(((BaseApplication) getApplication()).getComponent())
                .activityModule(new ActivityModule(this))
                .build();

        this.component.injectActivity(this);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    int index = 0;

    @OnClick(R.id.btn_click)
    public void clickme() {
        toastUtil.show(this, "index:" + index++);
    }

}